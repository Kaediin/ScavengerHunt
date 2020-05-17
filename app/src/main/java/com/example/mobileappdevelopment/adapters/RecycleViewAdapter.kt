package com.example.mobileappdevelopment.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.utils.Cache
import com.example.mobileappdevelopment.utils.Coordinates
import com.example.mobileappdevelopment.library.QuestionLibrary
import com.example.mobileappdevelopment.R
import com.example.mobileappdevelopment.activities.MapsActivity
import java.util.*

class RecycleViewAdapter(mContext: Context, titles: List<String>, authors: List<String>) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {
    private var titles: MutableList<String> = ArrayList()
    private var authors: MutableList<String> = ArrayList()
    private val mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // Inflates the view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hunts = Cache.allHunts
        holder.tvTitle.text = titles[position]
        holder.tvAuthor.text = authors[position]
        holder.setIsRecyclable(true)
        holder.relativeLayout.setOnClickListener { view ->
            Toast.makeText(mContext, "Playing Hunt: " + hunts[position].title, Toast.LENGTH_SHORT).show()
            Coordinates.coordinates = hunts[position].coordinates!!
            QuestionLibrary.choices1 = hunts[position].answer1!!
            QuestionLibrary.choices2 = hunts[position].answer2!!
            QuestionLibrary.choices3 = hunts[position].answer3!!
            QuestionLibrary.correctAnswers = hunts[position].correctAnswer!!
            QuestionLibrary.questions = hunts[position].questions!!
            QuestionLibrary.radius = hunts[position].radius!!
            val i = Intent(mContext, MapsActivity::class.java)
            i.putExtra("hunt_name", hunts[position].title)
            view.context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.title_hunt)
        var tvAuthor: TextView = itemView.findViewById(R.id.author_hunt)
        var relativeLayout: RelativeLayout = itemView.findViewById(R.id.rel_layout_rec)

    }

    init {
        this.titles = titles as MutableList<String>
        this.authors = authors as MutableList<String>
        this.mContext = mContext
    }
}