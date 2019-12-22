package com.example.mobileappdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdevelopment.Model.Hunt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChooseHuntActivity extends AppCompatActivity implements RecycleViewAdapter.ItemClickListener {

    private RecycleViewAdapter adapter;

    private List<String> namesId;

    private List<Hunt> allHunts;

    private FirebaseFirestore fb;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_choose_hunt);

        // data to populate the RecyclerView with
        namesId = new ArrayList<>();
        allHunts = new ArrayList<>();

        // connect to database
        fb = FirebaseFirestore.getInstance();

        // fill the list with the right data
        populateList();

        // display the list in a recycle layout
        display();

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + allHunts.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        Coordinates.coordinates = allHunts.get(position).getCoordinates();
        QuestionLibrary.choices1 = allHunts.get(position).getAnswer1();
        QuestionLibrary.choices2 = allHunts.get(position).getAnswer2();
        QuestionLibrary.choices3 = allHunts.get(position).getAnswer3();
        QuestionLibrary.correctAnswers = allHunts.get(position).getCorrectAnswer();
        QuestionLibrary.questions = allHunts.get(position).getQuestions();
        QuestionLibrary.radius = allHunts.get(position).getRadius();

        Intent i = new Intent(ChooseHuntActivity.this, MapsActivity.class);
        startActivity(i);
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
                    namesId.add(hunt.getTitle());
                    allHunts.add(hunt);
                }
                display();
            }
        });

    }

    public void display(){
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvHunts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleViewAdapter(this, namesId);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                1);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

//    public List<Hunt> getAllHunts() {
//        final List<Hunt> hunts = new ArrayList<>();
//
//        CollectionReference notesRef = fb.collection("Scavenger_Hunts").get();
//        notesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//
//                    }
//                }
//            }
//        });
//
//        return hunts;
//    }
}
