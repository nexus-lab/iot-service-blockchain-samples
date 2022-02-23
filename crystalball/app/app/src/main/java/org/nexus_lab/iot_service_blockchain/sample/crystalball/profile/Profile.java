package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import androidx.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.Date;
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

    @Nullable
    private Date mScreenshotTime;

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

    void setServiceName(@Nullable String name) {
        mServiceName = name;
    }

    @Nullable
    public Integer getServiceVersion() {
        return mServiceVersion;
    }

    void setServiceVersion(@Nullable Integer version) {
        mServiceVersion = version;
    }

    @Nullable
    public String getServiceDescription() {
        return mServiceDescription;
    }

    void setServiceDescription(@Nullable String description) {
        mServiceDescription = description;
    }

    @Nullable
    public OffsetDateTime getServiceLastUpdateTime() {
        return mServiceLastUpdateTime;
    }

    void setServiceLastUpdateTime(@Nullable OffsetDateTime time) {
        mServiceLastUpdateTime = time;
    }

    @Nullable
    public String getOrganizationId() {
        return mOrganizationId;
    }

    void setOrganizationId(@Nullable String id) {
        mOrganizationId = id;
    }

    @Nullable
    public String getDeviceName() {
        return mDeviceName;
    }

    void setDeviceName(@Nullable String name) {
        mDeviceName = name;
    }

    @Nullable
    public String getDeviceId() {
        return mDeviceId;
    }

    void setDeviceId(@Nullable String id) {
        mDeviceId = id;
    }

    @Nullable
    public String getDeviceDescription() {
        return mDeviceDescription;
    }

    void setDeviceDescription(@Nullable String description) {
        mDeviceDescription = description;
    }

    @Nullable
    public OffsetDateTime getDeviceLastUpdateTime() {
        return mDeviceLastUpdateTime;
    }

    void setDeviceLastUpdateTime(@Nullable OffsetDateTime time) {
        mDeviceLastUpdateTime = time;
    }

    @Nullable
    public Date getScreenshotTime() {
        return mScreenshotTime;
    }

    void setScreenshotTime(@Nullable Date time) {
        mScreenshotTime = time;
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
                .setDeviceLastUpdateTime(getDeviceLastUpdateTime())
                .setScreenshotTime(getScreenshotTime());
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
                && Objects.equals(getDeviceLastUpdateTime(), profile.getDeviceLastUpdateTime())
                && Objects.equals(getScreenshotTime(), profile.getScreenshotTime());
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
                getDeviceLastUpdateTime(),
                getScreenshotTime()
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

        @Nullable
        private Date mScreenshotTime;

        public Builder setId(@Nullable String id) {
            mId = id;
            return this;
        }

        public Builder setServiceName(@Nullable String name) {
            mServiceName = name;
            return this;
        }

        public Builder setServiceVersion(@Nullable Integer version) {
            mServiceVersion = version;
            return this;
        }

        public Builder setServiceDescription(@Nullable String description) {
            mServiceDescription = description;
            return this;
        }

        public Builder setServiceLastUpdateTime(@Nullable OffsetDateTime time) {
            mServiceLastUpdateTime = time;
            return this;
        }

        public Builder setOrganizationId(@Nullable String id) {
            mOrganizationId = id;
            return this;
        }

        public Builder setDeviceName(@Nullable String name) {
            mDeviceName = name;
            return this;
        }

        public Builder setDeviceId(@Nullable String id) {
            mDeviceId = id;
            return this;
        }

        public Builder setDeviceDescription(@Nullable String description) {
            mDeviceDescription = description;
            return this;
        }

        public Builder setDeviceLastUpdateTime(@Nullable OffsetDateTime time) {
            mDeviceLastUpdateTime = time;
            return this;
        }

        public Builder setScreenshotTime(@Nullable Date time) {
            mScreenshotTime = time;
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
            profile.setScreenshotTime(mScreenshotTime);
            return profile;
        }
    }
}
