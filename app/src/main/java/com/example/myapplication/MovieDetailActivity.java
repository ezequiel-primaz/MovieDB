package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.Utils.Constants;
import com.example.myapplication.ui.models.Genre;
import com.example.myapplication.ui.models.Movie;
import com.example.myapplication.ui.models.MovieDetail;
import com.example.myapplication.ui.models.MoviesCatalog;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {

    TextView txtTitle;
    TextView txtTagline;
    TextView txtOverview;
    ImageView imgPoster;
    RatingBar rtbStars;
    TextView txtVoteCount;
    TextView txtRuntime;
    TextView txtReleaseDate;
    TextView txtRevenue;
    TextView txtRevenueString;
    TextView txtMovieGenre;
    TextView txtGenreString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        txtTitle = findViewById(R.id.txtMovieTitle);
        txtTagline = findViewById(R.id.txtMovieTagline);
        txtOverview = findViewById(R.id.txtMovieOverview);
        imgPoster = findViewById(R.id.imgMoviePoster);
        rtbStars = findViewById(R.id.rtbMovie);
        txtVoteCount = findViewById(R.id.txtMovieVoteCount);
        txtRuntime = findViewById(R.id.txtMovieRuntime);
        txtReleaseDate = findViewById(R.id.txtMovieReleaseDate);
        txtRevenue = findViewById(R.id.txtMovieRevenue);
        txtRevenueString = findViewById(R.id.txtRevenueString);
        txtMovieGenre = findViewById(R.id.txtMovieGenre);
        txtGenreString = findViewById(R.id.txtGenreString);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getMovieDetail();
    }

    private void getMovieDetail() {

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
// To dismiss the dialog

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        Intent intent = getIntent();

        int id = intent.getIntExtra("id", 0);

        Call<MovieDetail> requestCatalog = service.getMovieDetail(id, Constants.API_KEY, Constants.LANGUAGE);

        requestCatalog.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (!response.isSuccessful()) {
                    Log.e("Ezequiel", "Erro: " + response.code());
                } else {
                    Log.e("Ezequiel", "Setting response body");
                    MovieDetail movieDetail = response.body();

                    try{
                        txtTitle.setText(movieDetail.title);

                        if(movieDetail.tagline != null) {
                            txtTagline.setText(movieDetail.tagline);
                        } else {
                            txtTagline.setVisibility(View.GONE);
                        }

                        if(movieDetail.overview != null) {
                            txtOverview.setText(movieDetail.overview);
                        } else {
                            txtOverview.setVisibility(View.GONE);
                        }

                        Picasso.get().load("https://image.tmdb.org/t/p/w342" + movieDetail.poster_path).into(imgPoster);

                        rtbStars.setVisibility(View.VISIBLE);
                        rtbStars.setRating(movieDetail.vote_average/2);

                        txtVoteCount.setText(Integer.toString(movieDetail.vote_count));
                        txtRuntime.setText(getRuntime(movieDetail.runtime));

                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        txtReleaseDate.setText(df.format(movieDetail.release_date));

                        if(movieDetail.revenue != 0){
                            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                            txtRevenue.setText(nf.format(movieDetail.revenue));
                        } else {
                            txtRevenue.setVisibility(View.GONE);
                            txtRevenueString.setVisibility(View.GONE);
                        }

                        if(movieDetail.genres.size() > 0) {
                            txtMovieGenre.setText(getGenres(movieDetail.genres));
                        } else {
                            txtGenreString.setVisibility(View.GONE);
                            txtMovieGenre.setVisibility(View.GONE);
                        }

                        progress.dismiss();

                    } catch(NullPointerException e){
                        Log.e("Ezequiel", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("Ezequiel", "Erro: " + t.getMessage());
            }
        });
    }

    private String getGenres(List<Genre> genres) {
        String txtGenres = "";
        for(Genre item : genres) {
            txtGenres = txtGenres.concat(item.name + " ");
        }
        return txtGenres.trim();
    }

    public String getRuntime(int runtime) {
        return runtime/60 + "hr " + runtime % 60 + "min";
    }

}
