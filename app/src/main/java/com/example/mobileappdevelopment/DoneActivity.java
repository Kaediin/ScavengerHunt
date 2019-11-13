package com.example.mobileappdevelopment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DoneActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_done);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        long time = extras.getLong("time");
        int totalQuestions = extras.getInt("questions");

        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;


        TextView timeView = findViewById(R.id.timer);
        TextView numberQuestions = findViewById(R.id.numberQuestions);

        timeView.setText("Hours: "+hours+"\nMinutes: "+minutes+"\nSeconds: "+seconds);
        if (totalQuestions == 1){
            numberQuestions.setText("You finished "+totalQuestions+ " question in");
        } else {
            numberQuestions.setText("You finished "+totalQuestions+ " questions in");
        }

    }
}
