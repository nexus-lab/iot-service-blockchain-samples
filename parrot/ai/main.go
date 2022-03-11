package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"sync"

	"github.com/google/uuid"
	files "github.com/ipfs/go-ipfs-files"
	icorepath "github.com/ipfs/interface-go-ipfs-core/path"
	"github.com/nexus-lab/iot-service-blockchain-samples/parrot/common"
	bcommon "github.com/nexus-lab/iot-service-blockchain/common"
)

func main() {
	// ===================== Configuration ==========================
	configPath := flag.String("config", "config.yml", "Path to the configuration file")
	flag.Parse()

	config := &Config{}
	if err := common.LoadConfigFromFile(*configPath, config); err != nil {
		log.Panic(err)
	}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	wg := new(sync.WaitGroup)
	wg.Add(3)
	defer wg.Wait()

	// ===================== IPFS ==========================
	ipfs, err := common.CreateIpfsNode(ctx, config.Ipfs.RepoPath, config.Ipfs.SwarmKey, config.Ipfs.SwarmAddresses)
	if err != nil {
		log.Panic(err)
	}

	// ===================== Blockchain Service ==========================
	actuatorVoiceAssistantRequestMap := new(sync.Map)
	incomingRequestQueue := make(chan *bcommon.ServiceRequest)
	outgoingRequestQueue := make(chan *bcommon.ServiceRequest)
	outgoingResponseQueue := make(chan *bcommon.ServiceResponse)

	blockchainSdk, err := common.NewBlockchainSdk(config.Client, config.Gateway, config.Network)
	if err != nil {
		log.Panic(err)
	}

	blockchainManager := &common.BlockchainRequestManager{
		Sdk: blockchainSdk,
		ListenService: &common.BlockchainServiceRef{
			OrganizationId: blockchainSdk.GetOrganizationId(),
			DeviceId:       blockchainSdk.GetDeviceId(),
			ServiceName:    config.Service.ServiceName,
		},
		RequestHandler: func(request *bcommon.ServiceRequest) {
			if request.Method == "PUT" && len(request.Arguments) == 1 {
				incomingRequestQueue <- request
			} else {
				outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_REQUEST, "request method or arguments are invalid")
			}
		},
		ResponseHandler: func(response *bcommon.ServiceResponse) {
			// passing actuator response to voice assistant
			requestId, ok := actuatorVoiceAssistantRequestMap.Load(response.RequestId)
			if !ok {
				log.Printf("actuator request %s is not found or the response timed out", response.RequestId)
			} else {
				actuatorVoiceAssistantRequestMap.Delete(response.RequestId)
				outgoingResponseQueue <- common.NewResponse(requestId.(string), response.StatusCode, response.ReturnValue)
			}
		},
		OnGarbageCollection: func(requestId string) {
			actuatorVoiceAssistantRequestMap.Delete(requestId)
		},
	}

	device := common.NewDevice(blockchainSdk, config.Device.DeviceName, config.Device.DeviceDescription)
	service := common.NewService(blockchainSdk, config.Service.ServiceName, config.Service.ServiceDescription, config.Service.ServiceVersion)
	if err := common.RegisterOnBlockchain(blockchainSdk, device, service); err != nil {
		log.Panic(err)
	}

	go func() {
		defer wg.Done()

		// use the same select{} so we send request and response in serial
		for {
			select {
			case request := <-outgoingRequestQueue:
				if _, err := blockchainManager.Request(request.Id, request.Service, request.Method, request.Arguments); err != nil {
					log.Panic(err)
				}
			case response := <-outgoingResponseQueue:
				if _, err := blockchainManager.Respond(response.RequestId, response.StatusCode, response.ReturnValue); err != nil {
					log.Panic(err)
				}
			case <-ctx.Done():
				return
			}
		}
	}()

	go func() {
		defer wg.Done()

		if err := blockchainManager.Listen(ctx); err != nil {
			log.Panic(err)
		}
	}()

	// ===================== Speech to Intent ==========================
	go func() {
		defer wg.Done()

		for {
			select {
			case request := <-incomingRequestQueue:
				audioPath := request.Arguments[0]
				audioFile, err := ipfs.Unixfs().Get(ctx, icorepath.New(audioPath))
				if err != nil {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_AUDIO, err.Error())
					continue
				}

				if _, ok := audioFile.(files.File); !ok {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_AUDIO, "audio file is not a regular file")
					continue
				}

				intent, err := runSpeechToIntent(ctx, config.Picovoice.AssetDir, config.Picovoice.AccessKey, audioFile.(files.File))
				if err != nil {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INFERENCE_FAILED, err.Error())
					continue
				}
				audioFile.Close()

				actuatorName, ok := intent.Slots["location"]
				if !ok {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_VOICE_COMMAND, "missing actuator location")
					continue
				}
				actuatorState := intent.Slots["state"]
				if !ok {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_VOICE_COMMAND, "missing actuator state")
					continue
				}
				actuatorService, ok := config.Actuators[actuatorName]
				if !ok {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_VOICE_COMMAND, fmt.Sprintf("actuator %s is not found", actuatorName))
					continue
				}

				actuatorRequest := common.NewRequest(
					uuid.NewString(),
					bcommon.Service{
						Name:           actuatorService.ServiceName,
						OrganizationId: actuatorService.OrganizationId,
						DeviceId:       actuatorService.DeviceId,
					},
					"PUT",
					[]string{actuatorState},
				)

				// associate actuator request to voice assistant request
				actuatorVoiceAssistantRequestMap.Store(actuatorRequest.Id, request.Id)

				outgoingRequestQueue <- actuatorRequest

			case <-ctx.Done():
				return
			}
		}
	}()

	wg.Wait()
}
