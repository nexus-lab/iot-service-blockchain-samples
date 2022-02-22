package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import androidx.annotation.Nullable;

import java.util.Objects;

public class GatewaySettings {
    @Nullable
    private String mEndpoint;

    @Nullable
    private String mServerName;

    @Nullable
    private String mTlsCertificate;

    GatewaySettings() {
    }

    @Nullable
    public String getEndpoint() {
        return mEndpoint;
    }

    void setEndpoint(@Nullable String endpoint) {
        mEndpoint = endpoint;
    }

    @Nullable
    public String getServerName() {
        return mServerName;
    }

    void setServerName(@Nullable String serverName) {
        mServerName = serverName;
    }

    @Nullable
    public String getTlsCertificate() {
        return mTlsCertificate;
    }

    void setTlsCertificate(@Nullable String tlsCertificate) {
        mTlsCertificate = tlsCertificate;
    }

    public Builder asBuilder() {
        return new Builder()
                .setEndpoint(getEndpoint())
                .setServerName(getServerName())
                .setTlsCertificate(getTlsCertificate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GatewaySettings that = (GatewaySettings) o;
        return Objects.equals(getEndpoint(), that.getEndpoint())
                && Objects.equals(getServerName(), that.getServerName())
                && Objects.equals(getTlsCertificate(), that.getTlsCertificate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpoint(), getServerName(), getTlsCertificate());
    }

    public static class Builder {
        @Nullable
        private String mEndpoint;

        @Nullable
        private String mServerName;

        @Nullable
        private String mTlsCertificate;

        public Builder setEndpoint(@Nullable String endpoint) {
            mEndpoint = endpoint;
            return this;
        }

        public Builder setServerName(@Nullable String serverName) {
            mServerName = serverName;
            return this;
        }

        public Builder setTlsCertificate(@Nullable String certificate) {
            mTlsCertificate = certificate;
            return this;
        }

        public GatewaySettings build() {
            GatewaySettings settings = new GatewaySettings();
            settings.setEndpoint(mEndpoint);
            settings.setServerName(mServerName);
            settings.setTlsCertificate(mTlsCertificate);
            return settings;
        }
    }
}
