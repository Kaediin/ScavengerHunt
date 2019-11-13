package com.example.mobileappdevelopment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseLocActivity extends AppCompatActivity implements OnMapReadyCallback{


    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LatLng selectedLatLng;
    private AlertDialog dialog;

    private Button selectLocationButton;
    private Button start;
    private Button cancel;
    private Button add;

    private EditText question;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;

    private RadioGroup radioGroup;

    private LatLng myPos;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_choose_location);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLocActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(ChooseLocActivity.this);
        @SuppressLint("InflateParams") View popupDialogView = layoutInflater.inflate(R.layout.dialog_create_question, null);
        builder.setView(popupDialogView);
        builder.setCancelable(false);
        dialog = builder.create();

        selectLocationButton = findViewById(R.id.select_location_button);
        start = findViewById(R.id.start_hunt);

        cancel = popupDialogView.findViewById(R.id.cancel_loc);
        add = popupDialogView.findViewById(R.id.add_new_loc);
        question = popupDialogView.findViewById(R.id.new_question);
        answer1 = popupDialogView.findViewById(R.id.edit_field_1);
        answer2 = popupDialogView.findViewById(R.id.edit_field_2);
        answer3 = popupDialogView.findViewById(R.id.edit_field_3);
        radioGroup = popupDialogView.findViewById(R.id.radio_group);
        selectedLatLng = null;

        start.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myPos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myPos).title("My position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15f));
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(myPos).title("My position"));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Tapped location")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin));
                selectedLatLng = latLng;
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLatLng != null) {
                    dialog.show();
                    question.requestFocus();
                } else {
                    Toast.makeText(ChooseLocActivity.this, "Please tap a location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!question.getText().toString().isEmpty() &&
                        !answer1.getText().toString().isEmpty() &&
                        !answer2.getText().toString().isEmpty() &&
                        !answer3.getText().toString().isEmpty() &&
                        getCorrectAnswer(radioGroup.getCheckedRadioButtonId()) != null) {

                    Coordinates.addCoordinates(selectedLatLng);

                    QuestionLibrary.questions.add(question.getText().toString());

                    QuestionLibrary.choices1.add(answer1.getText().toString());
                    QuestionLibrary.choices2.add(answer2.getText().toString());
                    QuestionLibrary.choices3.add(answer3.getText().toString());

                    QuestionLibrary.correctAnswers.add(getCorrectAnswer(radioGroup.getCheckedRadioButtonId()));

                    question.setText("");
                    answer1.setText("");
                    answer2.setText("");
                    answer3.setText("");
                    radioGroup.clearCheck();


                    start.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ChooseLocActivity.this, "Please fill all the fields in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseLocActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }

    private String getCorrectAnswer(int radioResult){
        switch (radioResult){
            case 1:
                return answer1.getText().toString();
            case 2:
                return answer2.getText().toString();
            case 3:
                return answer3.getText().toString();
        }
        return null;
    }

}
