package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.DialogServiceEditBinding;

import java.util.Objects;

public class ServiceEditDialogBuilder implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    private final AlertDialog.Builder builder;
    private final DialogServiceEditBinding binding;
    private Listener listener;
    public ServiceEditDialogBuilder(Context context) {
        this.binding = DialogServiceEditBinding.inflate(LayoutInflater.from(context));
        this.builder = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.action_cancel, this)
                .setPositiveButton(R.string.action_save, this)
                .setOnCancelListener(this);
        setIsCreating(false);
    }

    public ServiceEditDialogBuilder setOrganizationId(String organizationId) {
        binding.organizationId.setText(organizationId);
        return this;
    }

    public ServiceEditDialogBuilder setDeviceId(String deviceId) {
        binding.deviceId.setText(deviceId);
        return this;
    }

    public ServiceEditDialogBuilder setServiceName(String serviceName) {
        binding.serviceName.setText(serviceName);
        return this;
    }

    public ServiceEditDialogBuilder setIsCreating(boolean isCreating) {
        this.builder.setTitle(isCreating ? R.string.title_service_add : R.string.title_service_edit);
        return this;
    }

    public ServiceEditDialogBuilder setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public AlertDialog create() {
        return this.builder.create();
    }

    private Result getResult() {
        Result result = new Result();
        result.organizationId = Objects.requireNonNull(binding.organizationId.getText()).toString();
        result.deviceId = Objects.requireNonNull(binding.deviceId.getText()).toString();
        result.serviceName = Objects.requireNonNull(binding.serviceName.getText()).toString();
        return result;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (this.listener != null) {
            this.listener.onCancel();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (this.listener != null) {
                    this.listener.onSave(getResult());
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (this.listener != null) {
                    this.listener.onCancel();
                }
                break;
        }
    }

    public interface Listener {
        void onSave(Result result);

        void onCancel();
    }

    public static class Result {
        String organizationId;
        String deviceId;
        String serviceName;
    }
}
