package com.example.myapplication.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.ui.models.Genre;
import com.example.myapplication.ui.models.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MovieDAO {

    private SQLiteDatabase db;
    private DBHelper banco;
    private Context context;

    public MovieDAO(Context context){
        banco = new DBHelper(context);
        this.context = context;
    }

    public String insereDado(MovieDetail movie){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(movie.release_date);

        String genresList = "";

        for(Genre genre : movie.genres){
            genresList = genresList.concat(genre.name + " ");
        }

        valores = new ContentValues();
        valores.put(DBHelper.ID, movie.id);
        valores.put(DBHelper.DATA, date);
        valores.put(DBHelper.OVERVIEW, movie.overview);
        valores.put(DBHelper.RUNTIME, movie.runtime);
        valores.put(DBHelper.TAGLINE, movie.tagline);
        valores.put(DBHelper.TITLE, movie.title);
        valores.put(DBHelper.VOTE_AVERAGE, movie.vote_average);
        valores.put(DBHelper.VOTE_COUNT, movie.vote_count);
        valores.put(DBHelper.POSTER_PATH, movie.poster_path);
        valores.put(DBHelper.REVENUE, movie.revenue);
        valores.put(DBHelper.GENRES_NAMES, getGenreNames(movie.genres));
        valores.put(DBHelper.GENRES_ID, getGenreid(movie.genres));

        resultado = db.insert(DBHelper.TABELA, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao salvar dados!";
        else
            return "Dados salvos com sucesso!";

    }

    public Cursor carregaDados(){
        Cursor cursor;
        String[] campos =  {
            DBHelper.ID,
            DBHelper.DATA,
            DBHelper.OVERVIEW,
            DBHelper.RUNTIME,
            DBHelper.TAGLINE,
            DBHelper.TITLE,
            DBHelper.VOTE_AVERAGE,
            DBHelper.VOTE_COUNT,
            DBHelper.POSTER_PATH,
            DBHelper.REVENUE,
            DBHelper.GENRES_NAMES,
            DBHelper.GENRES_ID,
        };
        db = banco.getReadableDatabase();
        cursor = db.query(DBHelper.TABELA, campos, null, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    private String getGenreNames(List<Genre> genres) {
        String list = "";
        for(Genre g : genres){
            list = list.concat(g.name + "");
        }
        return list.trim();
    }

    private String getGenreid(List<Genre> genres) {
        String list = "";
        for(Genre g : genres){
            list = list.concat(g.id + "");
        }
        return list.trim();
    }
}