package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ClientSettings {
    @Nullable
    private String mOrganizationId;

    @Nullable
    private String mCertificate;

    @Nullable
    private String mPrivateKey;

    ClientSettings() {
    }

    @Nullable
    public String getOrganizationId() {
        return mOrganizationId;
    }

    void setOrganizationId(@Nullable String organizationId) {
        mOrganizationId = organizationId;
    }

    @Nullable
    public String getCertificate() {
        return mCertificate;
    }

    void setCertificate(@Nullable String certificate) {
        mCertificate = certificate;
    }

    @Nullable
    public String getPrivateKey() {
        return mPrivateKey;
    }

    void setPrivateKey(@Nullable String privateKey) {
        mPrivateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientSettings that = (ClientSettings) o;
        return Objects.equals(getOrganizationId(), that.getOrganizationId())
                && Objects.equals(getCertificate(), that.getCertificate())
                && Objects.equals(getPrivateKey(), that.getPrivateKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganizationId(), getCertificate(), getPrivateKey());
    }

    public Builder asBuilder() {
        return new Builder()
                .setOrganizationId(getOrganizationId())
                .setCertificate(getCertificate())
                .setPrivateKey(getPrivateKey());
    }

    public static class Builder {
        @Nullable
        private String mOrganizationId;

        @Nullable
        private String mCertificate;

        @Nullable
        private String mPrivateKey;

        public Builder setOrganizationId(@Nullable String organizationId) {
            mOrganizationId = organizationId;
            return this;
        }

        public Builder setCertificate(@Nullable String certificate) {
            mCertificate = certificate;
            return this;
        }

        public Builder setPrivateKey(@Nullable String privateKey) {
            mPrivateKey = privateKey;
            return this;
        }

        public ClientSettings build() {
            ClientSettings settings = new ClientSettings();
            settings.setOrganizationId(mOrganizationId);
            settings.setCertificate(mCertificate);
            settings.setPrivateKey(mPrivateKey);
            return settings;
        }
    }
}
