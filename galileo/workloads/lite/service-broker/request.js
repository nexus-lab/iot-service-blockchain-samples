const Service = require('@nexus-lab/iot-service-blockchain/sdk/javascript/Service').default;
const ServiceRequest =
  require('@nexus-lab/iot-service-blockchain/sdk/javascript/ServiceRequest').default;
const moment = require('@nexus-lab/iot-service-blockchain/sdk/javascript/moment').default;
const Base = require('../base');
const { uuid } = require('../helper');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const service = new Service(`service_${mspId}_${deviceId}_${this.txIndex}`, deviceId, mspId, 1);
    const requestId = uuid(mspId, deviceId, this.txIndex);
    const request = new ServiceRequest(requestId, moment(), service, 'GET', [mspId, deviceId]);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_broker:Request',
      contractArguments: [request.serialize()],
      timeout: 30,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
