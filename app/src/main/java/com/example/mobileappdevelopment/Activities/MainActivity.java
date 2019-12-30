package com.example.mobileappdevelopment.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mobileappdevelopment.DataUtils.DataHunt;
import com.example.mobileappdevelopment.Model.Hunt;
import com.example.mobileappdevelopment.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private LocationManager manager;

    private EditText title_hunt;

    private Button save_title_hunt;

    private GoogleSignInClient mGoogleSignInClient;

    private AlertDialog dialog;

    private String account_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        @SuppressLint("InflateParams") View popupDialogView = layoutInflater.inflate(R.layout.title_select_dialog, null);
        builder.setView(popupDialogView);
        dialog = builder.create();

        save_title_hunt = popupDialogView.findViewById(R.id.button_save_hunt_title);
        title_hunt = popupDialogView.findViewById(R.id.edit_text_hunt_title);

        final Button create_new_button = findViewById(R.id.button_create_new);
        final Button start_selected = findViewById(R.id.button_start_selected);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        create_new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        dialog.show();
                        save_title_hunt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setTitleHunt();
                            }
                        });
                    } else {
                        buildAlertMessageNoGps();
                    }
                }
            }
        });

        start_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent i = new Intent(MainActivity.this, ChooseHuntActivity.class);
                        startActivity(i);
                    } else {
                        buildAlertMessageNoGps();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            signIn();
        } else {
            account_name = account.getDisplayName();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 800);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 800) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Welcome " + Objects.requireNonNull(account).getDisplayName(), Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Sign in rejected. Going further as 'Anonymous'", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent i = new Intent(MainActivity.this, CreateHuntActivity.class);
                    startActivity(i);
                } else {
                    buildAlertMessageNoGps();
                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void setTitleHunt() {
        if (title_hunt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Title cant be null", Toast.LENGTH_SHORT).show();
        } else {
            String title = title_hunt.getText().toString();
            String huntCode = title+account_name;
            boolean isUnique = true;
            for (Hunt hunt : DataHunt.getHunts()) {
                if (hunt.getHuntCode().equals(huntCode)) {
                    isUnique = false;
                }
            }

            if (isUnique) {
                DataHunt.setTitleHunt(title);
                Intent i = new Intent(MainActivity.this, CreateHuntActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "Title is already occupied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
