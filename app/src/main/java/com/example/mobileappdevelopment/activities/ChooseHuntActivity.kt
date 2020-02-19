package com.example.mobileappdevelopment.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.Adapters.RecycleViewAdapter
import com.example.mobileappdevelopment.DataUtils.Cache
import com.example.mobileappdevelopment.Model.Hunt
import com.example.mobileappdevelopment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import org.intellij.lang.annotations.Language
import java.util.*

class ChooseHuntActivity : AppCompatActivity() {
    private var titles: MutableList<String> = ArrayList()
    private var authors: MutableList<String> = ArrayList()
    private var hunts: List<Hunt> = ArrayList()
    private var searchView: SearchView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstances: Bundle?) {
        super.onCreate(savedInstances)
        setContentView(R.layout.activity_choose_hunt)
        searchView = findViewById(R.id.search_view)
        progressBar = findViewById(R.id.progress_choose_hunt)

        populateList()

        setupQuery()

        display()

    }

    fun setupQuery() {
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                showSearched(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                showSearched(s)
                return false
            }
        })
    }

    fun populateList() {
        if (Cache.allHunts.isEmpty() || Cache.listUpdated) {
            Cache.query?.addOnCompleteListener { task ->
                for (query in task.result!!) {
                    val huntStringFromDb = query.getString("HuntFile")
                    val gson = Gson()
                    val hunt = gson.fromJson(huntStringFromDb, Hunt::class.java)
                    if (!hunt.isPrivate) {
                        hunt.title?.let { Cache.allHuntTitles.add(it) }
                        hunt.author?.let { Cache.allHuntAuthors.add(it) }
                        Cache.allHunts.add(hunt)
                    } else if (hunt.isPrivate && hunt.authorID == Cache.account?.id) {
                        hunt.title?.let { Cache.allHuntTitles.add(it) }
                        hunt.author?.let { Cache.allHuntAuthors.add(it) }
                        Cache.allHunts.add(hunt)
                    }
                }
                progressBar!!.visibility = View.GONE
                display()
            }
        } else {
            progressBar!!.visibility = View.GONE
        }
        hunts = Cache.allHunts
        titles = Cache.allHuntTitles
        authors = Cache.allHuntAuthors
        display()
    }

    // set up the RecyclerView
    fun display() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvHunts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter: RecyclerView.Adapter<*> = RecycleViewAdapter(this, titles, authors)
        recyclerView.adapter = adapter
    }

    @SuppressLint("DefaultLocale")
    fun showSearched(s: String) {
        val dynamic_list: MutableList<Hunt> = ArrayList()
        for (hunt in Cache.allHunts) {
            if (hunt.title!!.toUpperCase().contains(s.toUpperCase()) || hunt.author!!.toUpperCase().contains(s.toUpperCase())) {
                dynamic_list.add(hunt)
            }
        }
        titles.clear()
        authors.clear()
        for (hunt in dynamic_list) {
            hunt.title?.let { titles.add(it) }
            hunt.author?.let { authors.add(it) }
        }
        display()

    }
}