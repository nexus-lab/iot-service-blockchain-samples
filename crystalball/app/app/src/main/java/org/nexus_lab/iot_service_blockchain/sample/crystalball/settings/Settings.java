package org.nexus_lab.iot_service_blockchain.sample.crystalball.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.owlike.genson.Genson;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.App;

import java.io.InputStream;

public class Settings {
    private final static Genson GENSON = new Genson();
    private final static String PREFERENCE_NAME = "settings";
    @Nullable
    private ClientSettings client;
    @Nullable
    private GatewaySettings gateway;
    @Nullable
    private NetworkSettings network;

    public static Settings deserialize(InputStream input) {
        return GENSON.deserialize(input, Settings.class);
    }

    public static Settings deserialize(String input) {
        return GENSON.deserialize(input, Settings.class);
    }

    public static Settings load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.PREFERENCE_DEFAULT_NAME, Context.MODE_PRIVATE);
        String raw = preferences.getString(PREFERENCE_NAME, "");
        if (raw.isEmpty()) {
            return new Settings();
        }
        return deserialize(raw);
    }

    @Nullable
    public ClientSettings getClient() {
        return client;
    }

    public void setClient(@Nullable ClientSettings client) {
        this.client = client;
    }

    @Nullable
    public GatewaySettings getGateway() {
        return gateway;
    }

    public void setGateway(@Nullable GatewaySettings gateway) {
        this.gateway = gateway;
    }

    @Nullable
    public NetworkSettings getNetwork() {
        return network;
    }

    public void setNetwork(@Nullable NetworkSettings network) {
        this.network = network;
    }

    public void validate() throws InvalidSettingsException {
        if (client == null || gateway == null || network == null) {
            throw new InvalidSettingsException();
        }
        client.validate();
        gateway.validate();
        network.validate();
    }

    public String serialize() {
        return GENSON.serialize(this);
    }

    public void save(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.PREFERENCE_DEFAULT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_NAME, serialize());
        editor.apply();
    }

    public static class InvalidSettingsException extends Exception {
    }

    public static class ClientSettings {
        @Nullable
        private String organizationId;

        @Nullable
        private String certificate;

        @Nullable
        private String privateKey;

        @Nullable
        public String getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(@Nullable String organizationId) {
            this.organizationId = organizationId;
        }

        @Nullable
        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(@Nullable String certificate) {
            this.certificate = certificate;
        }

        @Nullable
        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(@Nullable String privateKey) {
            this.privateKey = privateKey;
        }

        public void validate() throws InvalidSettingsException {
            if (organizationId == null || certificate == null || privateKey == null) {
                throw new InvalidSettingsException();
            }
        }
    }

    public static class GatewaySettings {
        @Nullable
        private String endpoint;

        @Nullable
        private String serverName;

        @Nullable
        private String tlsCertificate;

        @Nullable
        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(@Nullable String endpoint) {
            this.endpoint = endpoint;
        }

        @Nullable
        public String getServerName() {
            return serverName;
        }

        public void setServerName(@Nullable String serverName) {
            this.serverName = serverName;
        }

        @Nullable
        public String getTlsCertificate() {
            return tlsCertificate;
        }

        public void setTlsCertificate(@Nullable String tlsCertificate) {
            this.tlsCertificate = tlsCertificate;
        }

        public void validate() throws InvalidSettingsException {
            if (endpoint == null || serverName == null || tlsCertificate == null) {
                throw new InvalidSettingsException();
            }
        }
    }

    public static class NetworkSettings {
        @Nullable
        private String name;

        @Nullable
        private String chaincode;

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getChaincode() {
            return chaincode;
        }

        public void setChaincode(@Nullable String chaincode) {
            this.chaincode = chaincode;
        }

        public void validate() throws InvalidSettingsException {
            if (name == null || chaincode == null) {
                throw new InvalidSettingsException();
            }
        }
    }
}
