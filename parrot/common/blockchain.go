package common

import (
	"context"
	"log"
	"sync"
	"time"

	bcommon "github.com/nexus-lab/iot-service-blockchain/common"
	blockchain "github.com/nexus-lab/iot-service-blockchain/sdk/go"
)

func NewBlockchainSdk(client BlockchainClientConfig, gateway BlockchainGatewayConfig, network BlockchainNetworkConfig) (*blockchain.Sdk, error) {
	return blockchain.NewSdk(
		&blockchain.SdkOptions{
			OrganizationId:            client.OrganizationId,
			Certificate:               []byte(client.Certificate),
			PrivateKey:                []byte(client.PrivateKey),
			GatewayPeerEndpoint:       gateway.Endpoint,
			GatewayPeerServerName:     gateway.ServerName,
			GatewayPeerTLSCertificate: []byte(gateway.TlsCertificate),
			NetworkName:               network.Name,
			ChaincodeId:               network.Chaincode,
		},
	)
}

func NewDevice(sdk *blockchain.Sdk, name, description string) *bcommon.Device {
	return &bcommon.Device{
		Id:             sdk.GetDeviceId(),
		OrganizationId: sdk.GetOrganizationId(),
		Name:           name,
		Description:    description,
		LastUpdateTime: time.Now(),
	}
}

func NewService(sdk *blockchain.Sdk, name, description string, version int32) *bcommon.Service {
	return &bcommon.Service{
		Name:           name,
		OrganizationId: sdk.GetOrganizationId(),
		DeviceId:       sdk.GetDeviceId(),
		Version:        version,
		Description:    description,
		LastUpdateTime: time.Now(),
	}
}

func NewRequest(requestId string, service bcommon.Service, method string, arguments []string) *bcommon.ServiceRequest {
	return &bcommon.ServiceRequest{
		Id:        requestId,
		Time:      time.Now(),
		Service:   service,
		Method:    method,
		Arguments: arguments,
	}
}

func NewResponse(requestId string, statusCode int32, returnValue string) *bcommon.ServiceResponse {
	return &bcommon.ServiceResponse{
		RequestId:   requestId,
		Time:        time.Now(),
		StatusCode:  statusCode,
		ReturnValue: returnValue,
	}
}

func RegisterOnBlockchain(sdk *blockchain.Sdk, device *bcommon.Device, service *bcommon.Service) error {
	deviceRegistry := sdk.GetDeviceRegistry()
	serviceRegistry := sdk.GetServiceRegistry()

	// register only when service or device does not exist
	if _, err := serviceRegistry.Get(service.OrganizationId, service.DeviceId, service.Name); err != nil {
		if _, err := deviceRegistry.Get(device.OrganizationId, device.Id); err != nil {
			if err = deviceRegistry.Register(device); err != nil {
				return err
			}
		}

		return serviceRegistry.Register(service)
	}

	return nil
}

type BlockchainRequestManager struct {
	lock                sync.Mutex
	requests            sync.Map
	Sdk                 *blockchain.Sdk
	ListenService       *BlockchainServiceRef
	RequestHandler      func(*bcommon.ServiceRequest)
	ResponseHandler     func(*bcommon.ServiceResponse)
	OnGarbageCollection func(string)
}

func (mgr *BlockchainRequestManager) Request(requestId string, service bcommon.Service, method string, arguments []string) (*bcommon.ServiceRequest, error) {
	mgr.lock.Lock()
	defer mgr.lock.Unlock()

	request := &bcommon.ServiceRequest{
		Id:        requestId,
		Time:      time.Now(),
		Service:   service,
		Method:    method,
		Arguments: arguments,
	}
	mgr.requests.Store(request.Id, request.Time)

	serviceBroker := mgr.Sdk.GetServiceBroker()
	if err := serviceBroker.Request(request); err != nil {
		mgr.requests.Delete(request.Id)
		return nil, err
	}

	log.Printf("request sent: [%s]%s %#v", request.Method, request.Id, request.Arguments)

	return request, nil
}

func (mgr *BlockchainRequestManager) Respond(requestId string, statusCode int32, returnValue string) (*bcommon.ServiceResponse, error) {
	serviceBroker := mgr.Sdk.GetServiceBroker()

	response := &bcommon.ServiceResponse{
		RequestId:   requestId,
		Time:        time.Now(),
		StatusCode:  statusCode,
		ReturnValue: returnValue,
	}
	err := serviceBroker.Respond(response)

	log.Printf("response sent [%d]%s %s", response.StatusCode, response.RequestId, response.ReturnValue)

	return response, err
}

func (mgr *BlockchainRequestManager) gc(ctx context.Context) {
	ticker := time.NewTicker(time.Minute)
	clear := func(key, value interface{}) bool {
		if time.Since(value.(time.Time)).Seconds() > 60 {
			log.Printf("timed out waiting for response of request %s", key)
			mgr.requests.Delete(key)

			if mgr.OnGarbageCollection != nil {
				mgr.OnGarbageCollection(key.(string))
			}
		}
		return true
	}

	for {
		select {
		case <-ticker.C:
			mgr.requests.Range(clear)
		case <-ctx.Done():
			return
		}
	}
}

func (mgr *BlockchainRequestManager) Listen(ctx context.Context) error {
	go mgr.gc(ctx)

	serviceBroker := mgr.Sdk.GetServiceBroker()

	events, cancel, err := serviceBroker.RegisterEvent()
	if err != nil {
		return err
	}
	defer cancel()

	log.Println("blockchain service started")
	log.Printf("\torganization id: %s", mgr.Sdk.GetOrganizationId())
	log.Printf("\tdevice id: %s", mgr.Sdk.GetDeviceId())

	for {
		select {
		case event := <-events:
			if event.Action == "request" {
				// only listen for requests sent to self
				if mgr.ListenService != nil && event.OrganizationId == mgr.ListenService.OrganizationId && event.DeviceId == mgr.ListenService.DeviceId && event.ServiceName == mgr.ListenService.ServiceName {
					request := event.Payload.(*bcommon.ServiceRequest)
					log.Printf("request received: [%s]%s %#v", request.Method, request.Id, request.Arguments)

					if mgr.RequestHandler != nil {
						go mgr.RequestHandler(request)
					}
				}
			} else if event.Action == "respond" {
				// only listen for responses of requests sent by self
				_, ok := mgr.requests.Load(event.RequestId)
				if !ok {
					continue
				}
				mgr.requests.Delete(event.RequestId)

				response := event.Payload.(*bcommon.ServiceResponse)
				log.Printf("response received: [%d]%s %s", response.StatusCode, response.RequestId, response.ReturnValue)

				if mgr.ResponseHandler != nil {
					go mgr.ResponseHandler(response)
				}
			}

		case <-ctx.Done():
			return nil
		}
	}
}
