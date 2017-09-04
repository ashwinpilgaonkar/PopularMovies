package com.ashwinpilgaonkar.popularmovies.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.ashwinpilgaonkar.popularmovies.Adapters.ImageAdapter;
import com.ashwinpilgaonkar.popularmovies.BuildConfig;
import com.ashwinpilgaonkar.popularmovies.ContentProvider.MovieContract;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {

    private MovieModel mMovie;

    //Initialize Global Variables
    private String[] poster_path= new String[30];
    private JSONArray results;
    private String TITLE;
    private String OVERVIEW;
    private String RDATE;
    private String RATING;
    private String ID;
    View v;

    private static final String[] FAV_COLUMNS = {
            MovieContract.FavEntry._ID,
            MovieContract.FavEntry.COLUMN_MOVIE_ID,
            MovieContract.FavEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavEntry.COLUMN_POSTER_PATH,
            MovieContract.FavEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavEntry.COLUMN_PLOT
    };

    private static final String CHOICE_SETTING_KEY = "choice";
    private static final String MOVIES_DATA_KEY = "movies";
    private static final String MOST_POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";

    private ImageAdapter imageAdapter;

    private String mChoice = MOST_POPULAR;
    private ArrayList<MovieModel> mMovies = null;

    public MainActivityFragment() {
    }

    public interface Callback {
        void onItemSelected(MovieModel movie);
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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CHOICE_SETTING_KEY)) {
                mChoice = savedInstanceState.getString(CHOICE_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_DATA_KEY);
                imageAdapter.setData(mMovies);
            } else {
                updateUI(mChoice);
            }
        } else {
            updateUI(mChoice);
        }

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

            updateUI(MOST_POPULAR);

            return true;
        }

        if (id == R.id.menu_action_rating) {

            if(!item.isChecked()) //To update RadioButton UI
                item.setChecked(true);

            updateUI(TOP_RATED);

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

        if (id == R.id.menu_action_favorites){
            mChoice = FAVORITE;
            updateUI(mChoice);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateUI(final String choice){

        if (!choice.contentEquals(FAVORITE)) {
            new FetchMovies().execute(choice);
        } else {
            new FetchFav(getActivity()).execute();
        }

        //new FetchData(0, v, getActivity(), choice);

        /*try {
            JSONObject jsonObject = new JSONObject(fetchData.MovieJSONData); //Contains all data of JSON String
            results = jsonObject.getJSONArray("results"); //Contains results of all movies in an array

            for(int i=0; i<results.length(); i++){
                JSONObject movie = results.getJSONObject(i); //Extracts info of each movie
                poster_path[i] = "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path");
            }
        }
        catch (JSONException e) {
            Log.e("ASD", "JSONException");
        }*/

        //Update GridView with images
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getActivity(),  new ArrayList<MovieModel>());
        gridView.setAdapter(imageAdapter);

        //Check orientation of device and update UI accordingly
        if(getResources().getConfiguration().orientation==1)
            gridView.setNumColumns(2); //for Portrait

        else
            gridView.setNumColumns(4); //for Landscape

        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel movie = imageAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
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
            updateUI(MOST_POPULAR); //Sort by popularity by Default when the app starts
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mChoice.contentEquals(MOST_POPULAR)) {
            outState.putString(CHOICE_SETTING_KEY, mChoice);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_DATA_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    public class FetchMovies extends AsyncTask<String, Void, List<MovieModel>> {

        private final String LOG_TAG = "Fetch Movies";

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonResponseString = null;

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG,"Fetch movies started");
            super.onPreExecute();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            if (params.length == 0){
                Log.d(LOG_TAG, "Died - total Params length is 0");
                return null;
            }

            try {
                String choice = params[0];

                String BASE_URL = "http://api.themoviedb.org/3/movie/" + choice;
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                //added new line for pretty printing
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                jsonResponseString = buffer.toString();
                //Log.d(LOG_TAG,"Result :" + jsonResponseString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, "Error "+ e);
                    }
                }
            }

            try {
                return getMoviesFromJson(jsonResponseString);
            } catch (JSONException e) {
                Log.d(LOG_TAG,"Error " + e);
            }

            //if we failed everywhere this will be returned
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            //we got movies so let's show them
            //puts movies into adaptor
            if (movies != null) {
                if (imageAdapter != null) {
                    imageAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
            Log.d(LOG_TAG,"Post execute of Fetch Movies");
        }

        private List<MovieModel> getMoviesFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<MovieModel> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel(movie);
                results.add(movieModel);
            }

            //Log.d(LOG_TAG,results.toString());
            return results;
        }
    }

    public class FetchFav extends AsyncTask<String, Void, List<MovieModel>> {

        private Context mContext;

        //constructor
        public FetchFav(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.FavEntry.CONTENT_URI,
                    FAV_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            //we got Fav movies so let's show them
            if (movies != null) {
                if (imageAdapter != null) {
                    imageAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }

        private List<MovieModel> getFavMoviesFromCursor(Cursor cursor) {
            List<MovieModel> results = new ArrayList<>();
            //if we have data in database for Fav. movies.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieModel movie = new MovieModel(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }
}
