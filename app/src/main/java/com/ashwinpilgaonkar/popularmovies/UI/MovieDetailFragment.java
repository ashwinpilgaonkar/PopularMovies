package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ashwinpilgaonkar.popularmovies.UI.MainActivity.isTablet;

public class MovieDetailFragment extends Fragment {

    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    public static final String TAG = "MovieDetailFragment";

    private String ID;

    public static TrailerModel trailerModel;
    private MovieModel movies;
    private MenuItem share;
    
    @BindView(R.id.detail_fragment_container) NestedScrollView DetailViewRoot;
    @BindView(R.id.detail_toolbar) Toolbar toolbar;

    //Initialize Movie UI Elements
    @BindView(R.id.backdrop_image) ImageView backdrop;
    @BindView(R.id.poster_image) ImageView poster;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.release_date) TextView movieRdate;
    @BindView(R.id.movie_rating) TextView movieRating;
    @BindView(R.id.movie_plot) TextView moviePlot;

    @BindView(R.id.trailers_list) LinearListView trailersListView;
    @BindView(R.id.reviews_list) LinearListView reviewsListView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Set theme again to avoid glitches when screen is rotated
        if (MainActivity.theme.contentEquals("light"))
            getActivity().setTheme(R.style.MovieTheme_Detail_Light);

        else
            getActivity().setTheme(R.style.MovieTheme_Detail);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, v);

        //Add up button to ActionBar
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if(!isTablet)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getActivity().setTitle("");

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(movieTitle.getText());
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });

        Bundle arguments = getArguments();

        if (arguments != null)
            movies = arguments.getParcelable(DETAIL_MOVIE);

        if (movies != null) {
            DetailViewRoot.setVisibility(View.VISIBLE);
            initializeUI();
        }

        else
            DetailViewRoot.setVisibility(View.INVISIBLE);

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (movies != null) {
            inflater.inflate(R.menu.menu_detail, menu);
            final MenuItem favorite = menu.findItem(R.id.action_favorite);
            share = menu.findItem(R.id.action_share);

            //Check if movie is in favorites list and accordingly set the appropriate icon
            new Favorite(getActivity(), movies, favorite, 0);
        }

    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_favorite:
                if (movies != null)
                    // Check if current Movie is in favorites list or not
                    new Favorite(getActivity(), movies, item, 1);

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
            //fetch movie trailers and reviews
            FetchData fetchData = new FetchData(getActivity());
            fetchData.getTrailers(ID);
            fetchData.getReviews(ID);
            fetchData.getBackdropimg(ID);
        }
    }

    private void initializeUI() {

        //Trailer elements
        TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<TrailerModel>());
        trailersListView.setAdapter(trailerAdapter);

        //Review elements
        ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<ReviewModel>());
        reviewsListView.setAdapter(reviewAdapter);

        //get data elements from MovieModel object
        ID = String.valueOf(movies.getID());
        String POSTER_PATH = Utility.buildPosterURL(movies.getPosterPath());
        String TITLE = movies.getTitle();
        String OVERVIEW = movies.getOverview();
        String RDATE = movies.getReleaseDate();
        String RATING = movies.getVoteAvg();

        //Update UI elements
        Picasso.with(getActivity()).load(POSTER_PATH).into(poster);
        movieTitle.setText(TITLE);
        movieRdate.setText(RDATE);
        movieRating.setText(RATING);
        moviePlot.setText(OVERVIEW);
    }

    private void ShareTrailer() {

        if (trailerModel != null) {
            share.setEnabled(true);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, movies.getTitle() + "\n" +
                    "http://www.youtube.com/watch?v=" + trailerModel.getKey()
                    + "\n\n" + getString(R.string.trailer_share_text));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via:"));
        }

        else
            share.setEnabled(false);
    }
}