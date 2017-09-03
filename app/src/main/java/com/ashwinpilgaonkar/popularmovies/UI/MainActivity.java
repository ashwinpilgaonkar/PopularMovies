package com.ashwinpilgaonkar.popularmovies.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ashwinpilgaonkar.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private boolean mTabUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //detect an handle tablet mode with Two pane UI with master detail flow
        if (findViewById(R.id.fragment_MovieDetail) != null) {
            mTabUI = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_MovieDetail, new MovieDetailFragment())
                        .commit();
            }
        } else {
            mTabUI = false;
        }
    }

    // implements callback for Movie Item Click
    //if we have Tablet then update detail fragment into main activity otherwise launch detail
    // activity with an intent
    /*@Override
    public void onItemSelected(MovieModel movie) {
        if (mTabUI) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_MOVIE, movie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.MovieListFragment, fragment, MovieDetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }*/
}