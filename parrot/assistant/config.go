package main

import "github.com/nexus-lab/iot-service-blockchain-samples/parrot/common"

type Config struct {
	Picovoice common.PicovoiceConfig         `yaml:"picovoice"`
	Ipfs      common.IpfsConfig              `yaml:"ipfs"`
	AiService common.BlockchainServiceRef    `yaml:"ai_service"`
	Client    common.BlockchainClientConfig  `yaml:"client"`
	Gateway   common.BlockchainGatewayConfig `yaml:"gateway"`
	Network   common.BlockchainNetworkConfig `yaml:"network"`
}
