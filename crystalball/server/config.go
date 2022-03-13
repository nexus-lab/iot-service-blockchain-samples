package main

import (
	"io/ioutil"
	"path/filepath"

	"gopkg.in/yaml.v3"
)

type StreamConfig struct {
	ListenAddr   string `yaml:"listen_addr"`
	ExternalAddr string `yaml:"external_addr"`
	Key          string `yaml:"key"`
}

type BlockchainClientConfig struct {
	OrganizationId string `yaml:"organization_id"`
	Certificate    string `yaml:"certificate"`
	PrivateKey     string `yaml:"private_key"`
}

type BlockchainDeviceConfig struct {
	DeviceName        string `yaml:"device_name"`
	DeviceDescription string `yaml:"device_description"`
}

type BlockchainServiceConfig struct {
	ServiceName        string `yaml:"service_name"`
	ServiceVersion     int32  `yaml:"service_version"`
	ServiceDescription string `yaml:"service_description"`
}

type BlockchainGatewayConfig struct {
	Endpoint       string `yaml:"endpoint"`
	ServerName     string `yaml:"server_name"`
	TlsCertificate string `yaml:"tls_certificate"`
}

type BlockchainNetworkConfig struct {
	Name      string `yaml:"name"`
	Chaincode string `yaml:"chaincode"`
}

type Config struct {
	Stream  StreamConfig            `yaml:"stream"`
	Device  BlockchainDeviceConfig  `yaml:"device"`
	Service BlockchainServiceConfig `yaml:"service"`
	Client  BlockchainClientConfig  `yaml:"client"`
	Gateway BlockchainGatewayConfig `yaml:"gateway"`
	Network BlockchainNetworkConfig `yaml:"network"`
}

func LoadConfigFromFile(configPath string) (*Config, error) {
	absConfigPath, err := filepath.Abs(configPath)
	if err != nil {
		return nil, err
	}

	content, err := ioutil.ReadFile(absConfigPath)
	if err != nil {
		return nil, err
	}

	config := &Config{}
	if err = yaml.Unmarshal(content, config); err != nil {
		return nil, err
	}

	return config, nil
}
