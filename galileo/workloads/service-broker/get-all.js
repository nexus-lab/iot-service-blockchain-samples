const Base = require('../base');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const serviceName = `service_${mspId}_${deviceId}`;

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_broker:GetAll',
      contractArguments: [mspId, deviceId, serviceName],
      timeout: 30,
      readOnly: true,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
