package com.example.mobileappdevelopment.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdevelopment.Adapters.RecycleViewAdapter;
import com.example.mobileappdevelopment.DataUtils.Cache;
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

    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<Hunt> hunts = new ArrayList<>();

    private SearchView searchView;
    private ProgressBar progressBar;

    private FirebaseFirestore fb;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_choose_hunt);


        searchView = findViewById(R.id.search_view);
        progressBar = findViewById(R.id.progress_choose_hunt);

        // connect to database
//        fb = FirebaseFirestore.getInstance();


        // fill the list with the right data
        populateList();

//        display();

        // display the list in a recycle layout
        setupQuery();


        RecyclerView recyclerView = findViewById(R.id.rvHunts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new RecycleViewAdapter(this, titles, authors);
        recyclerView.setAdapter(adapter);
    }


    public void setupQuery() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                showSearched(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                showSearched(s);
                return false;
            }

        });
    }

    public void populateList() {

        if (Cache.allHunts == null || Cache.allHunts.isEmpty() || Cache.listUpdated) {
            Cache.query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())) {
                        String huntStringFromDb = query.getString("HuntFile");
                        Gson gson = new Gson();
                        Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
                        if (!hunt.isPrivate()){
                            Cache.allHuntTitles.add(hunt.getTitle());
                            Cache.allHuntAuthors.add(hunt.getAuthor());
                            Cache.allHunts.add(hunt);
                        } else if (hunt.isPrivate() && hunt.getAuthorID().equals(Cache.account.getId())) {
                            Cache.allHuntTitles.add(hunt.getTitle());
                            Cache.allHuntAuthors.add(hunt.getAuthor());
                            Cache.allHunts.add(hunt);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    display();
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
        hunts = Cache.allHunts;
        titles = Cache.allHuntTitles;
        authors = Cache.allHuntAuthors;
        display();

    }

    public void display() {
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvHunts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new RecycleViewAdapter(this, titles, authors);
        recyclerView.setAdapter(adapter);


//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                1);
//        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void showSearched(String s) {
        List<Hunt> dynamic_list = new ArrayList<>();

        for (Hunt hunt : Cache.allHunts) {
            if (hunt.getTitle().toUpperCase().contains(s.toUpperCase()) || hunt.getAuthor().toUpperCase().contains(s.toUpperCase())) {
                dynamic_list.add(hunt);
            }
        }

        titles.clear();
        authors.clear();
        for (Hunt hunt : dynamic_list) {
            titles.add(hunt.getTitle());
            authors.add(hunt.getAuthor());
        }

        display();
//        RecyclerView recyclerView = findViewById(R.id.rvHunts);
//        RecycleViewAdapter adapter = new RecycleViewAdapter(this, titles, authors);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
