package com.example.mobileappdevelopment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DoneActivity extends AppCompatActivity {

    private TextView timeView;
    private TextView numberQuestions;

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



        timeView = findViewById(R.id.timer);
        numberQuestions = findViewById(R.id.numberQuestions);

        timeView.setText("Hours: "+hours+"\nMinutes: "+minutes+"\nSeconds: "+seconds);
        numberQuestions.setText("You finished "+totalQuestions+ " questions in");

    }
}
