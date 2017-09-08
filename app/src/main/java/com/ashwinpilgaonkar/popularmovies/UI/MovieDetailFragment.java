package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashwinpilgaonkar.popularmovies.Adapters.ReviewAdapter;
import com.ashwinpilgaonkar.popularmovies.Adapters.TrailerAdapter;
import com.ashwinpilgaonkar.popularmovies.Backend.Favorite;
import com.ashwinpilgaonkar.popularmovies.Backend.FetchData;
import com.ashwinpilgaonkar.popularmovies.Backend.Utility;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.Models.ReviewModel;
import com.ashwinpilgaonkar.popularmovies.Models.TrailerModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailFragment extends Fragment {

    private View v;
    private String ID;
    public static TrailerModel trailerModel;
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    public static final String TAG = "MovieDetailFragment";

    private MovieModel movies;
    private Toast mToast;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail, container, false);

        //Add up button to ActionBar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if(!MainActivity.isTablet)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getActivity().setTitle("");

        Bundle arguments = getArguments();

        if (arguments != null)
            movies = arguments.getParcelable(DETAIL_MOVIE);

        CoordinatorLayout DetailPage = (CoordinatorLayout) v.findViewById(R.id.activity_movie_detail);

        if (movies != null) {
            DetailPage.setVisibility(View.VISIBLE);
            initializeUI();
        }

        else
            DetailPage.setVisibility(View.INVISIBLE);

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (movies != null) {
            inflater.inflate(R.menu.menu_detail, menu);
            final MenuItem favorite = menu.findItem(R.id.action_favorite);

            //Check if movie is in favorites list and accordingly set the appropriate icon
            new Favorite(getContext(), movies, favorite, 0);
        }

    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_favorite:
                if (movies != null)
                    // Check if current Movie is in favorites list or not
                    new Favorite(getContext(), movies, item, 1);

                return true;

            case R.id.action_share:
                ShareTrailer();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(movies != null){
            /* Fetch Trailers and Reviews
             * Passing 1 as an arguement starts fetching Trailers
             * Once it's done, onPostExecutes re-executes the AsyncTask with 2 as an arguement to fetch Reviews
             */
            new FetchData(1, v, getActivity(), ID);
        }
    }

    private void initializeUI() {
        //Initialize Movie UI Elements
        ImageView backdrop = (ImageView) v.findViewById(R.id.backdrop_image);
        ImageView poster = (ImageView) v.findViewById(R.id.poster_image);
        TextView title_textView = (TextView) v.findViewById(R.id.movie_title);
        TextView rdate_textView = (TextView) v.findViewById(R.id.release_date);
        TextView rating_textView = (TextView) v.findViewById(R.id.movie_rating);
        TextView overview_textView = (TextView) v.findViewById(R.id.movie_plot);

        //Trailer elements
        LinearListView trailersListView = (LinearListView) v.findViewById(R.id.trailers_list);
        TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<TrailerModel>());
        trailersListView.setAdapter(trailerAdapter);

        //Review elements
        LinearListView reviewsListView = (LinearListView) v.findViewById(R.id.reviews_list);
        ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<ReviewModel>());
        reviewsListView.setAdapter(reviewAdapter);

        //get data elements from MovieModel object
        ID = String.valueOf(movies.getID());
        String POSTER_PATH = Utility.buildBackdropURL(movies.getPosterPath());
        String TITLE = movies.getTitle();
        String OVERVIEW = movies.getOverview();
        String RDATE = movies.getReleaseDate();
        String RATING = movies.getVoteAvg();

        //Update UI elements
        Picasso.with(getActivity()).load(POSTER_PATH).into(backdrop);
        Picasso.with(getActivity()).load(POSTER_PATH).into(poster);
        title_textView.setText(TITLE);
        rdate_textView.setText(RDATE);
        rating_textView.setText(RATING);
        overview_textView.setText(OVERVIEW);
    }

    private void ShareTrailer() {

        if (trailerModel != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, movies.getTitle() + "\n" +
                    "http://www.youtube.com/watch?v=" + trailerModel.getKey()
                    + "\n\n" + getString(R.string.trailer_share_text));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via:"));
        }

        else {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), "Please wait for trailers to load", Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}