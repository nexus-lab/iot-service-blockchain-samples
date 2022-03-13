# IoT Service Blockchain Explorer

Explore registered devices, services, and service requests/responses on IoT Service Blockchain.

<img src="./assets/screenshot.png" alt="Screenshot" style="max-width:800px" />

## Installation & Running

1. Install [Node.js](https://nodejs.org/) and [Yarn](https://classic.yarnpkg.com/).

1. Clone this repository and change working directory to `explorer`:

    ```shell
    git clone https://github.com/nexus-lab/iot-service-blockchain-samples.git
    cd iot-service-blockchain-samples/explorer
    ```

1. Install dependencies:

    ```shell
    yarn
    ```

1. Copy `config.yml.example` to `config.yml`.
    Fill in or change configurations according to your Hyperledger Fabric configurations.
    Configuration details are given in the comments.

    ```shell
    cp config.yml.example config.yml
    ```

1. Build and run this project:

    ```shell
    yarn build
    yarn start
    ```
