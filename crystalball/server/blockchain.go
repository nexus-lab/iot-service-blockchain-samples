package main

import (
	"fmt"
	"time"

	"github.com/nexus-lab/iot-service-blockchain/common"
	isb "github.com/nexus-lab/iot-service-blockchain/sdk/go"
	"github.com/sirupsen/logrus"
)

type BlockchainService struct {
	Sdk                *isb.Sdk
	TokenStore         *TokenStore
	StreamingAddr      string
	OrganizationId     string
	DeviceId           string
	DeviceName         string
	DeviceDescription  string
	ServiceName        string
	ServiceVersion     int32
	ServiceDescription string
}

func (s *BlockchainService) prepare() error {
	deviceRegistry := s.Sdk.GetDeviceRegistry()
	serviceRegistry := s.Sdk.GetServiceRegistry()

	// check if service or device exists
	if _, err := serviceRegistry.Get(s.OrganizationId, s.DeviceId, s.ServiceName); err != nil {
		if _, err := deviceRegistry.Get(s.OrganizationId, s.DeviceId); err != nil {
			err = deviceRegistry.Register(
				&common.Device{
					Id:             s.DeviceId,
					OrganizationId: s.OrganizationId,
					Name:           s.DeviceName,
					Description:    s.DeviceDescription,
					LastUpdateTime: time.Now(),
				},
			)

			if err != nil {
				logrus.Error("failed to register device: ", err)
				return err
			}
		}

		err = serviceRegistry.Register(
			&common.Service{
				Name:           s.ServiceName,
				OrganizationId: s.OrganizationId,
				DeviceId:       s.DeviceId,
				Version:        s.ServiceVersion,
				Description:    s.ServiceDescription,
				LastUpdateTime: time.Now(),
			},
		)

		if err != nil {
			logrus.Error("failed to register service: ", err)
			return err
		}
	}

	return nil
}

func (s *BlockchainService) Serve() error {
	deviceId := s.Sdk.GetDeviceId()
	organizationId := s.Sdk.GetOrganizationId()
	serviceBroker := s.Sdk.GetServiceBroker()

	events, _, err := serviceBroker.RegisterEvent()
	if err != nil {
		logrus.Error("failed to listen for blockchain events: ", err)
		return err
	}

	for event := range events {
		if event.Action != "request" || event.OrganizationId != organizationId || event.DeviceId != deviceId || event.ServiceName != s.ServiceName {
			continue
		}

		var statusCode int32 = 0
		var returnValue = ""
		request := event.Payload.(*common.ServiceRequest)

		logrus.Infof("received request: [%s] %s", request.Method, request.Id)

		if request.Method != "GET" {
			statusCode = 1
		} else {
			token := s.TokenStore.Issue()
			returnValue = fmt.Sprintf("rtmp://%s/?token=%s", s.StreamingAddr, token)
		}

		serviceBroker.Respond(
			&common.ServiceResponse{
				RequestId:   request.Id,
				Time:        time.Now(),
				StatusCode:  statusCode,
				ReturnValue: returnValue,
			},
		)
	}

	return nil
}

func (s *BlockchainService) Close() error {
	if s.Sdk == nil {
		return nil
	}
	return s.Sdk.Close()
}

func NewBlockchainService(config *Config, tokenStore *TokenStore) (*BlockchainService, error) {
	sdk, err := isb.NewSdk(
		&isb.SdkOptions{
			OrganizationId:            config.Client.OrganizationId,
			Certificate:               []byte(config.Client.Certificate),
			PrivateKey:                []byte(config.Client.PrivateKey),
			GatewayPeerEndpoint:       config.Gateway.Endpoint,
			GatewayPeerServerName:     config.Gateway.ServerName,
			GatewayPeerTLSCertificate: []byte(config.Gateway.TlsCertificate),
			NetworkName:               config.Network.Name,
			ChaincodeId:               config.Network.Chaincode,
		},
	)
	if err != nil {
		return nil, err
	}

	service := &BlockchainService{
		Sdk:                sdk,
		TokenStore:         tokenStore,
		StreamingAddr:      config.Stream.ExternalAddr,
		OrganizationId:     sdk.GetOrganizationId(),
		DeviceId:           sdk.GetDeviceId(),
		DeviceName:         config.Device.DeviceName,
		DeviceDescription:  config.Device.DeviceDescription,
		ServiceName:        config.Service.ServiceName,
		ServiceVersion:     config.Service.ServiceVersion,
		ServiceDescription: config.Service.ServiceDescription,
	}

	if err = service.prepare(); err != nil {
		return nil, err
	}
	return service, nil
}
