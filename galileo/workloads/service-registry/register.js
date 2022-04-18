const Service = require('@nexus-lab/iot-service-blockchain/sdk/javascript/Service').default;
const Base = require('../base');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const service = new Service(`service_${mspId}_${identityName}`, deviceId, mspId, 1);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_registry:Register',
      contractArguments: [service.serialize()],
      timeout: 30,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
