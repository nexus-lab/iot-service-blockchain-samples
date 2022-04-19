function renderOneIdentity(orgIndex, workerIndex, identityIndex, identitiesPerOrg) {
  return `        - name: 'User${identityIndex}'
          clientPrivateKey:
            path: '../gaia/crypto-config/peerOrganizations/org${orgIndex}.example.com/users/User${
    workerIndex * identitiesPerOrg + identityIndex
  }@org${orgIndex}.example.com/msp/keystore/priv_sk'
          clientSignedCert:
            path: '../gaia/crypto-config/peerOrganizations/org${orgIndex}.example.com/users/User${
    workerIndex * identitiesPerOrg + identityIndex
  }@org${orgIndex}.example.com/msp/signcerts/User${
    workerIndex * identitiesPerOrg + identityIndex
  }@org${orgIndex}.example.com-cert.pem'`;
}

function renderOrganization(mspIndex, workerIndex, identitiesPerOrg) {
  const identities = new Array(identitiesPerOrg)
    .fill(0)
    .map((_, i) => renderOneIdentity(mspIndex, workerIndex, i + 1, identitiesPerOrg))
    .join('\n');

  return (
    `  - mspid: Org${mspIndex}MSP
    connectionProfile:
      path: './connectors/connection-org${mspIndex}.yaml'
      discover: true
    identities:
      certificates:
` + identities
  );
}

function render(workerIndex, orgCount, identitiesPerWorker) {
  const organizations = new Array(orgCount)
    .fill(0)
    .map((_, i) => renderOrganization(i + 1, workerIndex, identitiesPerWorker / orgCount))
    .join('\n');
  return (
    `name: Galileo Worker ${workerIndex + 1}
version: '2.0.0'

caliper:
  blockchain: fabric

channels:
  - channelName: mychannel
    contracts:
      - id: iotservice

organizations:
` + organizations
  );
}

if (process.argv.length !== 5) {
  console.error('Usage: node ./generate.js <number_of_orgs> <identites_per_worker> <worker_index>');
} else {
  console.log(
    render(parseInt(process.argv[4]) - 1, parseInt(process.argv[2]), parseInt(process.argv[3]))
  );
}
