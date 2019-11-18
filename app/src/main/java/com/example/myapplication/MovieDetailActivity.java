package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Utils.Constants;
import com.example.myapplication.Utils.MovieDAO;
import com.example.myapplication.Utils.Util;
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
    TextView txtRuntimeString;
    TextView txtReleaseDate;
    TextView txtReleaseDateString;
    TextView txtRevenue;
    TextView txtRevenueString;
    TextView txtMovieGenre;
    TextView txtGenreString;
    MovieDetail movieDetail = null;
    Button btnSimilar;
    boolean favorite = false;

    ProgressDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        txtTitle = findViewById(R.id.txtMovieTitle);
        txtTagline = findViewById(R.id.txtMovieTagline);
        txtOverview = findViewById(R.id.txtMovieOverview);
        imgPoster = findViewById(R.id.imgMoviePoster);
        rtbStars = findViewById(R.id.rtbMovie);
        txtVoteCount = findViewById(R.id.txtMovieVoteCount);
        txtRuntime = findViewById(R.id.txtMovieRuntime);
        txtRuntimeString = findViewById(R.id.txtRuntimeString);
        txtReleaseDate = findViewById(R.id.txtMovieReleaseDate);
        txtReleaseDateString = findViewById(R.id.txtReleaseDateString);
        txtRevenue = findViewById(R.id.txtMovieRevenue);
        txtRevenueString = findViewById(R.id.txtRevenueString);
        txtMovieGenre = findViewById(R.id.txtMovieGenre);
        txtGenreString = findViewById(R.id.txtGenreString);
        btnSimilar = findViewById(R.id.btnSimilar);

        progress = new ProgressDialog(this);

        this.getMovieDetail();

        btnSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetSimilarActivity.class);
                intent.putExtra("id", movieDetail.id);
                intent.putExtra("title", movieDetail.title);
                startActivity(intent);
            }
        });
    }

    private void getMovieDetail() {

        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        Intent intent = getIntent();

        if (intent.getSerializableExtra("movie") != null) {
            movieDetail = (MovieDetail) intent.getSerializableExtra("movie");
            setDetailsToView(movieDetail);
        }

        int id = intent.getIntExtra("id", 0);

        Call<MovieDetail> requestCatalog = service.getMovieDetail(id, Constants.API_KEY, Constants.LANGUAGE);

        requestCatalog.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (!response.isSuccessful()) {
                    Log.e("Ezequiel", "Erro: " + response.code());
                } else {
                    Log.e("Ezequiel", "Setting response body");
                    movieDetail = response.body();
                    setDetailsToView(movieDetail);
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("Ezequiel", "Erro: " + t.getMessage());
            }
        });
    }

    private void setDetailsToView(MovieDetail movieDetail) {
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

            if (movieDetail.runtime != null) {
                txtRuntime.setText(getRuntime(movieDetail.runtime));
                txtRuntimeString.setVisibility(View.VISIBLE);
            }

            if (movieDetail.release_date != null) {
                txtReleaseDate.setText(Util.df.format(movieDetail.release_date));
                txtReleaseDateString.setVisibility(View.VISIBLE);
            }

            if(movieDetail.revenue != 0){
                NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                txtRevenue.setText(nf.format(movieDetail.revenue));
                txtRevenueString.setVisibility(View.VISIBLE);
            } else {
                txtRevenue.setVisibility(View.GONE);
                txtRevenueString.setVisibility(View.GONE);
            }

            if(movieDetail.genres.size() > 0) {
                txtMovieGenre.setText(getGenres(movieDetail.genres));
                txtGenreString.setVisibility(View.VISIBLE);
            } else {
                txtGenreString.setVisibility(View.GONE);
                txtMovieGenre.setVisibility(View.GONE);
            }

            progress.dismiss();

        } catch(NullPointerException e){
            Log.e("Ezequiel", e.getMessage());
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_movie_menu, menu);
        Drawable drawable = menu.getItem(0).getIcon();
        drawable.mutate();
        MovieDAO db = new MovieDAO(getApplicationContext());

        if (getIntent().getSerializableExtra("movie") != null) {

            MovieDetail md = (MovieDetail) getIntent().getSerializableExtra("movie");

            favorite = db.hasFavorite(md.id);
            if (favorite) {
                drawable.setColorFilter(getResources().getColor(R.color.addedToFavoriteList), PorterDuff.Mode.SRC_ATOP);
            } else {
                drawable.setColorFilter(getResources().getColor(R.color.addToFavoriteList), PorterDuff.Mode.SRC_ATOP);
            }

        } else {
            favorite = db.hasFavorite(getIntent().getIntExtra("id", 0));
            if (favorite) {
                drawable.setColorFilter(getResources().getColor(R.color.addedToFavoriteList), PorterDuff.Mode.SRC_ATOP);
            } else {
                drawable.setColorFilter(getResources().getColor(R.color.addToFavoriteList), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addActionButton) {

            if(!favorite){
                MovieDAO db = new MovieDAO(getBaseContext());
                String resultado = db.insereDado(movieDetail);

                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            } else {
                MovieDAO db = new MovieDAO(getBaseContext());
                String resultado = db.deletaRegistro(movieDetail.id);

                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            }

            finish();
            startActivity(getIntent());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
