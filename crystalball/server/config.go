package main

import (
	"io/ioutil"
	"path/filepath"

	"gopkg.in/yaml.v3"
)

type Config struct {
	Stream struct {
		Addr string `yaml:"addr"`
		Key  string `yaml:"key"`
	} `yaml:"stream"`
	Client struct {
		OrganizationId     string `yaml:"organization_id"`
		DeviceName         string `yaml:"device_name"`
		DeviceDescription  string `yaml:"device_description"`
		ServiceName        string `yaml:"service_name"`
		ServiceVersion     int32  `yaml:"service_version"`
		ServiceDescription string `yaml:"service_description"`
		Certificate        string `yaml:"certificate"`
		PrivateKey         string `yaml:"private_key"`
	} `yaml:"client"`
	Gateway struct {
		Endpoint       string `yaml:"endpoint"`
		ServerName     string `yaml:"server_name"`
		TlsCertificate string `yaml:"tls_certificate"`
	} `yaml:"gateway"`
	Network struct {
		Name      string `yaml:"name"`
		Chaincode string `yaml:"chaincode"`
	}
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
