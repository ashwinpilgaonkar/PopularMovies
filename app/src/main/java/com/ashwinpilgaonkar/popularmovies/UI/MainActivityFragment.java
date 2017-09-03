package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ashwinpilgaonkar.popularmovies.Adapters.ImageAdapter;
import com.ashwinpilgaonkar.popularmovies.Backend.FetchData;
import com.ashwinpilgaonkar.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityFragment extends Fragment {

    //Initialize Global Variables
    private String[] poster_path= new String[30];
    private JSONArray results;
    private String TITLE;
    private String OVERVIEW;
    private String RDATE;
    private String RATING;
    private String ID;
    View v;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main_activity, container, false);

        //Check if Intenet connectivity is available on startup to avoid app crash.
        checkNetworkConnectivity();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("About");
            builder.setMessage("Popular Movies App Stage 2 \n\nUdacity's Developing Android Apps Nanodegree Program \n\nCreated by Ashwin Pilgaonkar ");

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

        FetchData fetchData = new FetchData(0, v, getActivity(), choice);

        try {
            JSONObject jsonObject = new JSONObject(fetchData.MovieJSONData); //Contains all data of JSON String
            results = jsonObject.getJSONArray("results"); //Contains results of all movies in an array

            for(int i=0; i<results.length(); i++){
                JSONObject movie = results.getJSONObject(i); //Extracts info of each movie
                poster_path[i] = "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path");
            }
        }
        catch (JSONException e) {
            Log.e("ASD", "JSONException");
        }

        //Update GridView with images
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getActivity(), poster_path));

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
                    ID = selectedMovie.getString("id");

                }
                catch (JSONException e) {
                    Toast toast = Toast.makeText(getActivity(), "JSONException Error!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)  //implicit intent to launch MovieDetailActivity
                        .putExtra("POSTER_PATH", poster_path[position])
                        .putExtra("TITLE", TITLE)
                        .putExtra("OVERVIEW", OVERVIEW)
                        .putExtra("RDATE", RDATE)
                        .putExtra("RATING", RATING)
                        .putExtra("ID", ID);

                startActivity(intent);
            }
        });
    }

    private void checkNetworkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Check for Network Connectivity to prevent app crash in case no network connectivity is available
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Error");
            builder.setMessage("No internet connectivity found");

            builder.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetworkConnectivity();
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
