package com.example.mehranm1;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mehranm1.database.AppDatabase;
import com.example.mehranm1.databinding.NewCheckPointBinding;
import com.google.gson.Gson;

import java.io.File;

public class NewCheckPointFragment extends Fragment {

    NewCheckPointBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NewCheckPointBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecordModel recordModel = new Gson().fromJson(getArguments().getString("data"), RecordModel.class);
        RecordItemModel recordItemModel = recordModel.getCheckPoints().get(recordModel.getCheckPoints().size() - 1);
        binding.img.setImageURI(Uri.fromFile(new File(recordItemModel.getImg())));
        binding.data.setText(getString(R.string.record_txt, recordItemModel.getSpeed(), recordItemModel.getTemp(), recordItemModel.getLat(), recordItemModel.getLng(), recordItemModel.getSteps()));

        binding.save.setOnClickListener(v -> {
            AppDatabase.getAppDatabase(getActivity()).updateAll(recordModel);

            try {
                Navigation.findNavController(v).popBackStack();
            } catch (Exception e) {
            }

        });
        binding.imageView.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).popBackStack();
            } catch (Exception e) {
            }
        });

    }
}
