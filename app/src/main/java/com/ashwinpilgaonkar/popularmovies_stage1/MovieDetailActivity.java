package com.ashwinpilgaonkar.popularmovies_stage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Info");

        //Initialize UI Elements
        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView title_textView = (TextView) findViewById(R.id.title_textView);
        TextView rdate_textView = (TextView) findViewById(R.id.rdate_textView);
        TextView rating_textView = (TextView) findViewById(R.id.rating_textView);
        TextView overview_textView = (TextView) findViewById(R.id.overview_textView);

        //Retrieve messages passed
        Bundle extras = getIntent().getExtras();
        String POSTER_PATH = extras.getString("POSTER_PATH");
        String TITLE = extras.getString("TITLE");
        String OVERVIEW = extras.getString("OVERVIEW");
        String RDATE = extras.getString("RDATE");
        String RATING = extras.getString("RATING");

        //Update UI
        Picasso.with(this).load(POSTER_PATH).into(poster);
        title_textView.setText(TITLE);
        rdate_textView.setText("Release Date: "+RDATE);
        rating_textView.setText("Average Rating: "+RATING+"/10");
        overview_textView.setText(OVERVIEW);
    }

}
