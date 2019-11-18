package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.Utils.Constants;
import com.example.myapplication.Utils.MoviesArrayAdapter;
import com.example.myapplication.ui.models.Movie;
import com.example.myapplication.ui.models.MoviesCatalog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetSimilarActivity extends AppCompatActivity {


    private int page = 0;
    private List<Movie> list = null;
    private MoviesArrayAdapter adapter = null;
    private boolean flag_loading = false;
    ListView moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movies_list);

        this.getMovies(0);

        moviesList = findViewById(R.id.listMovies);

        if(list == null){
            list = new ArrayList<>();
        }

        adapter = new MoviesArrayAdapter(getApplicationContext(), list);
        moviesList.setAdapter(adapter);

        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra("id", list.get(position).id);
                startActivity(intent);
            }
        });

        moviesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!flag_loading)
                    {
                        flag_loading = true;
                        getMovies(1);
                    }
                }
            }
        });
    }

    private void getMovies(final int cod) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        Call<MoviesCatalog> requestCatalog = service.similarMovies(getIntent().getIntExtra("id",0), Constants.API_KEY, Constants.LANGUAGE, page + 1);

        requestCatalog.enqueue(new Callback<MoviesCatalog>() {
            @Override
            public void onResponse(Call<MoviesCatalog> call, Response<MoviesCatalog> response) {
                if (!response.isSuccessful()) {
                    Log.e("Ezequiel", "Erro: " + response.code() + response.raw().toString());
                } else {
                    MoviesCatalog catalog = response.body();
                    Log.e("Ezequiel", "page response = " + catalog.page);
                    page = catalog.page;
                    if(cod == 1) {
                        list.addAll(catalog.results);
                        adapter.notifyDataSetChanged();
                        flag_loading = false;
                    } else {
                        list.clear();
                        list.addAll(catalog.results);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesCatalog> call, Throwable t) {
                Log.e("Ezequiel", "Erro: " + t.getMessage());
            }
        });
    }
}
