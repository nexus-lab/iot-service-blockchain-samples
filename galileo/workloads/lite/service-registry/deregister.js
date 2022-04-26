const Service = require('@nexus-lab/iot-service-blockchain/sdk/javascript/Service').default;
const Base = require('../base');

class Workload extends Base {
  constructor() {
    super();
    this.serviceIndex = new Map();
  }

  createNextTransaction(mspId, identityName) {
    let serviceIndex = this.serviceIndex.get(`${mspId}_${identityName}`);
    if (typeof serviceIndex === 'undefined') {
      serviceIndex = 0;
    }
    const deviceId = this.getDeviceId(mspId, identityName);
    const service = new Service(`service_${mspId}_${deviceId}_${serviceIndex}`, deviceId, mspId, 1);
    this.serviceIndex.set(`${mspId}_${identityName}`, serviceIndex + 1);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_registry:Deregister',
      contractArguments: [service.serialize()],
      timeout: 30,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
