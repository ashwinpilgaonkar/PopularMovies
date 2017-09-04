package com.ashwinpilgaonkar.popularmovies.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ashwinpilgaonkar.popularmovies.R;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Info");

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_MOVIE,
                    getIntent().getParcelableExtra(MovieDetailFragment.DETAIL_MOVIE));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.MovieListFragment, fragment)
                    .commit();
        }
    }
}
