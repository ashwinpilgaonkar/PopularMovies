package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    public static boolean isTablet;
    @BindView(R.id.toolbar) Toolbar toolbar;

    public static String theme;
    SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSharedPrefs();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        isTablet = getResources().getBoolean(R.bool.isTab);

        //if device is a Tablet, use Master/Detail flow UI (two pane)
        if (isTablet) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_MovieDetail, new MovieDetailFragment(), MovieDetailFragment.TAG)
                        .commit();
            }
        }
    }

    //If device is a Tablet, load the detail fragment in the right pane
    //else use intent to launch detail activity
    @Override
    public void onItemSelected(MovieModel movie) {
        if (isTablet) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_MOVIE, movie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_MovieDetail, fragment, MovieDetailFragment.TAG)
                    .commit();
        }

        else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailFragment.DETAIL_MOVIE, movie);

            startActivity(intent);
        }
    }


    public void initSharedPrefs(){
        sharedPrefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        ed = sharedPrefs.edit();

        //Set default SharedPrefs
        if(!sharedPrefs.contains("theme")) {
            ed.putString("theme", "dark");
            ed.apply();
            theme = sharedPrefs.getString("theme", null);
        }

        else
            theme = sharedPrefs.getString("theme", null);

        //Set theme
        if (theme.contentEquals("light"))
            setTheme(R.style.MovieTheme_Light);

        else
            setTheme(R.style.MovieTheme);
    }
}