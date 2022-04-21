const { WorkloadModuleBase } = require('@hyperledger/caliper-core');
const {
  getClientId,
  parseCertificate,
} = require('@nexus-lab/iot-service-blockchain/sdk/javascript/identity');

class Base extends WorkloadModuleBase {
  constructor() {
    super();
    this.txIndex = 0;
    this.deviceIdByIdentity = new Map();
  }

  async initializeWorkloadModule(
    workerIndex,
    totalWorkers,
    roundIndex,
    roundArguments,
    sutAdapter,
    sutContext
  ) {
    super.initializeWorkloadModule(
      workerIndex,
      totalWorkers,
      roundIndex,
      roundArguments,
      sutAdapter,
      sutContext
    );

    sutAdapter.gatewayInstanceByIdentity.forEach((gateway, alias) => {
      const { certificate } = gateway.identity.credentials;
      const deviceId = getClientId(parseCertificate(certificate));
      this.deviceIdByIdentity.set(alias, deviceId);
    });
  }

  getDeviceId(mspId, identityName) {
    const identityAlias =
      this.sutAdapter.connectorConfiguration.getAliasNameForOrganizationAndIdentityName(
        mspId,
        identityName
      );
    return this.deviceIdByIdentity.get(identityAlias);
  }

  // eslint-disable-next-line class-methods-use-this, no-unused-vars
  createNextTransaction(mspId, identityName) {
    throw new Error('Base.createTransaction() must be implemented in derived class');
  }

  async submitTransaction() {
    const mspId = `Org${(this.txIndex % 2) + 1}MSP`;
    const identityName = `User1`;

    const tx = this.createNextTransaction(mspId, identityName);
    this.sutAdapter.sendRequests({
      invokerMspId: mspId,
      invokerIdentity: identityName,
      ...tx,
    });

    this.txIndex++;
  }
}

module.exports = Base;
