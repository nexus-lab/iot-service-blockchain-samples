const Device = require('@nexus-lab/iot-service-blockchain/sdk/javascript/Device').default;
const Base = require('../base');

class Workload extends Base {
  createNextTransaction(mspId, identityName) {
    const deviceId = this.getDeviceId(mspId, identityName);
    const device = new Device(deviceId, mspId, `device_${mspId}_${deviceId}`);

    return {
      contractId: 'iotservice',
      contractVersion: 'v1',
      contractFunction: 'device_registry:Register',
      contractArguments: [device.serialize()],
      timeout: 30,
    };
  }
}

module.exports.createWorkloadModule = () => new Workload();
