package com.example.myapplication;

import com.example.myapplication.ui.models.MovieDetail;
import com.example.myapplication.ui.models.MoviesCatalog;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBService {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/top_rated")
    Call<MoviesCatalog> listTopRatedMovies(@Query("page") int page, @Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/popular")
    Call<MoviesCatalog> listPopularMovies(@Query("page") int page, @Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/{id}")
    Call<MovieDetail> getMovieDetail(@Path("id") int id, @Query("api_key") String api_key, @Query("language") String language);

}
