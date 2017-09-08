package com.ashwinpilgaonkar.popularmovies.Backend;

import android.content.Context;
import android.database.Cursor;

import com.ashwinpilgaonkar.popularmovies.ContentProvider.MovieContract;

public class Utility {

    static int isFavorite(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.FavEntry.CONTENT_URI,
                null,
                MovieContract.FavEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { Integer.toString(id) },
                null
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildPosterURL(String PosterPath) {
        //Recommended image size is w185
        return "http://image.tmdb.org/t/p/w185" + PosterPath;
    }

    public static String buildBackdropURL(String BackdropPath) {
        return "http://image.tmdb.org/t/p/w300" + BackdropPath;
    }

}
