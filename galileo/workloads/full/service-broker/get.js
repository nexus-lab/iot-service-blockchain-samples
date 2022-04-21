const Base = require('../base');
const { uuid } = require('../helper');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const requestId = uuid(mspId, deviceId);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'service_broker:Get',
      contractArguments: [requestId],
      timeout: 30,
      readOnly: true,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
