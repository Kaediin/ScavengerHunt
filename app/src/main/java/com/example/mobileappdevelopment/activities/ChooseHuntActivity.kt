package com.example.mobileappdevelopment.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.adapters.RecycleViewAdapter
import com.example.mobileappdevelopment.utils.Cache
import com.example.mobileappdevelopment.model.Hunt
import com.example.mobileappdevelopment.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_choose_hunt.*
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
        searchView = search_view
        progressBar = progress_choose_hunt

        populateList()

        setupQuery()

        display()

    }

    private fun setupQuery() {
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

    private fun populateList() {
        if (Cache.allHunts.isEmpty() || Cache.listUpdated) {
            Cache.allHunts.clear()
            Cache.allHuntAuthors.clear()
            Cache.allHuntTitles.clear()
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
    private fun display() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvHunts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter: RecyclerView.Adapter<*> = RecycleViewAdapter(this, titles, authors)
        recyclerView.adapter = adapter
    }

    @SuppressLint("DefaultLocale")
    fun showSearched(s: String) {
        val dynamicList: MutableList<Hunt> = ArrayList()
        for (hunt in Cache.allHunts) {
            if (hunt.title!!.toUpperCase().contains(s.toUpperCase()) || hunt.author!!.toUpperCase().contains(s.toUpperCase())) {
                dynamicList.add(hunt)
            }
        }
        titles.clear()
        authors.clear()
        for (hunt in dynamicList) {
            hunt.title?.let { titles.add(it) }
            hunt.author?.let { authors.add(it) }
        }
        display()

    }
}