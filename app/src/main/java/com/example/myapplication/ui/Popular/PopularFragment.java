package com.example.myapplication.ui.Popular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MovieDBService;
import com.example.myapplication.MovieDetailActivity;
import com.example.myapplication.MoviesArrayAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Constants;
import com.example.myapplication.ui.models.Movie;
import com.example.myapplication.ui.models.MoviesCatalog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopularFragment extends Fragment {

    private int page = 0;
    private List<Movie> list = null;
    private MoviesArrayAdapter adapter = null;
    private boolean flag_loading = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_movies_list, container, false);
        final ListView moviesList = root.findViewById(R.id.listMovies);

        this.getMovies(0);

        if(list == null){
            list = new ArrayList<>();
        }

        adapter = new MoviesArrayAdapter(this.getContext(), list);

        moviesList.setAdapter(adapter);

        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
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

        return root;
    }

    private void getMovies(final int cod) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        Log.e("Ezequiel", "page = " + (page + 1));
        Call<MoviesCatalog> requestCatalog = service.listPopularMovies(page + 1, Constants.API_KEY, Constants.LANGUAGE);

        requestCatalog.enqueue(new Callback<MoviesCatalog>() {
            @Override
            public void onResponse(Call<MoviesCatalog> call, Response<MoviesCatalog> response) {
                if (!response.isSuccessful()) {
                    Log.e("Ezequiel", "Erro: " + response.code());
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