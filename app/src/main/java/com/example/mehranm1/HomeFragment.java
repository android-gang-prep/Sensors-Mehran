package com.example.mehranm1;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranm1.databinding.DialogBinding;
import com.example.mehranm1.databinding.HomeFragBinding;

import java.io.File;
import java.util.Random;

public class HomeFragment extends Fragment {

    HomeFragBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_recordFragment));
        binding.circleAnimView.setOnClickListener(v -> binding.circleAnimView.startAnimation());
        RecyclerView recyclerView = binding.rec;
        recyclerView.setAdapter(new AdapterRec(getActivity(), this::ShowData));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void ShowData(RecordItemModel recordItemModel) {
        DialogBinding dialogBinding = DialogBinding.inflate(getLayoutInflater());
        dialogBinding.img.setImageURI(Uri.fromFile(new File(recordItemModel.getImg())));
        dialogBinding.data.setText(getString(R.string.record_txt, recordItemModel.getSpeed(), recordItemModel.getTemp(), recordItemModel.getLat(), recordItemModel.getLng(), recordItemModel.getSteps()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogBinding.getRoot());
        builder.create().show();

    }
}
