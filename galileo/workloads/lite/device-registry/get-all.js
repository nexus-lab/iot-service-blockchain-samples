const Base = require('../base');

class Workload extends Base {
  // eslint-disable-next-line class-methods-use-this, no-unused-vars
  createNextTransaction(mspId, identityName) {
    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'device_registry:GetAll',
      contractArguments: [mspId],
      timeout: 30,
      readOnly: true,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
