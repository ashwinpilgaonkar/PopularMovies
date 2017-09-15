package com.ashwinpilgaonkar.popularmovies.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ashwinpilgaonkar.popularmovies.R;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set theme
        if (MainActivity.theme.contentEquals(MainActivity.lightTheme))
            setTheme(R.style.MovieTheme_Detail_Light);

        else
            setTheme(R.style.MovieTheme_Detail);

        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_MOVIE,
                    getIntent().getParcelableExtra(MovieDetailFragment.DETAIL_MOVIE));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }
}