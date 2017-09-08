package com.ashwinpilgaonkar.popularmovies.ContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Moviedb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "movies.db";


    Moviedb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + MovieContract.FavEntry.TABLE_NAME + " ( " +
                MovieContract.FavEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.FavEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.FavEntry.COLUMN_PLOT + " TEXT NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavEntry.TABLE_NAME);
        onCreate(db);
    }
}