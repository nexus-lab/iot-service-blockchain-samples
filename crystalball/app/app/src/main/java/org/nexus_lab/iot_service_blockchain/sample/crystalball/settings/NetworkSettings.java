package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import androidx.annotation.Nullable;

import java.util.Objects;

public class NetworkSettings {
    @Nullable
    private String mName;

    @Nullable
    private String mChaincode;

    NetworkSettings() {
    }

    @Nullable
    public String getName() {
        return mName;
    }

    void setName(@Nullable String name) {
        mName = name;
    }

    @Nullable
    public String getChaincode() {
        return mChaincode;
    }

    void setChaincode(@Nullable String chaincode) {
        mChaincode = chaincode;
    }

    public Builder asBuilder() {
        return new Builder()
                .setName(getName())
                .setChaincode(getChaincode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkSettings that = (NetworkSettings) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getChaincode(), that.getChaincode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getChaincode());
    }

    public static class Builder {
        @Nullable
        private String mName;

        @Nullable
        private String mChaincode;

        public Builder setName(@Nullable String name) {
            mName = name;
            return this;
        }

        public Builder setChaincode(@Nullable String chaincode) {
            mChaincode = chaincode;
            return this;
        }

        public NetworkSettings build() {
            NetworkSettings settings = new NetworkSettings();
            settings.setName(mName);
            settings.setChaincode(mChaincode);
            return settings;
        }
    }
}
