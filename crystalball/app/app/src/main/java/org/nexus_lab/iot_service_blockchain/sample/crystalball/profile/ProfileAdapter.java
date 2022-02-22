package org.nexus_lab.iot_service_blockchain.sample.crystalball.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ItemServiceProfileBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private final List<OnItemClickListener> mListeners = new ArrayList<>();
    private final AsyncListDiffer<Profile> mDiffer = new AsyncListDiffer<>(this, new DiffCallback());

    public void submitList(List<Profile> profiles) {
        mDiffer.submitList(profiles);
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        mListeners.add(listener);
    }

    public void removeOnItemClickListener(OnItemClickListener listener) {
        mListeners.remove(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceProfileBinding binding = ItemServiceProfileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile profile = mDiffer.getCurrentList().get(position);
        holder.getServiceNameView().setText(profile.getServiceName());
        holder.getDeviceNameView().setText(profile.getDeviceName() == null ? profile.getDeviceId() : profile.getDeviceName());
        holder.getOrganizationIdView().setText(profile.getOrganizationId());
        holder.setOnClickListener(view -> {
            for (OnItemClickListener listener : mListeners) {
                listener.onItemClick(view, profile);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Profile profile);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemServiceProfileBinding mViewBinding;

        public ViewHolder(@NonNull ItemServiceProfileBinding binding) {
            super(binding.getRoot());
            this.mViewBinding = binding;
        }

        public TextView getServiceNameView() {
            return mViewBinding.serviceName;
        }

        public TextView getDeviceNameView() {
            return mViewBinding.deviceName;
        }

        public TextView getOrganizationIdView() {
            return mViewBinding.organizationId;
        }

        public void setOnClickListener(View.OnClickListener listener) {
            this.mViewBinding.getRoot().setOnClickListener(listener);
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Profile> {

        @Override
        public boolean areItemsTheSame(@NonNull Profile oldItem, @NonNull Profile newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Profile oldItem, @NonNull Profile newItem) {
            return oldItem.equals(newItem);
        }
    }
}
