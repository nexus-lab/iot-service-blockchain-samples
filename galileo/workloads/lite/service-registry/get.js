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
    const serviceName = `service_${mspId}_${deviceId}_${serviceIndex}`;
    this.serviceIndex.set(`${mspId}_${identityName}`, serviceIndex + 1);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_registry:Get',
      contractArguments: [mspId, deviceId, serviceName],
      timeout: 30,
      readOnly: true,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
