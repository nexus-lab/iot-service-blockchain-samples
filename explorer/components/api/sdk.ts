import Sdk from '@nexus-lab/iot-service-blockchain/sdk/javascript/Sdk';
import * as fs from 'fs';
import * as yaml from 'js-yaml';
import * as path from 'path';

interface Config {
  client: {
    organization_id: string;
    certificate: string;
    private_key: string;
  };
  gateway: {
    endpoint: string;
    server_name: string;
    tls_certificate: string;
  };
  network: {
    name: string;
    chaincode: string;
  };
}

const config = yaml.load(
  fs.readFileSync(path.join(process.cwd(), 'config.yml'), { encoding: 'utf-8' })
) as Config;

const sdk = new Sdk({
  organizationId: config.client.organization_id,
  certificate: config.client.certificate,
  privateKey: config.client.private_key,
  gatewayPeerEndpoint: config.gateway.endpoint,
  gatewayPeerServerName: config.gateway.server_name,
  gatewayPeerTLSCertificate: config.gateway.tls_certificate,
  networkName: config.network.name,
  chaincodeId: config.network.chaincode,
});

export default sdk;
