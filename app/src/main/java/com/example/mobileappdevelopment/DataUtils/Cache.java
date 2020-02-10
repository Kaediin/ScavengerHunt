package com.example.mobileappdevelopment.DataUtils;

import com.example.mobileappdevelopment.Model.Hunt;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Cache {

    public static List<Hunt> allHunts = new ArrayList<>();
    public static List<String> allHuntTitles = new ArrayList<>();
    public static List<String> allHuntAuthors = new ArrayList<>();

    public static boolean isPrivate = false;
    public static boolean listUpdated = false;

    public static GoogleSignInAccount account = null;

    public static Task<QuerySnapshot> query = null;
}
