package com.example.myapplication.ui.Favorite;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MovieDetailActivity;
import com.example.myapplication.Utils.DBHelper;
import com.example.myapplication.Utils.MovieCursorAdapter;
import com.example.myapplication.Utils.MovieDAO;
import com.example.myapplication.R;
import com.example.myapplication.ui.models.Genre;
import com.example.myapplication.ui.models.MovieDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private Cursor cursor = null;
    private ListView moviesList;
    private MovieCursorAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_movies_list, container, false);
        moviesList = root.findViewById(R.id.listMovies);

        adapter = new MovieCursorAdapter(getContext(), cursor);
        moviesList.setAdapter(adapter);

        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);

                MovieDetail movie = new MovieDetail();

                movie.title = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE));
                movie.tagline = cursor.getString(cursor.getColumnIndex(DBHelper.TAGLINE));
                movie.poster_path = cursor.getString(cursor.getColumnIndex(DBHelper.POSTER_PATH));
                movie.vote_average = cursor.getFloat(cursor.getColumnIndex(DBHelper.VOTE_AVERAGE));
                movie.vote_count = cursor.getInt(cursor.getColumnIndex(DBHelper.VOTE_COUNT));
                movie.overview = cursor.getString(cursor.getColumnIndex(DBHelper.OVERVIEW));

                try {
                    movie.release_date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex(DBHelper.DATA)));
                }catch(ParseException e){
                    e.printStackTrace();
                }

                movie.revenue = cursor.getLong(cursor.getColumnIndex(DBHelper.REVENUE));
                movie.genres = getGenresObject();
                movie.runtime = cursor.getInt(cursor.getColumnIndex(DBHelper.RUNTIME));
                movie.id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));

                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        MovieDAO db = new MovieDAO(getContext());
        cursor = db.carregaDados();
        adapter.changeCursor(cursor);
    }

    private List<Genre> getGenresObject() {
        List<Genre> list = new ArrayList<>();

        String names = cursor.getString(cursor.getColumnIndex(DBHelper.GENRES_NAMES));
        String ids = cursor.getString(cursor.getColumnIndex(DBHelper.GENRES_ID));

        String splitedNames[] = names.split(" ");
        String splitedIds[] = ids.split(" ");

        for(int i = 0; i < splitedIds.length; i++){
            Genre newGenre = new Genre();
            newGenre.id = Integer.parseInt(splitedIds[i]);
            newGenre.name = splitedNames[i];
            list.add(newGenre);
        }
        return list;
    }
}