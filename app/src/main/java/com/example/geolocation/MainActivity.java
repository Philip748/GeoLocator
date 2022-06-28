package com.example.geolocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 15;
    public static final int FASTEST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_accuracy, tv_altitude, tv_updates, tv_bearing, tv_unidistance, tv_mcdistance,tv_homedistance, tv_unidirection, tv_mcdirection, tv_homedirection, tv_speed;
    Switch sw_locationsupdates;

    LocationRequest locationRequest;

    Location uni, mc, home;

    // Google's API for location services. VERY IMPORTANT
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_updates = findViewById(R.id.tv_updates);
        tv_bearing = findViewById(R.id.tv_bearing);
        tv_unidistance = findViewById(R.id.tv_unidistance);
        tv_mcdistance = findViewById(R.id.tv_mcdistance);
        tv_homedistance = findViewById(R.id.tv_homedistance);
        tv_unidirection = findViewById(R.id.tv_unidirection);
        tv_mcdirection = findViewById(R.id.tv_mcdirection);
        tv_homedirection = findViewById(R.id.tv_homedirection);
        tv_speed = findViewById(R.id.tv_speed);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);

        uni = new Location("University");
        uni.setLatitude(55.870889);
        uni.setLongitude(-4.288742);
        uni.setAltitude(92.0);

        mc = new Location("McDonalds");
        mc.setLatitude(55.862795);
        mc.setLongitude(-4.280469);
        mc.setAltitude(75.0);

        home = new Location("Home");
        home.setLatitude(55.866866);
        home.setLongitude(-4.290409);
        home.setAltitude(79.0);

        // Set all properties of LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationsupdates.isChecked()) {
                    startLcationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_lat.setText("Location is not being tracked");
        tv_lon.setText("Location is not being tracked");
        tv_altitude.setText("Location is not being tracked");
        tv_accuracy.setText("Location is not being tracked");
        tv_updates.setText("Location is not being tracked");
        tv_bearing.setText("Location is not being tracked");
        tv_unidistance.setText("Location is not being tracked");
        tv_mcdistance.setText("Location is not being tracked");
        tv_homedistance.setText("Location is not being tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLcationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires GPS permission to be granted in order to function", Toast.LENGTH_SHORT);
                    finish();
                }
        }
    }

    private void updateGPS() {
        // Get permission to track gps from user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Perms granted by user
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // We got perms, Put location value into display.
                    updateUIValues(location);
                }
            });
        }
        else {
            // Perms not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
        // Get the current location
        // Update the display
    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        tv_unidistance.setText(String.valueOf(location.distanceTo(uni)) + "M");
        tv_unidirection.setText(String.valueOf(location.bearingTo(uni)));
        tv_mcdistance.setText(String.valueOf(location.distanceTo(mc)) + "M");
        tv_mcdirection.setText(String.valueOf(location.bearingTo(mc)));
        tv_homedistance.setText(String.valueOf(location.distanceTo(home)) + "M");
        tv_homedirection.setText(String.valueOf(location.bearingTo(home)));

        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("NA");
        }

        if (location.hasBearing()){
            tv_bearing.setText(String.valueOf(location.getBearing()));
        }
        else {
            tv_bearing.setText("NA");
        }
        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("NA");
        }
    }
}