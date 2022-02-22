package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import androidx.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Profile {
    @Nullable
    private String mId;

    @Nullable
    private String mServiceName;

    @Nullable
    private Integer mServiceVersion;

    @Nullable
    private String mServiceDescription;

    @Nullable
    private OffsetDateTime mServiceLastUpdateTime;

    @Nullable
    private String mOrganizationId;

    @Nullable
    private String mDeviceName;

    @Nullable
    private String mDeviceId;

    @Nullable
    private String mDeviceDescription;

    @Nullable
    private OffsetDateTime mDeviceLastUpdateTime;

    Profile() {
    }

    @Nullable
    public String getId() {
        return mId;
    }

    void setId(@Nullable String id) {
        mId = id;
    }

    @Nullable
    public String getServiceName() {
        return mServiceName;
    }

    void setServiceName(@Nullable String serviceName) {
        mServiceName = serviceName;
    }

    @Nullable
    public Integer getServiceVersion() {
        return mServiceVersion;
    }

    void setServiceVersion(@Nullable Integer serviceVersion) {
        mServiceVersion = serviceVersion;
    }

    @Nullable
    public String getServiceDescription() {
        return mServiceDescription;
    }

    void setServiceDescription(@Nullable String serviceDescription) {
        mServiceDescription = serviceDescription;
    }

    @Nullable
    public OffsetDateTime getServiceLastUpdateTime() {
        return mServiceLastUpdateTime;
    }

    void setServiceLastUpdateTime(@Nullable OffsetDateTime serviceLastUpdateTime) {
        mServiceLastUpdateTime = serviceLastUpdateTime;
    }

    @Nullable
    public String getOrganizationId() {
        return mOrganizationId;
    }

    void setOrganizationId(@Nullable String organizationId) {
        mOrganizationId = organizationId;
    }

    @Nullable
    public String getDeviceName() {
        return mDeviceName;
    }

    void setDeviceName(@Nullable String deviceName) {
        mDeviceName = deviceName;
    }

    @Nullable
    public String getDeviceId() {
        return mDeviceId;
    }

    void setDeviceId(@Nullable String deviceId) {
        mDeviceId = deviceId;
    }

    @Nullable
    public String getDeviceDescription() {
        return mDeviceDescription;
    }

    void setDeviceDescription(@Nullable String deviceDescription) {
        mDeviceDescription = deviceDescription;
    }

    @Nullable
    public OffsetDateTime getDeviceLastUpdateTime() {
        return mDeviceLastUpdateTime;
    }

    void setDeviceLastUpdateTime(@Nullable OffsetDateTime deviceLastUpdateTime) {
        mDeviceLastUpdateTime = deviceLastUpdateTime;
    }

    public Builder asBuilder() {
        return new Builder()
                .setId(getId())
                .setServiceName(getServiceName())
                .setServiceVersion(getServiceVersion())
                .setServiceDescription(getServiceDescription())
                .setServiceLastUpdateTime(getServiceLastUpdateTime())
                .setOrganizationId(getOrganizationId())
                .setDeviceId(getDeviceId())
                .setDeviceName(getDeviceName())
                .setDeviceDescription(getDeviceDescription())
                .setDeviceLastUpdateTime(getDeviceLastUpdateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profile profile = (Profile) o;
        return Objects.equals(getId(), profile.getId())
                && Objects.equals(getServiceName(), profile.getServiceName())
                && Objects.equals(getServiceVersion(), profile.getServiceVersion())
                && Objects.equals(getServiceDescription(), profile.getServiceDescription())
                && Objects.equals(getServiceLastUpdateTime(), profile.getServiceLastUpdateTime())
                && Objects.equals(getOrganizationId(), profile.getOrganizationId())
                && Objects.equals(getDeviceName(), profile.getDeviceName())
                && Objects.equals(getDeviceId(), profile.getDeviceId())
                && Objects.equals(getDeviceDescription(), profile.getDeviceDescription())
                && Objects.equals(getDeviceLastUpdateTime(), profile.getDeviceLastUpdateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getServiceName(),
                getServiceVersion(),
                getServiceDescription(),
                getServiceLastUpdateTime(),
                getOrganizationId(),
                getDeviceName(),
                getDeviceId(),
                getDeviceDescription(),
                getDeviceLastUpdateTime()
        );
    }

    public static class Builder {
        @Nullable
        private String mId = UUID.randomUUID().toString();

        @Nullable
        private String mServiceName;

        @Nullable
        private Integer mServiceVersion;

        @Nullable
        private String mServiceDescription;

        @Nullable
        private OffsetDateTime mServiceLastUpdateTime;

        @Nullable
        private String mOrganizationId;

        @Nullable
        private String mDeviceName;

        @Nullable
        private String mDeviceId;

        @Nullable
        private String mDeviceDescription;

        @Nullable
        private OffsetDateTime mDeviceLastUpdateTime;

        public Builder setId(@Nullable String id) {
            mId = id;
            return this;
        }

        public Builder setServiceName(@Nullable String serviceName) {
            mServiceName = serviceName;
            return this;
        }

        public Builder setServiceVersion(@Nullable Integer serviceVersion) {
            mServiceVersion = serviceVersion;
            return this;
        }

        public Builder setServiceDescription(@Nullable String serviceDescription) {
            mServiceDescription = serviceDescription;
            return this;
        }

        public Builder setServiceLastUpdateTime(@Nullable OffsetDateTime serviceLastUpdateTime) {
            mServiceLastUpdateTime = serviceLastUpdateTime;
            return this;
        }

        public Builder setOrganizationId(@Nullable String organizationId) {
            mOrganizationId = organizationId;
            return this;
        }

        public Builder setDeviceName(@Nullable String deviceName) {
            mDeviceName = deviceName;
            return this;
        }

        public Builder setDeviceId(@Nullable String deviceId) {
            mDeviceId = deviceId;
            return this;
        }

        public Builder setDeviceDescription(@Nullable String deviceDescription) {
            mDeviceDescription = deviceDescription;
            return this;
        }

        public Builder setDeviceLastUpdateTime(@Nullable OffsetDateTime deviceLastUpdateTime) {
            mDeviceLastUpdateTime = deviceLastUpdateTime;
            return this;
        }

        public Profile build() {
            Profile profile = new Profile();
            profile.setId(mId);
            profile.setServiceName(mServiceName);
            profile.setServiceVersion(mServiceVersion);
            profile.setServiceDescription(mServiceDescription);
            profile.setServiceLastUpdateTime(mServiceLastUpdateTime);
            profile.setOrganizationId(mOrganizationId);
            profile.setDeviceId(mDeviceId);
            profile.setDeviceName(mDeviceName);
            profile.setDeviceDescription(mDeviceDescription);
            profile.setDeviceLastUpdateTime(mDeviceLastUpdateTime);
            return profile;
        }
    }
}
