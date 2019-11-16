package com.example.myapplication.Utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.models.Movie;
import com.squareup.picasso.Picasso;

public class MovieCursorAdapter extends CursorAdapter {

    private final LayoutInflater inflater;

    public MovieCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        // Get inflater
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Use the inflater to inflate the listview layout and return it
        return inflater.inflate(R.layout.list_movie, parent, false);
    }

    @Override
    public void bindView(View listItemView, Context context, Cursor cursor) {

        Log.e("Ezequiel", "creating list item");

        TextView txtTitle = listItemView.findViewById(R.id.txt_title);
        ImageView imgPoster = listItemView.findViewById(R.id.img_poster);
        RatingBar rtbStars = listItemView.findViewById(R.id.rtb_stars);

        txtTitle.setText(cursor.getString(cursor.getColumnIndex(DBHelper.TITLE)));
        Picasso.get().load("https://image.tmdb.org/t/p/w92" + cursor.getString(cursor.getColumnIndex(DBHelper.POSTER_PATH))).into(imgPoster);
        rtbStars.setRating(cursor.getInt(cursor.getColumnIndex(DBHelper.VOTE_AVERAGE))/2);

    }
}
