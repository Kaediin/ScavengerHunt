package com.example.mobileappdevelopment.DataUtils

import com.example.mobileappdevelopment.Model.Hunt
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

object Cache {
    var allHunts: MutableList<Hunt> = ArrayList()
    var allHuntTitles: MutableList<String> = ArrayList()
    var allHuntAuthors: MutableList<String> = ArrayList()
    var isPrivate = false
    var listUpdated = false
    var account: GoogleSignInAccount? = null
    var query: Task<QuerySnapshot>? = null
}