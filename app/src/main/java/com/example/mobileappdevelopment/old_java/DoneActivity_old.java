//package com.example.mobileappdevelopment.activities;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.mobileappdevelopment.DataUtils.Coordinates;
//import com.example.mobileappdevelopment.DataUtils.LocUtils;
//import com.example.mobileappdevelopment.R;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.Objects;
//
//public class DoneActivity_old extends AppCompatActivity implements OnMapReadyCallback {
//
//    private AlertDialog dialog;
//
//    private Button button;
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    protected void onCreate(Bundle bundle) {
//        super.onCreate(bundle);
//        setContentView(R.layout.activity_done);
//        Objects.requireNonNull(getSupportActionBar()).hide();
//
//        button = findViewById(R.id.button_done);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(DoneActivity.this);
//        LayoutInflater layoutInflater = LayoutInflater.from(DoneActivity.this);
//        @SuppressLint("InflateParams") View popupDialogView = layoutInflater.inflate(R.layout.dialog_score, null);
//        builder.setView(popupDialogView);
//        builder.setCancelable(true);
//        dialog = builder.create();
//
//        int totalQuestions = getIntent().getIntExtra("questions", 0);
//        String huntName = getIntent().getStringExtra("hunt_name");
//
//        SharedPreferences sp = getSharedPreferences(huntName, Context.MODE_PRIVATE);
//        long time = sp.getLong(huntName+"time", 0);
//
//        long seconds = time / 1000;
//        long minutes = seconds / 60;
//        long hours = minutes / 60;
//
//        long actualSeconds = seconds - (minutes * 60);
//        long actualMinutes = minutes - (hours * 60);
//
//        TextView que = popupDialogView.findViewById(R.id.question_value);
//        TextView hrs = popupDialogView.findViewById(R.id.hours_value);
//        TextView min = popupDialogView.findViewById(R.id.minutes_value);
//        TextView sec = popupDialogView.findViewById(R.id.seconds_value);
//
//        que.setText(String.valueOf(totalQuestions));
//        hrs.setText(String.valueOf(hours));
//        min.setText(String.valueOf(actualMinutes));
//        sec.setText(String.valueOf(actualSeconds));
//
//        ImageButton imageButton = findViewById(R.id.image_button_score);
//
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.show();
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(DoneActivity.this, MainActivity.class);
//                startActivity(i);
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        int teller = 1;
//
//        for (LatLng latLng : Coordinates.getCoordinatesList()) {
//            googleMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(DoneActivity.this, "marker_green")))
//                    .title("Marker " + teller)
//            );
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
//            teller++;
//        }
//    }
//}
