# IoT Service Blockchain Benchmark Suite

Benchmarking IoT Service Blockchain smart contracts using Hyperledger Caliper.

## Testbed Setup

| Name                        | CPU (Cores) | Memory (MB) | Disk (GB) | IP           | OS             |
|-----------------------------|-------------|-------------|-----------|--------------|----------------|
| manager.caliper.example.com | 2           | 2048        | 40        | 172.16.0.101 | Ubuntu 20.04.4 |
| worker1.caliper.example.com | 2           | 2048        | 40        | 172.16.0.102 | Ubuntu 20.04.4 |
| worker2.caliper.example.com | 2           | 2048        | 40        | 172.16.0.103 | Ubuntu 20.04.4 |
| worker3.caliper.example.com | 2           | 2048        | 40        | 172.16.0.104 | Ubuntu 20.04.4 |
| worker4.caliper.example.com | 2           | 2048        | 40        | 172.16.0.105 | Ubuntu 20.04.4 |
| worker5.caliper.example.com | 2           | 2048        | 40        | 172.16.0.106 | Ubuntu 20.04.4 |

## Installation & Running

### Prerequisites

- Install [Node.js](https://nodejs.org) of version >= 14.13.1 and < 15 on **all Caliper nodes**.
- Install `make` and other build tools (`build-essential` package on Ubuntu) on **all Caliper
    nodes**.
- Install [Eclipse Mosquitto](http://mosquitto.org/) 2.0 on the **Caliper manager node**
    (`manager.caliper.example.com`).
- Ensure **every Caliper node** (`*.caliper.example.com`) can connect to each other and all
    Hyperledger Fabric nodes (`orderer*.example.com`, `peer*.org*.example.com`).
    A simple way is to achieve this is editting `/etc/hosts`:

    ```
    $ echo "172.16.0.2 orderer1.example.com" >> /etc/hosts
    $ echo "172.16.0.3 orderer2.example.com" >> /etc/hosts
    $ echo "172.16.0.4 orderer3.example.com" >> /etc/hosts
    $ echo "172.16.0.5 peer1.org1.example.com" >> /etc/hosts
    $ echo "172.16.0.6 peer2.org1.example.com" >> /etc/hosts
    $ echo "172.16.0.7 peer1.org2.example.com" >> /etc/hosts
    $ echo "172.16.0.8 peer2.org2.example.com" >> /etc/hosts

    $ echo "172.16.0.101 manager.caliper.example.com" >> /etc/hosts
    $ echo "172.16.0.102 worker1.caliper.example.com" >> /etc/hosts
    $ echo "172.16.0.103 worker2.caliper.example.com" >> /etc/hosts
    $ echo "172.16.0.104 worker3.caliper.example.com" >> /etc/hosts
    $ echo "172.16.0.105 worker4.caliper.example.com" >> /etc/hosts
    $ echo "172.16.0.106 worker5.caliper.example.com" >> /etc/hosts
    ```

### Install

1. Set up a multi-node Hyperledger Fabric testbed as instructed by [Gaia](../gaia/README.md).

1. Configure Mosquitto to listen on port 1883 and allow anonymous users on the **Caliper manager
    node** (`manager.caliper.example.com`).

    ```
    # /etc/mosquitto/conf.d/default.conf
    allow_anonymous true
    listener 1883
    ```
    
    Then, start the `mosquitto` systemd service:

    ```
    (manager.caliper.example.com) $ sudo systemctl restart mosquitto
    ```

1. Clone this repository to **all Caliper nodes** (`*.caliper.example.com`):

    ```
    $ git clone https://github.com/nexus-lab/iot-service-blockchain-samples.git
    ```

1. Copy `iot-service-blockchain-samples/gaia/crypto-config/` generated when setting up the testbed
    to **all Caliper nodes** (`*.caliper.example.com`) to the same location
    (`iot-service-blockchain-samples/gaia/crypto-config/`).

1. Change working directory to `iot-service-blockchain-samples/galileo` on **all Caliper nodes**
    (`*.caliper.example.com`).

    ```
    $ cd iot-service-blockchain-samples/galileo
    ```

1. Install Node.js dependencies on **all Caliper nodes** (`*.caliper.example.com`):

    ```
    $ npm install
    ```

1. Edit `connectors/connection-org1.yaml` and `connectors/connection-org1.yaml`, replace
    the `pem` fields with corresponding file content of
    `gaia/crypto-config/peerOrganizations/org*.example.com/peers/peer*.org*.example.com/tls/ca.crt`:

    ```yaml
    # connectors/connection-org1.yaml
    ---
    ...
    peers:
      peer1.org1.example.com:
        ...
        tlsCACerts:
          pem: |
            # replace with file content of gaia/crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/ca.crt
        ...
      peer2.org1.example.com:
        ...
        tlsCACerts:
          pem: |
            # replace with file content of gaia/crypto-config/peerOrganizations/org1.example.com/peers/peer2.org1.example.com/tls/ca.crt
        ...
    ...
    ```

    ```yaml
    # connectors/connection-org2.yaml
    ---
    ...
    peers:
      peer1.org2.example.com:
        ...
        tlsCACerts:
          pem: |
            # replace with file content of gaia/crypto-config/peerOrganizations/org2.example.com/peers/peer1.org2.example.com/tls/ca.crt
        ...
      peer2.org2.example.com:
        ...
        tlsCACerts:
          pem: |
            # replace with file content of gaia/crypto-config/peerOrganizations/org2.example.com/peers/peer2.org2.example.com/tls/ca.crt
        ...
    ...
    ```

### Run Benchmarks

1. First, start Caliper manager on the **Caliper manager node** (`manager.caliper.example.com`):

    ```
    (manager.caliper.example.com) $ npx caliper launch manager \
                                        --caliper-networkconfig connectors/manager.yaml \
                                        --caliper-benchconfig <benchmark config file>
    ```

    Replace the `<benchmark config file>` with the relative path to the `*.yaml` file under `benchmarks/` directory.

1. Then, start one Caliper worker on each **Caliper worker node** (`worker*.caliper.example.com`):

    ```
    (worker1.caliper.example.com) $ npx caliper launch worker \
                                    --caliper-networkconfig connectors/worker1.yaml \
                                    --caliper-benchconfig <benchmark config file>
    (worker2.caliper.example.com) $ npx caliper launch worker \
                                    --caliper-networkconfig connectors/worker2.yaml \
                                    --caliper-benchconfig <benchmark config file>
    (worker3.caliper.example.com) $ npx caliper launch worker \
                                    --caliper-networkconfig connectors/worker3.yaml \
                                    --caliper-benchconfig <benchmark config file>
    (worker4.caliper.example.com) $ npx caliper launch worker \
                                    --caliper-networkconfig connectors/worker4.yaml \
                                    --caliper-benchconfig <benchmark config file>
    (worker5.caliper.example.com) $ npx caliper launch worker \
                                    --caliper-networkconfig connectors/worker5.yaml \
                                    --caliper-benchconfig <benchmark config file>
    ```

    The `<benchmark config file>` has to be same as the value used in the previous step.

