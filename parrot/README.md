# Parrot

A smart voice control system built on the IoT Service Blockchain.

## System Architecture & Components

- **Actuators**

  Actuators are IoT devices connected to the IoT Service Blockchain.
  They listen to service requests on blockchain and interact with their environment based on the
  request content.
  For example, an actuator can be a smart light which can switch itself on and off based on
  the service request argument.

  _Source code: [actuator](actuator/)_

- **AI**

  The AI component is the central hub for receiving voice commands sent by voice assistants on IoT
  Service Blockchain and translating them into user intents using speech-to-intent machine learning
  engine.
  It listens for voice command service request on blockchain and fetch audio data from IPFS.

  _Source code: [ai](ai/)_

- **Assistant**

  Voice assistants are IoT devices that listen for and record user voice commands.
  To achieve better energy efficiency and user privacy, it uses wake word detection to only respond
  to voice commands that start with a specific wake word, such as "Hey Google" or "Hi Alexa".
  Then, it determine the duration of the command with voice activity detection.
  A voice assistant must feature a microphone to record audio.
  The recorded audio will be sent to the AI component via IoT Service Blockchain and IPFS.

  _Source code: [assistant](assistant/)_

## Installation & Running

### Prerequisites

- A running [Hyperledger Fabric](https://www.hyperledger.org/use/fabric) network of version 2.4 or
    newer
- Install [IoT Service Blockchain](https://github.com/nexus-lab/iot-service-blockchain) chaincode
    to your Hyperledger Fabric network
- [Raspberry Pi 4](https://www.raspberrypi.com/) and/or a Linux workstation
- [Go](https://go.dev/) 1.16 or newer
- GCC and build tools
- A microphone
- A [Picovoice](https://picovoice.ai/) access key
- Clone this repository, change working directory to `parrot`, and install all Go dependencies

    ```
    $ git clone https://github.com/nexus-lab/iot-service-blockchain-samples.git
    $ cd iot-service-blockchain-samples/parrot
    $ go mod download
    ```

### Actuators

1. Change working directory to `actuator`:

    ```
    $ cd actuator
    ```

1. Copy `config.yml.example` to `config.yml`.
    Fill in or change configurations according to your Hyperledger Fabric configurations.
    Configuration details are given in the comments.

    ```
    $ cp config.yml.example config.yml
    ```

1. Run the following command to start the actuator:

    ```
    $ go run *.go --config config.yml
    2022/03/12 14:02:06 blockchain service started
    2022/03/12 14:02:06     organization id: Org2MSP
    2022/03/12 14:02:06     device id: eDUwOTo6Q049dXNlcjEsQz1VUyxTVD1Ob3J0aCBDYXJvbGluYSxPPUh5cGVybGVkZ2VyLE9VPWNsaWVudDo6Q049Y2Eub3JnMi5leGFtcGxlLmNvbSxDPVVLLEw9SHVyc2xleSxTVD1IYW1wc2hpcmUsTz1vcmcyLmV4YW1wbGUuY29t
    ```

    Take note of the device ID in the output.
    This ID, along with the organization ID and service name, will be used in the configuration of
    AI component.
    Additionally, you may run multiple instances of actuator using different configuration files.

### AI

1. Change the working directory to `ai`:

    ```
    $ cd ../ai
    ```

1. Copy `config.yml.example` to `config.yml`.
    Fill in or change configurations according to your IPFS, Picovoice, and Hyperledger Fabric
    configurations.
    Configuration details are given in the comments.

    ```
    $ cp config.yml.example config.yml
    ```

1. Run the following command to start the AI component:

    ```
    $ go run *.go --config config.yml
    2022/03/12 14:22:05 ipfs node QmWncbyxnmGv8b6KPsnPY7rLg1eLdS9zGxsM4Z7WpwgTV2 started, addresses are
    2022/03/12 14:22:05     /ip4/127.0.0.1/tcp/14001/p2p/QmWncbyxnmGv8b6KPsnPY7rLg1eLdS9zGxsM4Z7WpwgTV2
    2022/03/12 14:22:05     /ip6/::1/tcp/14001/p2p/QmWncbyxnmGv8b6KPsnPY7rLg1eLdS9zGxsM4Z7WpwgTV2
    2022/03/12 14:22:07 blockchain service started
    2022/03/12 14:22:07     organization id: Org2MSP
    2022/03/12 14:22:07     device id: eDUwOTo6Q049dXNlcjEsQz1VUyxTVD1Ob3J0aCBDYXJvbGluYSxPPUh5cGVybGVkZ2VyLE9VPWNsaWVudDo6Q049Y2Eub3JnMi5leGFtcGxlLmNvbSxDPVVLLEw9SHVyc2xleSxTVD1IYW1wc2hpcmUsTz1vcmcyLmV4YW1wbGUuY29t
    ```

    Take note of the IPFS addresses and the device ID in the output.
    You will need them to configure the assistant.

### Assistant

1. Make sure your microphone is connected and set as the default input.

1. Change the working directory to `ai`:

    ```
    $ cd ../assistant
    ```

1. Copy `config.yml.example` to `config.yml`.
    Fill in or change configurations according to your IPFS, Picovoice, and Hyperledger Fabric
    configurations.
    Configuration details are given in the comments.

    ```
    $ cp config.yml.example config.yml
    ```

1. Run the following command to start the assistant:

    ```
    $ go run *.go --config config.yml
    2022/03/12 14:37:25 ipfs node QmWzZvSsshPYxYPbBxaKnU9z9ZZTZWkjEVHTekmCYiF5wg started, addresses are
    2022/03/12 14:37:25     /ip4/127.0.0.1/tcp/24001/p2p/QmWzZvSsshPYxYPbBxaKnU9z9ZZTZWkjEVHTekmCYiF5wg
    2022/03/12 14:37:25     /ip6/::1/tcp/24001/p2p/QmWzZvSsshPYxYPbBxaKnU9z9ZZTZWkjEVHTekmCYiF5wg
    2022/03/12 14:37:25 Wake word detection started
    2022/03/12 14:37:25 Voice activity detection started
    2022/03/12 14:37:25 blockchain service started
    2022/03/12 14:37:25     organization id: Org1MSP
    2022/03/12 14:37:25     device id: eDUwOTo6Q049dXNlcjEsQz1VUyxTVD1Ob3J0aCBDYXJvbGluYSxPPUh5cGVybGVkZ2VyLE9VPWNsaWVudDo6Q049Y2Eub3JnMS5leGFtcGxlLmNvbSxDPVVTLEw9RHVyaGFtLFNUPU5vcnRoIENhcm9saW5hLE89b3JnMS5leGFtcGxlLmNvbQ==
    ```

1. Say any of the following commands to your microphone.
    You should get a result from the blockchain in a few seconds.

    ```
    Parrot, turn on/off the kitchen/living room/bedroom light.
    Parrot, switch on/off the kitchen/living room/bedroom light.
    ```

    Here is an example of the assistant's output:

    ```
    2022/03/12 14:44:08 Recording started
    2022/03/12 14:44:09 Recording stopped
    2022/03/12 14:44:09 Audio saved to /ipfs/QmRkbRGKL1mTxKAmm3TMmFi1P8RHeG9ujRm3Jb66CeK65y
    2022/03/12 14:44:11 request sent: [PUT]5eeb4804-67bd-4865-a60c-b97c6c30eca1 []string{"/ipfs/QmRkbRGKL1mTxKAmm3TMmFi1P8RHeG9ujRm3Jb66CeK65y"}
    2022/03/12 14:44:18 response received: [0]5eeb4804-67bd-4865-a60c-b97c6c30eca1 state is on
    ```
