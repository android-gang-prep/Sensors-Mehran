package com.example.mehranm1;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;

import com.example.mehranm1.database.AppDatabase;
import com.example.mehranm1.databinding.RecordFragBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RecordFragment extends Fragment implements SensorEventListener {

    RecordFragBinding binding;

    Sensor tempSensor;
    SensorManager sensorManager;

    float temp;
    double lat;
    double lng;
    float speed;
    int step;
    int totalStep;

    private boolean running = false;

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float

    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float
    private int previousTotalSteps = 0;
    RecordModel recordModel;
    Sensor stepSensor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RecordFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        binding.play.setOnClickListener(v -> {


            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                return;
            }

            recordModel = new RecordModel(0, new ArrayList<>(), 0);
            recordModel.setId(AppDatabase.getAppDatabase(getActivity()).insert(recordModel));
            startAnimation();
            startLocation();
            time = 0;
            start();
        });

        binding.checkPoint.setOnClickListener(v -> openCamera());

        binding.finish.setOnClickListener(v -> {
            recordModel.addCheckPoint(new RecordItemModel(speed, temp, lat, lng, step, ""));
            recordModel.setStatus(1);
            AppDatabase.getAppDatabase(getActivity()).updateAll(recordModel);
            Navigation.findNavController(v).popBackStack();
        });


    }

    private void startAnimation() {
        binding.linearLayoutCompat.setBackground(getResources().getDrawable(R.drawable.gradient_animation));
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.linearLayoutCompat.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

            File folder = new File(getActivity().getCacheDir(), "cache");
            if (!folder.exists())
                folder.mkdirs();

            String filename = System.currentTimeMillis() + ".jpg";

            File img = new File(folder, filename);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(img);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);


                recordModel.addCheckPoint(new RecordItemModel(speed, temp, lat, lng, step, img.getPath()));
                Bundle bundle = new Bundle();
                bundle.putString("data", new Gson().toJson(recordModel));
                Navigation.findNavController(getView()).navigate(R.id.action_recordFragment_to_newCheckPointFragment, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    });


    private void startLocation() {
      /*  LocationRequest.Builder locationRequest = new LocationRequest.Builder(100);
        locationRequest.setWaitForAccurateLocation(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.requestLocationUpdates(locationRequest.build(),
                locationCallback,
                Looper.getMainLooper());*/

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationCallback);
        setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    Timer timer;

    int time;

    private void start() {
        running = true;


        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);


        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                recordModel.setTime(time);
                getActivity().runOnUiThread(() -> binding.timer.setText(String.valueOf(time)));
            }
        }, 1000, 1000);
        binding.play.setVisibility(View.GONE);
        binding.checkPoint.setVisibility(View.VISIBLE);
        binding.finish.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timer != null) {
            start();
            startLocation();
            recordModel = AppDatabase.getAppDatabase(getActivity()).recordDAO().getRecord(recordModel.getId());

            updateData();
            binding.timer.setText(time + "");
            startAnimation();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationCallback);
      /*  if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);*/
    }

    private boolean first = true;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            if (event.values[0] <= 100 && event.values[0] >= -273.1)
                temp = event.values[0];
        } else {
            totalStep = (int) event.values[0];

            if (first) {
                previousTotalSteps = totalStep;
                first = false;
            }
            if (running) {
                step = totalStep - previousTotalSteps;
            }
        }
        updateData();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateData() {
        binding.data.setText(getString(R.string.record_txt, speed, temp, lat, lng, step));
    }

    LocationListener locationCallback = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location == null)
                return;
            setLocation(location);
        }
    };

    private void setLocation(Location location) {

        lat = location.getLatitude();
        lng = location.getLongitude();
        speed = location.getSpeed();
        if (recordModel.getCheckPoints().size() == 0) {
            recordModel.getCheckPoints().add(new RecordItemModel(speed, temp, lat, lng, step, ""));
            AppDatabase.getAppDatabase(getActivity()).updateAll(recordModel);

        }
        updateData();

    }
  /*  LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null)
                return;

            for (Location location : locationResult.getLocations()) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                speed = location.getSpeed();
                if (recordModel.getCheckPoints().size() == 0) {
                    recordModel.getCheckPoints().add(new RecordItemModel(speed, temp, lat, lng, step, ""));
                    AppDatabase.getAppDatabase(getActivity()).updateAll(recordModel);

                }
            }
            updateData();

        }
    };*/

}
