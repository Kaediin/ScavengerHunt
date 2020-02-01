package com.example.mobileappdevelopment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdevelopment.Activities.MapsActivity;
import com.example.mobileappdevelopment.DataUtils.Cache;
import com.example.mobileappdevelopment.DataUtils.Coordinates;
import com.example.mobileappdevelopment.Library.QuestionLibrary;
import com.example.mobileappdevelopment.Model.Hunt;
import com.example.mobileappdevelopment.R;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapter(Context mContext, List<String> titles, List<String> authors) {
        this.titles = titles;
        this.authors = authors;
        this.mContext = mContext;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final List<Hunt> hunts = Cache.allHunts;

        holder.tvTitle.setText(titles.get(position));
        holder.tvAuthor.setText(authors.get(position));
        holder.setIsRecyclable(true);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Playing Hunt: " + hunts.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                Coordinates.coordinates = hunts.get(position).getCoordinates();
                QuestionLibrary.choices1 = hunts.get(position).getAnswer1();
                QuestionLibrary.choices2 = hunts.get(position).getAnswer2();
                QuestionLibrary.choices3 = hunts.get(position).getAnswer3();
                QuestionLibrary.correctAnswers = hunts.get(position).getCorrectAnswer();
                QuestionLibrary.questions = hunts.get(position).getQuestions();
                QuestionLibrary.radius = hunts.get(position).getRadius();
                Intent i = new Intent(mContext, MapsActivity.class);
                i.putExtra("hunt_name", hunts.get(position).getTitle());
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvAuthor;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title_hunt);
            tvAuthor = itemView.findViewById(R.id.author_hunt);
            relativeLayout = itemView.findViewById(R.id.rel_layout_rec);
        }
    }
}