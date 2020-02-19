//package com.example.mobileappdevelopment.activities;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.example.mobileappdevelopment.DataUtils.Coordinates;
//import com.example.mobileappdevelopment.DataUtils.LocUtils;
//import com.example.mobileappdevelopment.Library.QuestionLibrary;
//import com.example.mobileappdevelopment.R;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.text.DecimalFormat;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Objects;
//
//public class MapsActivity_old extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
//
//    private AlertDialog dialog;
//
//    private GoogleMap mMap;
//    private GoogleApiClient googleApiClient;
//
//    private int progress_number;
//    private long time;
//
//    private boolean dialogActive = false;
//
//    private TextView mQuestionView;
//
//    private String mAnswer;
//    private String hunt_name;
//
//    private Button mButtonChoice1;
//    private Button mButtonChoice2;
//    private Button mButtonChoice3;
//
//    private long start_time;
//
//    private SharedPreferences sp;
//    private SharedPreferences.Editor editor;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        Objects.requireNonNull(getSupportActionBar()).hide();
//
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        googleApiClient = new GoogleApiClient.Builder(this).
//                addApi(LocationServices.API).
//                addConnectionCallbacks(this).
//                addOnConnectionFailedListener(this).build();
//
//        hunt_name = getIntent().getStringExtra("hunt_name");
//        sp = getSharedPreferences(hunt_name, Context.MODE_PRIVATE);
//        int progress = sp.getInt(hunt_name, 0);
//
//        if (progress == 0){
//            start_time = Calendar.getInstance().getTimeInMillis();
//            editor = sp.edit();
//            editor.putLong(hunt_name+"start_time", start_time);
//            editor.apply();
//        } else {
//            start_time = sp.getLong(hunt_name+"start_time", Calendar.getInstance().getTimeInMillis());
//        }
//
//        progress_number = progress;
//        time = 1000;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // stop location updates
//        if (googleApiClient != null && googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
//
//        if (location != null) {
//            setTVDistance(location);
//            setupMap(location);
//        }
//
//        startLocationUpdates();
//    }
//
//    private void setupMap(Location location) {
//        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.addMarker(new MarkerOptions()
//                .position(currentLoc)
//                .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(MapsActivity.this, "marker_red")))
//                .title("Marker in your current location"));
//        loadCoordinates(progress_number);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15f));
//    }
//
//    private void loadCoordinates(int numberAscending) {
//        List<LatLng> coordinates = Coordinates.getCoordinatesList();
//        mMap.addMarker(new MarkerOptions()
//                .position(coordinates.get(numberAscending))
//                .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(MapsActivity.this, "marker_green")))
//                .title("Coordinate " + numberAscending));
//        mMap.addCircle(new CircleOptions().center(coordinates.get(numberAscending)).radius(QuestionLibrary.radius.get(numberAscending)));
//    }
//
//    private void startLocationUpdates() {
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(time);
//        locationRequest.setFastestInterval(time);
//
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "You need to enable permissions to display your location!", Toast.LENGTH_SHORT).show();
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if (location != null && !dialogActive) {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mMap.clear();
//                LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
//                loadCoordinates(progress_number);
//                mMap.addMarker(new MarkerOptions()
//                        .position(currentLoc)
//                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(MapsActivity.this, "marker_red")))
//                        .title("Marker in your current location"));
//
//                double distanceInKM = setTVDistance(location);
//
//                if (distanceInKM < QuestionLibrary.radius.get(progress_number)) {
//
//                    dialogActive = true;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    LayoutInflater layoutInflater = LayoutInflater.from(this);
//                    @SuppressLint("InflateParams") View popupDialogView = layoutInflater.inflate(R.layout.dialog_question, null);
//                    builder.setView(popupDialogView);
//                    builder.setCancelable(false);
//                    dialog = builder.create();
//                    dialog.show();
//
//                    mQuestionView = popupDialogView.findViewById(R.id.question);
//                    mButtonChoice1 = popupDialogView.findViewById(R.id.button_1);
//                    mButtonChoice2 = popupDialogView.findViewById(R.id.button_2);
//                    mButtonChoice3 = popupDialogView.findViewById(R.id.button_3);
//
//                    updateQuestions(progress_number);
//                    final Handler handler = new Handler();
//
//                    if (mButtonChoice1 != null) {
//                        mButtonChoice1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if (mButtonChoice1.getText().equals(mAnswer)) {
//                                    mButtonChoice1.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.green));
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            next_level();
//                                            isDone(progress_number);
//                                        }
//                                    }, 1000);
//                                } else {
//                                    mButtonChoice1.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.red));
//                                }
//                            }
//                        });
//                    }
//
//                    if (mButtonChoice2 != null) {
//                        mButtonChoice2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if (mButtonChoice2.getText().equals(mAnswer)) {
//                                    mButtonChoice2.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.green));
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            next_level();
//                                            isDone(progress_number);
//                                        }
//                                    }, 1000);
//                                } else {
//                                    mButtonChoice2.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.red));
//                                }
//                            }
//                        });
//                    }
//
//                    if (mButtonChoice3 != null) {
//                        mButtonChoice3.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if (mButtonChoice3.getText().equals(mAnswer)) {
//                                    mButtonChoice3.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.green));
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            next_level();
//                                            isDone(progress_number);
//                                        }
//                                    }, 1000);
//                                } else {
//                                    mButtonChoice3.setBackgroundTintList(MapsActivity.this.getResources().getColorStateList(R.color.red));
//                                }
//                            }
//                        });
//                    }
//                }
//            } else {
//                Toast.makeText(this, "You need to enable permissions to display your location!", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(MapsActivity.this, MainActivity.class);
//                startActivity(i);
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private double setTVDistance(Location location) {
//        Location locCheck = new Location(LocationManager.GPS_PROVIDER);
//        locCheck.setLatitude(Coordinates.getCoordinatesList().get(progress_number).latitude);
//        locCheck.setLongitude(Coordinates.getCoordinatesList().get(progress_number).longitude);
//        double distanceInKM = (location.distanceTo(locCheck));
//        TextView distanceTV = findViewById(R.id.distanceText);
//        DecimalFormat distanceRounded = new DecimalFormat("###");
//        distanceTV.setText((distanceRounded.format(distanceInKM)) + " M");
//
//        return distanceInKM;
//    }
//
//    public void next_level(){
//        dialog.dismiss();
//        dialogActive = false;
//        progress_number++;
//        editor = sp.edit();
//        editor.putInt(hunt_name, progress_number);
//        editor.apply();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    public void isDone(int numberAscending) {
//        if (numberAscending >= QuestionLibrary.questions.size()) {
//            long endTime = Calendar.getInstance().getTimeInMillis();
//            long time = endTime - start_time;
//
//            editor = sp.edit();
//            editor.putInt(hunt_name, 0);
//            editor.putLong(hunt_name+"time", time);
//            editor.apply();
//
//            Intent i = new Intent(MapsActivity.this, DoneActivity.class);
//            i.putExtra("hunt_name", hunt_name);
//            i.putExtra("questions", numberAscending);
//            startActivity(i);
//        }
//    }
//
//    private void updateQuestions(int mQuestionNumber) {
//
//        mQuestionView.setText(QuestionLibrary.questions.get(mQuestionNumber));
//        mButtonChoice1.setText(QuestionLibrary.choices1.get(mQuestionNumber));
//        mButtonChoice2.setText(QuestionLibrary.choices2.get(mQuestionNumber));
//        mButtonChoice3.setText(QuestionLibrary.choices3.get(mQuestionNumber));
//        mAnswer = QuestionLibrary.correctAnswers.get(mQuestionNumber);
//    }
//}
