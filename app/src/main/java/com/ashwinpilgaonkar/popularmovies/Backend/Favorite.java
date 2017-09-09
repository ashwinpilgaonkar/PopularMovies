package com.ashwinpilgaonkar.popularmovies.Backend;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import com.ashwinpilgaonkar.popularmovies.ContentProvider.MovieContract;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.ashwinpilgaonkar.popularmovies.UI.MainActivity;
import com.ashwinpilgaonkar.popularmovies.UI.MainActivityFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// This class handles all operations of checking/adding/removing items to the favorites list
public class Favorite {

    private Activity activity;
    private MovieModel movie;
    private MenuItem favorite;
    private Snackbar snackbar;
    private ArrayList<MovieModel> movieList;

    @BindView(R.id.activity_movie_detail) CoordinatorLayout rootView;

    private static final String[] FAV_COLUMNS = {
            MovieContract.FavEntry._ID,
            MovieContract.FavEntry.COLUMN_MOVIE_ID,
            MovieContract.FavEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavEntry.COLUMN_POSTER_PATH,
            MovieContract.FavEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavEntry.COLUMN_PLOT
    };

    /* The constructor Favorite accepts the current movie object, a reference to the favorite menu item and an action integer.
     * If action is passed as 0, it will check if current movie is in favorites list or not (execute SetFavoritesIconAsyncTask)
     * If action is passed as 1, it will add/remove the current movie from the favorites list (execute AddRemoveFavoritesAsyncTask)
     */

    public Favorite(Activity activity, MovieModel movie, MenuItem favorite, int action){
        this.activity = activity;
        this.movie = movie;
        this.favorite = favorite;

        if(action==0)
            new SetFavoritesIconAsyncTask().execute();

        else {
            ButterKnife.bind(this, activity);
            new AddRemoveFavoritesAsyncTask().execute();
        }
    }

    public Favorite(Activity activity, ArrayList<MovieModel> movieList){
        this.activity = activity;
        this.movieList = movieList;

        new FetchFavoritesAsyncTask().execute();
    }

    private class SetFavoritesIconAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return Utility.isFavorite(activity, movie.getID());
        }

        @Override
            protected void onPostExecute (Integer isFavored){
            favorite.setIcon(isFavored == 1 ?
                    R.drawable.ic_favorite_black_24dp :
                    R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private class AddRemoveFavoritesAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return Utility.isFavorite(activity, movie.getID());
        }

        @Override
        protected void onPostExecute(Integer isFavorite) {
            //If movie is in favorites list
            if (isFavorite == 1) {
                // Delete from favorites list
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... params) {
                        return activity.getContentResolver().delete(
                                MovieContract.FavEntry.CONTENT_URI,
                                MovieContract.FavEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{Integer.toString(movie.getID())}
                        );
                    }

                    @Override
                    protected void onPostExecute(Integer rowsDeleted) {
                        favorite.setIcon(R.drawable.ic_favorite_border_black_24dp);

                        snackbar = Snackbar.make(rootView, activity.getString(R.string.remove_favorite_snackbar), Snackbar.LENGTH_LONG)
                                .setAction(activity.getString(R.string.snackbar_action), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        MainActivityFragment.CHOICE="favorite";

                                        //If device is a Tablet. just refresh the left pane
                                        if(MainActivity.isTablet) {
                                            new Favorite(activity, movieList);
                                        }

                                        else {
                                            Intent i = new Intent(activity, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            activity.startActivity(i);
                                            MainActivityFragment.CHOICE = "favorite";
                                        }
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                        //Refresh Favorites list
                        if(MainActivity.isTablet && MainActivityFragment.CHOICE.contentEquals("favorite")) {
                            new Favorite(activity, movieList);
                        }
                    }
                }.execute();
            }

            // If movie is not in favorites list
            else {
                // Add it to favorites list
                new AsyncTask<Void, Void, Uri>() {
                    @Override
                    protected Uri doInBackground(Void... params) {
                        ContentValues values = new ContentValues();

                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_ID, movie.getID());
                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                        values.put(MovieContract.FavEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                        values.put(MovieContract.FavEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                        values.put(MovieContract.FavEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAvg());
                        values.put(MovieContract.FavEntry.COLUMN_PLOT, movie.getOverview());

                        return activity.getContentResolver().insert(MovieContract.FavEntry.CONTENT_URI, values);
                    }

                    @Override
                    protected void onPostExecute(Uri returnUri) {
                        favorite.setIcon(R.drawable.ic_favorite_black_24dp);

                        snackbar = Snackbar.make(rootView, activity.getString(R.string.add_favorite_snackbar), Snackbar.LENGTH_LONG)
                                            .setAction(activity.getString(R.string.snackbar_action), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    MainActivityFragment.CHOICE="favorite";

                                                    //If device is a Tablet. just refresh the left pane
                                                    if(MainActivity.isTablet)
                                                        new Favorite(activity, movieList);

                                                    else {
                                                        Intent i = new Intent(activity, MainActivity.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        activity.startActivity(i);
                                                        MainActivityFragment.CHOICE = "favorite";
                                                    }
                                                }
                                            });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                        //Refresh Favorites list
                        if(MainActivity.isTablet && MainActivityFragment.CHOICE.contentEquals("favorite")) {
                            new Favorite(activity, movieList);
                        }
                    }
                }.execute();
            }
        }
    }

    private class FetchFavoritesAsyncTask extends AsyncTask<String, Void, List<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            Cursor cursor = activity.getContentResolver().query(
                    MovieContract.FavEntry.CONTENT_URI,
                    FAV_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {

            if (movies != null) {
                if (MainActivityFragment.imageAdapter != null) {
                    MainActivityFragment.imageAdapter.setData(movies);
                }
                movieList = new ArrayList<>();
                movieList.addAll(movies);

                //to update detail (right) pane of favorites list
                if(MainActivity.isTablet && !MainActivityFragment.imageAdapter.isEmpty())
                    ((MainActivityFragment.Callback) activity).onItemSelected(MainActivityFragment.imageAdapter.getItem(0));
            }
        }

        private List<MovieModel> getFavMoviesFromCursor(Cursor cursor) {
            List<MovieModel> results = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieModel movie = new MovieModel(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }
}