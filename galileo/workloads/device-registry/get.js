const Base = require('../base');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'device_registry:Get',
      contractArguments: [mspId, deviceId],
      timeout: 30,
      readOnly: true,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
