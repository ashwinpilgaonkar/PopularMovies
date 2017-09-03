package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashwinpilgaonkar.popularmovies.Adapters.ReviewAdapter;
import com.ashwinpilgaonkar.popularmovies.Adapters.TrailerAdapter;
import com.ashwinpilgaonkar.popularmovies.Backend.FetchData;
import com.ashwinpilgaonkar.popularmovies.Backend.Utility;
import com.ashwinpilgaonkar.popularmovies.ContentProvider.MovieContract;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.Models.ReviewModel;
import com.ashwinpilgaonkar.popularmovies.Models.TrailerModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailFragment extends Fragment {

    View v;

    private TrailerModel trailerModel;
    private CardView trailersCardView;
    private TrailerAdapter trailerAdapter;

    private ReviewModel reviewModel;
    private CardView reviewsCardView;
    private ReviewAdapter reviewAdapter;

    String ID;
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    public static final String TAG = "DetailActivityFragment";

    private MovieModel mMovie;
    private Toast mToast;
    private TrailerModel mTrailer;
    private ScrollView mDetailLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovie = arguments.getParcelable(DETAIL_MOVIE);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        mDetailLayout = (ScrollView) rootView.findViewById(R.id.activity_movie_detail);

        if (mMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }
        initializeUI();
        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (mMovie != null) {
            Log.e("ASD", "movie");
            inflater.inflate(R.menu.menu_detail, menu);
            final MenuItem action_favorite = menu.findItem(R.id.action_favorite);
            MenuItem action_share = menu.findItem(R.id.action_share);

            //set proper icon on toolbar for favored movies
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.isFavored(getActivity(), mMovie.getID());
                }

                @Override
                protected void onPostExecute(Integer isFavored) {
                    action_favorite.setIcon(isFavored == 1 ?
                            R.drawable.ic_favorite_black_24dp :
                            R.drawable.ic_favorite_border_black_24dp);
                }
            }.execute();
        }

    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                if (mMovie != null) {
                    // check if movie is favored or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.isFavored(getActivity(), mMovie.getID());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavored) {
                            // if it is in favorites
                            if (isFavored == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.FavEntry.CONTENT_URI,
                                                MovieContract.FavEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mMovie.getID())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                "Removed from Favorites", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_ID, mMovie.getID());
                                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
                                        values.put(MovieContract.FavEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
                                        values.put(MovieContract.FavEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
                                        values.put(MovieContract.FavEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAvg());
                                        values.put(MovieContract.FavEntry.COLUMN_PLOT, mMovie.getOverview());

                                        return getActivity().getContentResolver().insert(MovieContract.FavEntry.CONTENT_URI, values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                "Added to Favorites", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;

            case R.id.action_share:
                //share movie trailer
                shareTrailer();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Fetch Trailers and Reviews
        new FetchData(1, v, getActivity(), ID);
    }

    private void initializeUI(){
        //Initialize Movie UI Elements
        ImageView poster = (ImageView) v.findViewById(R.id.poster);
        TextView title_textView = (TextView) v.findViewById(R.id.title_textView);
        TextView rdate_textView = (TextView) v.findViewById(R.id.rdate_textView);
        TextView rating_textView = (TextView) v.findViewById(R.id.rating_textView);
        TextView overview_textView = (TextView) v.findViewById(R.id.overview_textView);

        //Trailer elements
        trailersCardView = (CardView) v.findViewById(R.id.detail_trailers_cardview);
        LinearListView trailersListView = (LinearListView) v.findViewById(R.id.trailers_ListView);
        trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<TrailerModel>());
        trailersListView.setAdapter(trailerAdapter);

        //Review elements
        reviewsCardView = (CardView) v.findViewById(R.id.detail_reviews_cardview);
        LinearListView reviewsListView = (LinearListView) v.findViewById(R.id.reviews_ListView);
        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<ReviewModel>());
        reviewsListView.setAdapter(reviewAdapter);

        //Retrieve messages passed
        Bundle extras = getActivity().getIntent().getExtras();
        String POSTER_PATH = extras.getString("POSTER_PATH");
        String TITLE = extras.getString("TITLE");
        String OVERVIEW = extras.getString("OVERVIEW");
        String RDATE = extras.getString("RDATE");
        String RATING = extras.getString("RATING");
        ID = extras.getString("ID");

        //Update UI
        Picasso.with(getActivity()).load(POSTER_PATH).into(poster);
        title_textView.setText(TITLE);
        rdate_textView.setText(RDATE);
        rating_textView.setText(RATING+"/10");
        overview_textView.setText(OVERVIEW);
    }

    private void shareTrailer() {

        if (mTrailer != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getTitle() + "\n" +
                    "http://www.youtube.com/watch?v=" + mTrailer.getKey()
                    + "\n'Shared Via Popular Movies App, Data is Sourced form http://themoviedb.org/' ");
            sendIntent.setType("text/plain");

            startActivity(Intent.createChooser(sendIntent, "Share Using"));
        }else {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), "Wait for Trailers to Load", Toast.LENGTH_SHORT);
            mToast.show();
        }

    }
}