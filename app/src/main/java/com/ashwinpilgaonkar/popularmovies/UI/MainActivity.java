package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ashwinpilgaonkar.popularmovies.Backend.GetData;
import com.ashwinpilgaonkar.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Initialize Global Variables
    private String[] poster_path= new String[30];
    private JSONArray results;
    private String TITLE;
    private String OVERVIEW;
    private String RDATE;
    private String RATING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check if Intenet connectivity is available on startup to avoid appcrash.
        isNetworkAvailable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_action_popular) {

            if (!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            updateUI("popular");

            return true;
        }

        if (id == R.id.menu_action_rating) {

            if(!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            updateUI("top_rated");

            return true;
        }

        if (id == R.id.menu_action_about) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("About");
            builder.setMessage("Popular Movies App Stage 1 \n\nUdacity's Developing Android Apps Nanodegree Program \n\nCreated by Ashwin Pilgaonkar ");

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(final String choice){

        GetData getData = new GetData(choice, getApplicationContext());

        try {
            JSONObject jsonObject = new JSONObject(getData.JSONData); //Contains all data of JSON String
            results = jsonObject.getJSONArray("results"); //Contains results of all movies in an array

            for(int i=0; i<results.length(); i++){
                JSONObject movie = results.getJSONObject(i); //Extracts info of each movie
                poster_path[i] = "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path");
            }
        }
        catch (JSONException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "JSONException Error!", Toast.LENGTH_SHORT);
            toast.show();
        }

        //Update GridView with images
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this, poster_path));


        //Check orientation of device and update UI accordingly
        if(getResources().getConfiguration().orientation==1)
            gridView.setNumColumns(2); //for Portrait

        else
            gridView.setNumColumns(4); //for Landscape

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                try {
                    JSONObject selectedMovie = results.getJSONObject(position);
                    TITLE = selectedMovie.getString("title");
                    OVERVIEW = selectedMovie.getString("overview");
                    RDATE = selectedMovie.getString("release_date");
                    RATING = selectedMovie.getString("vote_average");

                }
                catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "JSONException Error!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class)  //implicit intent to launch MovieDetailActivity
                        .putExtra("POSTER_PATH", poster_path[position])
                        .putExtra("TITLE", TITLE)
                        .putExtra("OVERVIEW", OVERVIEW)
                        .putExtra("RDATE", RDATE)
                        .putExtra("RATING", RATING);

                startActivity(intent);
            }
        });
    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Check for Network Connectivity to prevent app crash in case no network connectivity is available
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Error");
            builder.setMessage("No internet connectivity found");

            builder.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isNetworkAvailable();
                        }
                    });

            builder.setNegativeButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

            builder.show();
        }

        else
            updateUI("popular"); //Sort by popularity by Default when the app starts
    }
}
