package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.R;
import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.DialogServiceEditBinding;

import java.util.Objects;

public class ProfileEditorBuilder implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    private final AlertDialog.Builder mBuilder;
    private final DialogServiceEditBinding mViewBinding;
    private Listener mListener;

    public ProfileEditorBuilder(Context context) {
        mViewBinding = DialogServiceEditBinding.inflate(LayoutInflater.from(context));
        mBuilder = new MaterialAlertDialogBuilder(context)
                .setView(mViewBinding.getRoot())
                .setNegativeButton(R.string.action_cancel, this)
                .setPositiveButton(R.string.action_save, this)
                .setOnCancelListener(this);
    }

    public ProfileEditorBuilder setOrganizationId(String organizationId) {
        mViewBinding.organizationId.setText(organizationId);
        return this;
    }

    public ProfileEditorBuilder setDeviceId(String deviceId) {
        mViewBinding.deviceId.setText(deviceId);
        return this;
    }

    public ProfileEditorBuilder setServiceName(String serviceName) {
        mViewBinding.serviceName.setText(serviceName);
        return this;
    }

    public ProfileEditorBuilder setTitle(String title) {
        mBuilder.setTitle(title);
        return this;
    }

    public ProfileEditorBuilder setOnActionListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public AlertDialog create() {
        return this.mBuilder.create();
    }

    private Profile getResult() {
        return new Profile.Builder()
                .setOrganizationId(Objects.requireNonNull(mViewBinding.organizationId.getText()).toString())
                .setDeviceId(Objects.requireNonNull(mViewBinding.deviceId.getText()).toString())
                .setServiceName(Objects.requireNonNull(mViewBinding.serviceName.getText()).toString())
                .build();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (mListener != null) {
                    mListener.onSave(getResult());
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (mListener != null) {
                    mListener.onCancel();
                }
                break;
        }
    }

    public interface Listener {
        void onSave(@NonNull Profile profile);

        default void onCancel() {
        }
    }
}
