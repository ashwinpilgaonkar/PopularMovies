package com.ashwinpilgaonkar.popularmovies.Backend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ashwinpilgaonkar.popularmovies.Adapters.ReviewAdapter;
import com.ashwinpilgaonkar.popularmovies.Adapters.TrailerAdapter;
import com.ashwinpilgaonkar.popularmovies.BuildConfig;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.ashwinpilgaonkar.popularmovies.Models.ReviewModel;
import com.ashwinpilgaonkar.popularmovies.Models.TrailerModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.ashwinpilgaonkar.popularmovies.UI.MainActivity;
import com.ashwinpilgaonkar.popularmovies.UI.MainActivityFragment;
import com.ashwinpilgaonkar.popularmovies.UI.MovieDetailFragment;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*  This class is used to fetch data from REST APIs
 *  TMdb API is being used in this case to fetch
 *  Movie data, trailers and reviews
 *  Volley is used to handle HTTP Requests
 */

public class FetchData {

    private Activity activity;
    private List<MovieModel> MovieList = new ArrayList<>();
    private List<TrailerModel> TrailerList = new ArrayList<>();
    private List<ReviewModel> ReviewList = new ArrayList<>();
    @BindView(R.id.backdrop_image) ImageView backdrop;
    private String TAG = "FetchData";

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    @BindView(R.id.detail_trailers_cardview) CardView trailersCardView;
    @BindView(R.id.trailers_list) LinearListView trailersListView;
    @BindView(R.id.detail_reviews_cardview) CardView reviewsCardView;
    @BindView(R.id.reviews_list) LinearListView reviewsListView;

    public FetchData(Activity activity){
        this.activity = activity;
    }

    public void getMovies(String choice){

        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + choice;

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .build();

        String JSONURL = builtUri.toString();

        //GET Request
        StringRequest movieStringRequest = new StringRequest(Request.Method.GET, JSONURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String JSONStr) {

                        try {
                            //getting the whole json object from the response string
                            JSONObject movieJSONobj = new JSONObject(JSONStr);

                            //get results JSON object
                            JSONArray MoviesArray = movieJSONobj.getJSONArray("results");

                            //loop through every movie in the array
                            for (int i = 0; i < MoviesArray.length(); i++) {

                                //getting the json object of each movie
                                JSONObject movieObject = MoviesArray.getJSONObject(i);

                                //creating a movie object and giving it the values from json object
                                MovieModel movieModel = new MovieModel(movieObject);
                                MovieList.add(movieModel);
                            }

                            if (MovieList.size()>0) {

                                if (MainActivityFragment.imageAdapter != null) {
                                    MainActivityFragment.imageAdapter.setData(MovieList);
                                }
                                MainActivityFragment.movies = new ArrayList<>();
                                MainActivityFragment.movies.addAll(MovieList);

                                //Update DetailView with first movie by default if device is a Tablet
                                if (MainActivity.isTablet)
                                    ((MainActivityFragment.Callback) activity).onItemSelected(MainActivityFragment.imageAdapter.getItem(0));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.volley_error), Toast.LENGTH_LONG);
                        toast.show();
                        Log.e(TAG, String.valueOf(error));
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        //adding the string request to request queue
        requestQueue.add(movieStringRequest);

    }

    public void getTrailers(String id){

        ButterKnife.bind(this, activity);
        trailerAdapter = new TrailerAdapter(activity, new ArrayList<TrailerModel>());
        trailersListView.setAdapter(trailerAdapter);

        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/videos";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .build();

        String JSONURL = builtUri.toString();

        //GET Request
        StringRequest trailerStringRequest = new StringRequest(Request.Method.GET, JSONURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String JSONStr) {

                        try {
                            JSONObject trailerJSONobj = new JSONObject(JSONStr);
                            JSONArray TrailersArray = trailerJSONobj.getJSONArray("results");

                            for (int i=0; i < TrailersArray.length(); i++) {
                                JSONObject trailerObject = TrailersArray.getJSONObject(i);

                                //This will filter the trailer list to show only those on YouTube
                                if (trailerObject.getString("site").contentEquals("YouTube")) {
                                    TrailerModel trailerModel = new TrailerModel(trailerObject);
                                    TrailerList.add(trailerModel);
                                }
                            }

                            if (TrailerList.size()>0) {
                                //Make CardView Visible to show fetched trailers
                                trailersCardView.setVisibility(View.VISIBLE);
                                if (trailerAdapter != null) {
                                    trailerAdapter.remove();

                                    for (TrailerModel trailer : TrailerList)
                                        trailerAdapter.add(trailer);
                                }
                            }

                            if(!TrailerList.isEmpty()) {
                                MovieDetailFragment.trailerModel = TrailerList.get(0);
                            }

                            else
                                MovieDetailFragment.trailerModel = null;
                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.volley_error), Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e(TAG, String.valueOf(error));
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        requestQueue.add(trailerStringRequest);

        trailersListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                TrailerModel TrailerModel = trailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + TrailerModel.getKey()));
                activity.startActivity(intent);
            }
        });
    }

    public void getReviews(String id){

        ButterKnife.bind(this, activity);
        reviewAdapter = new ReviewAdapter(activity, new ArrayList<ReviewModel>());
        reviewsListView.setAdapter(reviewAdapter);

        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/reviews";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .build();

        String JSONURL = builtUri.toString();

        //GET Request
        StringRequest reviewStringRequest = new StringRequest(Request.Method.GET, JSONURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String JSONStr) {

                        try {
                            JSONObject reviewJSONobj = new JSONObject(JSONStr);
                            JSONArray ReviewsArray = reviewJSONobj.getJSONArray("results");

                            for (int i=0; i < ReviewsArray.length(); i++) {
                                JSONObject reviewObject = ReviewsArray.getJSONObject(i);
                                ReviewModel reviewModel = new ReviewModel(reviewObject);
                                ReviewList.add(reviewModel);
                            }

                            if (ReviewList.size()>0) {
                                //Make CardView Visible to show fetched reviews
                                reviewsCardView.setVisibility(View.VISIBLE);
                                if (reviewAdapter != null) {
                                    reviewAdapter.remove();

                                    for (ReviewModel review : ReviewList)
                                        reviewAdapter.add(review);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.volley_error), Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e(TAG, String.valueOf(error));
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(reviewStringRequest);
    }

    public void getBackdropimg(String id){
        ButterKnife.bind(this, activity);
        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/images";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .build();

        String JSONURL = builtUri.toString();

        //GET Request
        StringRequest backdropStringRequest = new StringRequest(Request.Method.GET, JSONURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String JSONStr) {

                        try {
                            JSONObject backdropJSONobj = new JSONObject(JSONStr);
                            JSONArray BackdropsArray = backdropJSONobj.getJSONArray("backdrops");

                            //get the path to a random backdrop image of the movie (we dont need to fetch all of them)
                            JSONObject backdropObject = BackdropsArray.getJSONObject((int)(Math.random()*BackdropsArray.length()-1));

                            String poster_url = Utility.buildBackdropURL(backdropObject.getString("file_path"));
                            Picasso.with(activity).load(poster_url).skipMemoryCache().into(backdrop);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.volley_error), Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e(TAG, String.valueOf(error));
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(backdropStringRequest);
    }
}
