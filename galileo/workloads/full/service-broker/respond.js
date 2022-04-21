const ServiceResponse =
  require('@nexus-lab/iot-service-blockchain/sdk/javascript/ServiceResponse').default;
const moment = require('@nexus-lab/iot-service-blockchain/sdk/javascript/moment').default;
const Base = require('../base');
const { uuid } = require('../helper');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const requestId = uuid(mspId, deviceId);
    const response = new ServiceResponse(requestId, moment(), 0, 'OK');

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_broker:Respond',
      contractArguments: [response.serialize()],
      timeout: 30,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
