package com.example.mobileappdevelopment.DataUtils;

import androidx.annotation.NonNull;

import com.example.mobileappdevelopment.Model.Hunt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataHunt {
    private static List<String> titles = new ArrayList<>();
    private static List<String> authors = new ArrayList<>();
    private static List<Hunt> hunts = new ArrayList<>();

    private static FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private static String title = null;

    public static void setTitleHunt(String titleInput){
        title = titleInput;
    }

    public static String getTitleHunt(){
        return title;
    }


    public static List<Hunt> getHunts(){
        Task<QuerySnapshot> query = fb.collection("Scavenger_Hunts").get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())){
                    String huntStringFromDb = query.getString("HuntFile");
                    Gson gson = new Gson();
                    Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
                    hunts.add(hunt);
                }
            }
        });
        return hunts;
    }

    public static List<String> getTitles(){
        titles.clear();
        Task<QuerySnapshot> query = fb.collection("Scavenger_Hunts").get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())){
                    String huntStringFromDb = query.getString("HuntFile");
                    Gson gson = new Gson();
                    Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
                    titles.add(hunt.getTitle());
                }
            }
        });
        return titles;
    }

    public static List<String> getAuthors(){
        authors.clear();
        Task<QuerySnapshot> query = fb.collection("Scavenger_Hunts").get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())){
                    String huntStringFromDb = query.getString("HuntFile");
                    Gson gson = new Gson();
                    Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
                    authors.add(hunt.getAuthor());
                }
            }
        });
        return authors;
    }
}
