package com.example.mobileappdevelopment.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdevelopment.Adapters.RecycleViewAdapter;
import com.example.mobileappdevelopment.DataUtils.DataHunt;
import com.example.mobileappdevelopment.Model.Hunt;
import com.example.mobileappdevelopment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChooseHuntActivity extends AppCompatActivity {

    private List<String> titles;
    private List<String> authors;
    private List<Hunt> hunts;

    private FirebaseFirestore fb;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_choose_hunt);

        // data to populate the RecyclerView with
        titles = DataHunt.getTitles();
        authors = DataHunt.getAuthors();
        hunts = new ArrayList<>();

        // connect to database
        fb = FirebaseFirestore.getInstance();

        // fill the list with the right data
        populateList();

        // display the list in a recycle layout
        display();

    }

    public void populateList(){

        Task<QuerySnapshot> query = fb.collection("Scavenger_Hunts").get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())){
                    String huntStringFromDb = query.getString("HuntFile");
                    Gson gson = new Gson();
                    Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
//                    titles.add(hunt.getTitle());
//                    authors.add(hunt.getAuthor());
                    hunts.add(hunt);
                }
                display();
            }
        });
    }

    public void display(){
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvHunts);
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, titles, authors);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                1);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
