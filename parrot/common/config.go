package common

import (
	"io/ioutil"

	"gopkg.in/yaml.v3"
)

type PicovoiceConfig struct {
	AssetDir  string `yaml:"asset_dir"`
	AccessKey string `yaml:"access_key"`
}

type IpfsConfig struct {
	RepoPath       string   `yaml:"repo_path"`
	SwarmKey       string   `yaml:"swarm_key"`
	SwarmAddresses []string `yaml:"swarm_addresses"`
	BootstrapPeers []string `yaml:"bootstrap_peers"`
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

type BlockchainServiceRef struct {
	OrganizationId string `yaml:"organization_id"`
	DeviceId       string `yaml:"device_id"`
	ServiceName    string `yaml:"service_name"`
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

func LoadConfigFromFile(configPath string, config interface{}) error {
	content, err := ioutil.ReadFile(configPath)
	if err != nil {
		return err
	}

	if err = yaml.Unmarshal(content, config); err != nil {
		return err
	}

	return nil
}
