package org.nexus_lab.iot_service_blockchain.sample.crystalball;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.nexus_lab.iot_service_blockchain.sample.crystalball.databinding.ItemServiceProfileBinding;

public class ServiceProfileListAdapter extends RecyclerView.Adapter<ServiceProfileListAdapter.ViewHolder> {
    private final static String[][] DUMMY_DATA = new String[][]{
            {"Streaming", "camera01", "Org1MSP"},
            {"Streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"Streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
            {"streaming01", "camera02", "Org2MSP"},
    };
    private final OnItemClickListener listener;

    public ServiceProfileListAdapter(OnItemClickListener listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceProfileBinding binding = ItemServiceProfileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return DUMMY_DATA.length;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String[] data = DUMMY_DATA[position];
        holder.getServiceNameView().setText(data[0]);
        holder.getDeviceNameView().setText(data[1]);
        holder.getOrganizationIdView().setText(data[2]);
        holder.setOnClickListener(view -> ServiceProfileListAdapter.this.listener.onItemClick(view, data));
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String[] item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemServiceProfileBinding binding;

        public ViewHolder(@NonNull ItemServiceProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getServiceNameView() {
            return binding.serviceName;
        }

        public TextView getDeviceNameView() {
            return binding.deviceName;
        }

        public TextView getOrganizationIdView() {
            return binding.organizationId;
        }

        public void setOnClickListener(View.OnClickListener listener) {
            this.binding.getRoot().setOnClickListener(listener);
        }
    }
}
