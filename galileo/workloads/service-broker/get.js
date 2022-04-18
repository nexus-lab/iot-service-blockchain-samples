const Base = require('../base');
const { uuid } = require('../helper');

class Workload extends Base {
  // eslint-disable-next-line class-methods-use-this
  createNextTransaction(mspId, identityName) {
    const requestId = uuid(mspId, identityName);

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
