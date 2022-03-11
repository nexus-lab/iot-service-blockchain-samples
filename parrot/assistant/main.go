package main

import (
	"context"
	"flag"
	"log"
	"sync"

	"github.com/google/uuid"
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
	wg.Add(5)

	// ===================== IPFS ==========================
	ipfs, err := common.CreateIpfsNode(ctx, config.Ipfs.RepoPath, config.Ipfs.SwarmKey, config.Ipfs.SwarmAddresses)
	if err != nil {
		log.Panic(err)
	}

	if err = common.ConnectIpfsPeers(ctx, ipfs, config.Ipfs.BootstrapPeers); err != nil {
		log.Panic(err)
	}

	// ===================== Wake Word Detection ==========================
	wakeWordNotifier := make(chan interface{})
	go func() {
		defer wg.Done()

		if err := detectWakeWord(ctx, config.Picovoice.AssetDir, config.Picovoice.AccessKey, wakeWordNotifier); err != nil {
			log.Panic(err)
		}
	}()

	// ===================== Voice Activity Detection ==========================
	voiceActivityNotifier := make(chan bool)
	go func() {
		defer wg.Done()

		if err := detectVoiceActivity(ctx, voiceActivityNotifier); err != nil {
			log.Panic(err)
		}
	}()

	// ===================== Voice Recorder ==========================
	recordNotifier := make(chan string)
	go func() {
		defer wg.Done()

		if err := recordAudio(ctx, ipfs, wakeWordNotifier, voiceActivityNotifier, recordNotifier); err != nil {
			log.Panic(err)
		}
	}()

	// ===================== Blockchain Service ==========================
	blockchainSdk, err := common.NewBlockchainSdk(config.Client, config.Gateway, config.Network)
	if err != nil {
		log.Panic(err)
	}
	blockchainManager := &common.BlockchainRequestManager{Sdk: blockchainSdk}

	go func() {
		defer wg.Done()

		aiService := bcommon.Service{
			Name:           config.AiService.ServiceName,
			DeviceId:       config.AiService.DeviceId,
			OrganizationId: config.AiService.OrganizationId,
		}
		for {
			select {
			case audioPath := <-recordNotifier:
				if _, err := blockchainManager.Request(uuid.NewString(), aiService, "PUT", []string{audioPath}); err != nil {
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
