package main

import (
	"io/ioutil"
	"log"
	"os"
	"path/filepath"
	"time"

	"github.com/google/uuid"
	"github.com/nexus-lab/iot-service-blockchain/common"
	sdk "github.com/nexus-lab/iot-service-blockchain/sdk/go"
)

const (
	ORG_ID        = "Org2MSP"
	ORG_DOMAIN    = "org2.example.com"
	USER_NAME     = "User1@org2.example.com"
	PEER_NAME     = "peer0.org2.example.com"
	PEER_ENDPOINT = "localhost:9051"
)

func getCredentials() ([]byte, []byte, []byte) {
	root := filepath.Join(os.Getenv("FABRIC_ROOT"), "test-network/organizations/peerOrganizations/", ORG_DOMAIN)
	filepaths := []string{
		"users/" + USER_NAME + "/msp/signcerts/cert.pem",
		"users/" + USER_NAME + "/msp/keystore/priv_sk",
		"peers/" + PEER_NAME + "/tls/ca.crt",
	}

	files := make([][]byte, 0)
	for _, path := range filepaths {
		data, err := ioutil.ReadFile(filepath.Join(root, path))
		if err != nil {
			log.Fatal(err)
		}
		files = append(files, data)
	}

	return files[0], files[1], files[2]
}

func main() {
	certificate, privateKey, tlsCertificate := getCredentials()

	isb, err := sdk.NewSdk(
		&sdk.SdkOptions{
			OrganizationId:            ORG_ID,
			Certificate:               certificate,
			PrivateKey:                privateKey,
			GatewayPeerEndpoint:       PEER_ENDPOINT,
			GatewayPeerServerName:     PEER_NAME,
			GatewayPeerTLSCertificate: tlsCertificate,
			NetworkName:               "mychannel",
			ChaincodeId:               "iotservice",
		},
	)
	if err != nil {
		log.Fatal(err)
	}

	log.Printf("Organization ID is %s\n", isb.GetOrganizationId())
	log.Printf("Client ID is %s\n", isb.GetDeviceId())

	err = isb.GetServiceBroker().Request(
		&common.ServiceRequest{
			Id:   uuid.NewString(),
			Time: time.Now(),
			Service: common.Service{
				Name:           "streaming",
				DeviceId:       "eDUwOTo6Q049dXNlcjEsQz1VUyxTVD1Ob3J0aCBDYXJvbGluYSxPPUh5cGVybGVkZ2VyLE9VPWNsaWVudDo6Q049Y2Eub3JnMS5leGFtcGxlLmNvbSxDPVVTLEw9RHVyaGFtLFNUPU5vcnRoIENhcm9saW5hLE89b3JnMS5leGFtcGxlLmNvbQ==",
				OrganizationId: "Org1MSP",
			},
			Method:    "GET",
			Arguments: make([]string, 0),
		},
	)
	if err != nil {
		log.Fatal(err)
	}

	isb.Close()
}
