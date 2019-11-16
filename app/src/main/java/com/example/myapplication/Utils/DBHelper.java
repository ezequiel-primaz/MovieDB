package com.example.myapplication.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco.db";
    public static final String TABELA = "favoritos";
    public static final String ID = "_id";
    public static final String REVENUE = "revenue";
    public static final String DATA = "release_date";
    public static final String RUNTIME = "runtime";
    public static final String TAGLINE = "tagline";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_COUNT = "vote_count";
    public static final String GENRES_NAMES = "genres_names";
    public static final String GENRES_ID = "genres_id";
    public static final String TITLE = "title";
    public static final String POSTER_PATH = "poster_path";
    public static final String VOTE_AVERAGE = "vote_average";
    private static final int VERSAO = 2;

    public DBHelper(Context context){
        super(context, NOME_BANCO,null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABELA+"("
                + ID + " integer primary key not null,"
                + REVENUE + " long,"
                + DATA + " text,"
                + RUNTIME + " int,"
                + TAGLINE + " text,"
                + OVERVIEW + " text,"
                + VOTE_COUNT + " int,"
                + GENRES_NAMES + " text,"
                + GENRES_ID + " text,"
                + TITLE + " text,"
                + POSTER_PATH + " text,"
                + VOTE_AVERAGE + " float"
                +")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
