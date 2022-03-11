package main

import "github.com/nexus-lab/iot-service-blockchain-samples/parrot/common"

type Config struct {
	Device  common.BlockchainDeviceConfig  `yaml:"device"`
	Service common.BlockchainServiceConfig `yaml:"service"`
	Client  common.BlockchainClientConfig  `yaml:"client"`
	Gateway common.BlockchainGatewayConfig `yaml:"gateway"`
	Network common.BlockchainNetworkConfig `yaml:"network"`
}
