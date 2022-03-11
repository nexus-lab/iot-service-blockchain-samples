package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"sync"
	"time"

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
	wg.Add(2)

	// ===================== Blockchain Service ==========================
	lastRequestTime := time.Unix(0, 0)
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
			if request.Time.Sub(lastRequestTime) > 0 {
				if request.Method == "PUT" && len(request.Arguments) == 1 {
					// in actual IoT device, the device will act according to the request arguments
					// but now we simply print out the arguments
					log.Printf("change actuator state to: %s", request.Arguments[0])
					lastRequestTime = request.Time
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_OK, fmt.Sprintf("state is %s", request.Arguments[0]))
				} else {
					outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_REQUEST, "request method or arguments are invalid")
				}
			} else {
				outgoingResponseQueue <- common.NewResponse(request.Id, common.STATUS_INVALID_REQUEST, "request is outdated")
			}
		},
	}

	device := common.NewDevice(blockchainSdk, config.Device.DeviceName, config.Device.DeviceDescription)
	service := common.NewService(blockchainSdk, config.Service.ServiceName, config.Service.ServiceDescription, config.Service.ServiceVersion)
	if err := common.RegisterOnBlockchain(blockchainSdk, device, service); err != nil {
		log.Panic(err)
	}

	go func() {
		defer wg.Done()

		for {
			select {
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

	wg.Wait()
}
