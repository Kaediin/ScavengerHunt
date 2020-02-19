package com.example.mobileappdevelopment.DataUtils

import com.example.mobileappdevelopment.Model.Hunt
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object DataHunt {
    //    private static List<String> titles = new ArrayList<>();
//    private static List<String> authors = new ArrayList<>();
    private val hunts: MutableList<Hunt> = ArrayList()
    private val fb = FirebaseFirestore.getInstance()
    //    public static List<Hunt> getHunts(){
    var titleHunt: String? = null

    //        Task<QuerySnapshot> query = fb.collection("Scavenger_Hunts").get();
//        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for (QueryDocumentSnapshot query : Objects.requireNonNull(task.getResult())){
//                    String huntStringFromDb = query.getString("HuntFile");
//                    Gson gson = new Gson();
//                    Hunt hunt = gson.fromJson(huntStringFromDb, Hunt.class);
//                    hunts.add(hunt);
//                }
//            }
//        });
//        return hunts;
//    }
//    public static void display(Context context, RecyclerView recyclerView, List<String> titles, List<String> authors){
//        RecycleViewAdapter adapter = new RecycleViewAdapter(context, titles, authors);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//    }
/*
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

     */
}