package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.ui.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    public MoviesArrayAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_movie, parent, false);
        }

        Movie movie = getItem(position);

        TextView txtTitle = listItemView.findViewById(R.id.txt_title);
        ImageView imgPoster = listItemView.findViewById(R.id.img_poster);
        RatingBar rtbStars = listItemView.findViewById(R.id.rtb_stars);

        txtTitle.setText(movie.title);
        Picasso.get().load("https://image.tmdb.org/t/p/w92" + movie.poster_path).into(imgPoster);
        rtbStars.setRating(movie.vote_average/2);

        return listItemView;
    }
}
