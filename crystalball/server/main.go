package main

import (
	"os"
	"sync"

	"github.com/sirupsen/logrus"
)

func main() {
	configPath := "./config.yml"
	if len(os.Args) > 1 {
		configPath = os.Args[1]
	}

	config, err := LoadConfigFromFile(configPath)
	if err != nil {
		logrus.Fatalf("failed to load configuration from file %s: %s", configPath, err.Error())
	}

	tokenStore := new(TokenStore)
	waitGroup := new(sync.WaitGroup)
	waitGroup.Add(2)

	go func() {
		var err error
		var blockchainService *BlockchainService

		defer blockchainService.Close()
		defer waitGroup.Done()

		if blockchainService, err = NewBlockchainService(config, tokenStore); err == nil {
			logrus.Info("blockchain service started")

			err = blockchainService.Serve()
		}
		if err != nil {
			logrus.Fatal("blockchain service exited with error: ", err)
		}
	}()

	go func() {
		defer waitGroup.Done()

		rtmpServer := NewRtmpServer(config.Stream.ListenAddr, config.Stream.Key, tokenStore)
		logrus.Infof("streaming server address: rtmp://%s/?key=%s", config.Stream.ExternalAddr, config.Stream.Key)

		if err := rtmpServer.ListenAndServe(); err != nil {
			logrus.Fatal("streaming server exited with error: ", err)
		}
	}()

	waitGroup.Wait()
}
