package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Settings {
    @Nullable
    private ClientSettings mClient;
    @Nullable
    private GatewaySettings mGateway;
    @Nullable
    private NetworkSettings mNetwork;

    Settings() {
    }

    @Nullable
    public ClientSettings getClient() {
        return mClient;
    }

    void setClient(@Nullable ClientSettings client) {
        mClient = client;
    }

    @Nullable
    public GatewaySettings getGateway() {
        return mGateway;
    }

    void setGateway(@Nullable GatewaySettings gateway) {
        mGateway = gateway;
    }

    @Nullable
    public NetworkSettings getNetwork() {
        return mNetwork;
    }

    void setNetwork(@Nullable NetworkSettings network) {
        mNetwork = network;
    }

    public Builder asBuilder() {
        Builder builder = new Builder();
        if (getClient() != null) {
            builder.setClient(getClient().asBuilder());
        }
        if (getGateway() != null) {
            builder.setGateway(getGateway().asBuilder());
        }
        if (getNetwork() != null) {
            builder.setNetwork(getNetwork().asBuilder());
        }
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Settings that = (Settings) o;
        return Objects.equals(getClient(), that.getClient())
                && Objects.equals(getGateway(), that.getGateway())
                && Objects.equals(getNetwork(), that.getNetwork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClient(), getGateway(), getNetwork());
    }

    public static class Builder {
        @Nullable
        private ClientSettings.Builder mClient;
        @Nullable
        private GatewaySettings.Builder mGateway;
        @Nullable
        private NetworkSettings.Builder mNetwork;

        public Builder setClient(@Nullable ClientSettings.Builder client) {
            mClient = client;
            return this;
        }

        public Builder setGateway(@Nullable GatewaySettings.Builder gateway) {
            mGateway = gateway;
            return this;
        }

        public Builder setNetwork(@Nullable NetworkSettings.Builder network) {
            mNetwork = network;
            return this;
        }

        public Settings build() {
            Settings settings = new Settings();
            if (mClient != null) {
                settings.setClient(mClient.build());
            }
            if (mGateway != null) {
                settings.setGateway(mGateway.build());
            }
            if (mNetwork != null) {
                settings.setNetwork(mNetwork.build());
            }
            return settings;
        }
    }
}
