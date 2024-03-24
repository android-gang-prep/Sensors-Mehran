package com.example.mehranm1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranm1.database.AppDatabase;
import com.example.mehranm1.databinding.ItemBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterRec extends RecyclerView.Adapter<AdapterRec.ViewHolder> {

    List<RecordModel> recordModels;
    OnClick onClick;

    public AdapterRec(Context context, OnClick onClick) {
        this.onClick = onClick;
        this.recordModels = new ArrayList<>(AppDatabase.getAppDatabase(context).recordDAO().getAll());
    }

    public interface OnClick {
        void onClick(RecordItemModel recordModel);
    }

    @NonNull
    @Override
    public AdapterRec.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinding binding = ItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRec.ViewHolder holder, int position) {
        holder.binding.sec.setText(recordModels.get(position).getTime() + "S");
        holder.binding.chartRecord.addAllData(recordModels.get(position).getCheckPoints());
        holder.binding.chartRecord.setListener(onClick);
    }

    @Override
    public int getItemCount() {
        return recordModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBinding binding;

        public ViewHolder(@NonNull ItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }
}
