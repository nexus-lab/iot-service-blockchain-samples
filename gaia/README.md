# Multi-Node Hyperledger Fabric Network

The testbed setup scripts.

> Adapted from [hyperledger/fabric-samples/test-network-nano-bash](https://github.com/hyperledger/fabric-samples/tree/daf08981dd0cca6aa35cf5c4b31de3b7a42bb7c1/test-network-nano-bash).

## Testbed Setup

Here is a minimal sample of the testbed configuration:

| Name                   | CPU (Cores) | Memory (MB) | Disk (GB) | IP         | OS             |
|------------------------|-------------|-------------|-----------|------------|----------------|
| orderer1.example.com   | 2           | 4096        | 40        | 172.16.0.2 | Ubuntu 20.04.4 |
| orderer2.example.com   | 2           | 4096        | 40        | 172.16.0.3 | Ubuntu 20.04.4 |
| orderer3.example.com   | 2           | 4096        | 40        | 172.16.0.4 | Ubuntu 20.04.4 |
| peer1.org1.example.com | 2           | 4096        | 40        | 172.16.0.5 | Ubuntu 20.04.4 |
| peer2.org1.example.com | 2           | 4096        | 40        | 172.16.0.6 | Ubuntu 20.04.4 |
| peer1.org2.example.com | 2           | 4096        | 40        | 172.16.0.7 | Ubuntu 20.04.4 |
| peer2.org2.example.com | 2           | 4096        | 40        | 172.16.0.8 | Ubuntu 20.04.4 |

Here is the directory structure after following the installation instructions:

```
.
└── iot-service-blockchain-samples
    ├── bin
    ├── config
    ├── ...
    ├── gaia
    │   ├── channel-artifacts
    │   ├── crypto-config
    │   ├── explorer
    │   └── iot-service-blockchain
    │       ├── chaincode
    │       └── ...
    └── ...
```

## Installation & Running

### Prerequisites

- Seven virtual or baremetal machines configured as shown in the [Testbed Setup](#testbed-setup)
    section
- Install [Go](https://go.dev/) 1.16 or newer on **all peer nodes** (`peer*.org*.example.com`) and
    add it to your `PATH` environment variable.
- Install [Docker](https://www.docker.com/) on **all peer nodes** (`peer*.org*.example.com`), add
    your user to the `docker` user group, and reboot your machine to make changes take effect.
- Ensure that each node can connect to others using the hostname `*.example.com`.
    A simple way to do this is editting `/etc/hosts`:

    ```
    echo "172.16.0.2 orderer1.example.com" >> /etc/hosts
    echo "172.16.0.3 orderer2.example.com" >> /etc/hosts
    echo "172.16.0.4 orderer3.example.com" >> /etc/hosts
    echo "172.16.0.5 peer1.org1.example.com" >> /etc/hosts
    echo "172.16.0.6 peer2.org1.example.com" >> /etc/hosts
    echo "172.16.0.7 peer1.org2.example.com" >> /etc/hosts
    echo "172.16.0.8 peer2.org2.example.com" >> /etc/hosts
    ```

### Set up the network

1. Clone this repository to your **local machine** as well as **all nodes** (`*.example.com`) and
    change working directory to `iot-service-blockchain-samples`:

    ```
    $ git clone https://github.com/nexus-lab/iot-service-blockchain-samples.git
    $ cd iot-service-blockchain-samples
    ```

1. Download Fabric binaries to your **local machine** and **all nodes** (`*.example.com`):

    ```
    $ curl -sSL https://bit.ly/2ysbOFE | bash -s -- 2.4.3 1.5.2 -s -d
    ```

1. Change your directory to `gaia` on your **local machine** and **all nodes** (`*.example.com`):

    ```
    $ cd gaia
    ```

1. On your **local machine**, generate private keys and certificates using the following command:

    ```
    (local) $ ./generate_artifacts.sh
    ```

1. Copy `channel-artifacts` and `crypto-config` to the `gaia` directory on **all nodes**.

1. Run `orderer1`, `orderer2`, and `orderer3` on `orderer1.example.com`, `orderer2.example.com`,
    and `orderer3.example.com` respectively:

    ```
    (orderer1.example.com) $ ./orderer1
    (orderer2.example.com) $ ./orderer2
    (orderer3.example.com) $ ./orderer3
    ```

1. Run `peer1.org1`, `peer2.org1`, `peer1.org2`, and `peer2.org2` on `peer1.org1.example.com`,
    `peer2.org1.example.com`, `peer1.org2.example.com`, and `peer2.org2.example.com` respectively:

    ```
    (peer1.org1.example.com) $ ./peer1.org1
    (peer2.org1.example.com) $ ./peer2.org1
    (peer1.org2.example.com) $ ./peer1.org2
    (peer2.org2.example.com) $ ./peer2.org2
    ```

1. Run `admin.peer1.org1` on `peer1.org1.example.com` and copy `channel-artifacts/mychannel.block` 
    to the same location on **other peer nodes** (`peer*.org*.example.com`):

    ```
    (peer1.org1.example.com) $ ./admin.peer1.org1
    ```

1. Run `admin.peer2.org1`, `admin.peer1.org2`, and `admin.peer2.org2` on `peer2.org1.example.com`,
    `peer1.org2.example.com`, and `peer2.org2.example.com` respectively:

    ```
    (peer2.org1.example.com) $ ./admin.peer2.org1
    (peer1.org2.example.com) $ ./admin.peer1.org2
    (peer2.org2.example.com) $ ./admin.peer2.org2
    ```

### Install chaincode

1. Open a new terminal for **each peer node** (`peer*.org*.example.com`) and set up environment 
    variables in each terminal:

    ```
    (peer1.org1.example.com) $ source admin.peer1.org1 >/dev/null 2>&1
    (peer2.org1.example.com) $ source admin.peer2.org1 >/dev/null 2>&1
    (peer1.org2.example.com) $ source admin.peer1.org2 >/dev/null 2>&1
    (peer2.org2.example.com) $ source admin.peer2.org2 >/dev/null 2>&1
    ```

1. Under `gaia`, clone the [IoT Service Blockchain](https://github.com/nexus-lab/iot-service-blockchain)
    repository to **all peer nodes** (peer*.org*.example.com):

    ```
    (peer*.org*.example.com) $ git clone https://github.com/nexus-lab/iot-service-blockchain.git
    ```

1. In the terminal of each peer node, package and install chaincode:

    ```
    (peer*.org*.example.com) $ peer lifecycle chaincode package iotservice.tar.gz \
                                    --path ./iot-service-blockchain/chaincode \
                                    --lang golang \
                                    --label iotservice_1
    (peer*.org*.example.com) $ peer lifecycle chaincode install iotservice.tar.gz
    ```

    Note the chaincode package identifier returned by the second command, expose it as an
    environment variable in the terminal, respectively:

    ```
    (peer*.org*.example.com) $ export CHAINCODE_ID=iotservice_1:...
    ```

1. From the terminals of peer node 1 of organization 1 and 2 (`peer1.org*.example.com`), approve
    the installed chaincode:

    ```
    (peer1.org1.example.com) $ peer lifecycle chaincode approveformyorg \
                                    -o orderer1.example.com:7050 \
                                    --channelID mychannel \
                                    --name iotservice \
                                    --version 1 \
                                    --sequence 1 \
                                    --package-id $CHAINCODE_ID \
                                    --tls \
                                    --cafile ${PWD}/crypto-config/ordererOrganizations/example.com/orderers/orderer1.example.com/tls/ca.crt
    (peer1.org2.example.com) $ peer lifecycle chaincode approveformyorg \
                                    -o orderer2.example.com:7050 \
                                    --channelID mychannel \
                                    --name iotservice \
                                    --version 1 \
                                    --sequence 1 \
                                    --package-id $CHAINCODE_ID \
                                    --tls \
                                    --cafile ${PWD}/crypto-config/ordererOrganizations/example.com/orderers/orderer2.example.com/tls/ca.crt
    ```

    You can use the following command to check if the chaincode has been approved by both
    organizations:

    ```
    (peer1.org1.example.com) $ peer lifecycle chaincode checkcommitreadiness \
                                    --channelID mychannel \
                                    --name iotservice \
                                    --version 1 \
                                    --sequence 1 \
                                    --output json
    ```

1. Finally, when the chaincode is approved by both organizations, run the following command to
    commit the chaincode definition to the channel on `peer1.org1.example.com`:

    ```
    (peer1.org1.example.com) $ peer lifecycle chaincode commit \
                                    -o orderer1.example.com:7050 \
                                    --channelID mychannel \
                                    --name iotservice \
                                    --version 1 \
                                    --sequence 1 \
                                    --tls \
                                    --cafile "${PWD}"/crypto-config/ordererOrganizations/example.com/orderers/orderer1.example.com/tls/ca.crt \
                                    --peerAddresses peer1.org1.example.com:7051 \
                                    --tlsRootCertFiles ${PWD}/crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/ca.crt \
                                    --peerAddresses peer1.org2.example.com:7051 \
                                    --tlsRootCertFiles ${PWD}/crypto-config/peerOrganizations/org2.example.com/peers/peer1.org2.example.com/tls/ca.crt
    ```

1. You can use the following query on **any peer node** (`peer*.org*.example.com`) to check if the
    chaincode is successfully installed and activated:

    ```
    (peer*.org*.example.com) $ peer chaincode query \
                                    -C mychannel \
                                    -n iotservice \
                                    -c '{"Args":["device_registry:getAll","Org1MSP"]}'
    ```

### Run blockchain explorer (Optional)

1. To run the blockchain explorer, first install [Docker](https://www.docker.com/) and 
    [Docker Compose](https://docs.docker.com/compose/) on your **local machine**.

1. Change directory to `explorer` under the `gaia` project:

    ```
    (local)  $ cd explorer
    ```

1. Start blockchain explorer using the following command:

    ```
    (local)  $ docker-compose up -d
    ```
    
    You can change the `extra_hosts` settings of the `explorer` service if the external IP addresses
    of the nodes are different from the default setup.

1. Finally, open [http://localhost:8080](http://localhost:8080) from your browser and use the
    username `exploreradmin` and password `exploreradminpw` to sign into the blockchain explorer.

### Clean up the network

To clean up the network, simply stop all running scripts and delete the `data` directory under
`iot-service-blockchain-samples/gaia` on **all nodes** (`*.example.com`).
