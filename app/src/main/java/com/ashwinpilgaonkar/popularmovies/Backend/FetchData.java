package com.ashwinpilgaonkar.popularmovies.Backend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.ashwinpilgaonkar.popularmovies.Adapters.ReviewAdapter;
import com.ashwinpilgaonkar.popularmovies.Adapters.TrailerAdapter;
import com.ashwinpilgaonkar.popularmovies.BuildConfig;
import com.ashwinpilgaonkar.popularmovies.Models.ReviewModel;
import com.ashwinpilgaonkar.popularmovies.Models.TrailerModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.linearlistview.LinearListView;

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
import java.util.concurrent.ExecutionException;

public class FetchData {

    private int type;
    private String ID; //Can store either MovieID or sort criteria (popular/toprated)
    public String MovieJSONData;

    private TrailerModel trailerModel;
    private CardView trailersCardView;
    private TrailerAdapter trailerAdapter;

    private ReviewModel reviewModel;
    private CardView reviewsCardView;
    private ReviewAdapter reviewAdapter;

    /*type = 0 -> Fetch Movies
      type = 1 -> Fetch Trailers
      type = 2 -> Fetch Reviews
     */

    public FetchData(int type, View v, final Context context, String ID){
        this.type = type;
        this.ID = ID;

        if(type==0)
            try {
                //.get() gets the list returned from the AstncTask. .get(0) gets the required string from the list index 0. (String) is used to cast the Object into a String.
                MovieJSONData = (String)new FetchDataAsyncTask().execute(ID).get().get(0);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }

        else {
            //Trailer elements
            trailersCardView = (CardView) v.findViewById(R.id.detail_trailers_cardview);
            LinearListView trailersListView = (LinearListView) v.findViewById(R.id.trailers_ListView);
            trailerAdapter = new TrailerAdapter(context, new ArrayList<TrailerModel>());
            trailersListView.setAdapter(trailerAdapter);

            //Review elements
            reviewsCardView = (CardView) v.findViewById(R.id.detail_reviews_cardview);
            LinearListView reviewsListView = (LinearListView) v.findViewById(R.id.reviews_ListView);
            reviewAdapter = new ReviewAdapter(context, new ArrayList<ReviewModel>());
            reviewsListView.setAdapter(reviewAdapter);

            new FetchDataAsyncTask().execute(ID);

            trailersListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, long id) {
                    TrailerModel TrailerModel = trailerAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + TrailerModel.getKey()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public class FetchDataAsyncTask extends AsyncTask<String, Void, List<Object>> {

        private final String LOG_TAG = "TrailersAsyncTask";

        @Override
        protected List<Object> doInBackground(String... params) {

            if (params.length == 0 ){
                Log.d(LOG_TAG,"params length 0");
                return null;
            }

            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String JSONString = null;

            try {

                final String BASE_URL;

                switch (type){
                    case 0: BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0]; //params[0] contains sort criteria (popular/top rated)
                            break;

                    case 1: BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos"; //MovieID passed as params[0]
                            break;

                    case 2: BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews"; //MovieID passed as params[0]
                            break;

                    default: BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0]; //case 0 is default
                }

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                    return null;

                JSONString = buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG,"IOException Error " + e);
                return null;
            }

            finally {

                if (httpURLConnection != null)
                    httpURLConnection.disconnect();

                if (reader != null){
                    try {
                        reader.close();
                    }

                    catch (IOException e) {
                        Log.e(LOG_TAG,"IOException Error " + e);
                    }
                }
            }

            //Get Movie/Trailers/Reviews from JSON
            try {
                    //Items defined below can contain data of type Review as well as Trailer
                    JSONObject trailerReviewJSON = new JSONObject(JSONString);
                    JSONArray trailerReviewArray = trailerReviewJSON.getJSONArray("results");
                    List<Object> trailerReviewList = new ArrayList<>();

                    List<Object> JSONStr;

                    switch (type){

                        //Put Movie JSON Response String into ArrayList[0] as return type is List<Object>
                        case 0: JSONStr = new ArrayList<>();
                                JSONStr.add(JSONString);
                                return JSONStr;

                        case 1:  for(int i=0; i<trailerReviewArray.length(); i++) {
                                    JSONObject trailer = trailerReviewArray.getJSONObject(i);

                                    //This will filter the trailer list to show only those on YouTube
                                    if (trailer.getString("site").contentEquals("YouTube")) {
                                         TrailerModel trailerModel = new TrailerModel(trailer);
                                         trailerReviewList.add(trailerModel);
                                     }
                                 }
                                 return trailerReviewList;

                    case 2:  for(int i=0; i<trailerReviewArray.length(); i++) {
                                JSONObject review = trailerReviewArray.getJSONObject(i);
                                trailerReviewList.add(new ReviewModel(review));
                            }
                            return trailerReviewList;


                    default: JSONStr = new ArrayList<>();
                             JSONStr.add(JSONString);
                             return JSONStr;
                }
            }

            catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException Error " + e );
            }

            //Default return type in case fetch attempts fail
            return null;
        }

        @Override
        protected void onPostExecute(List<Object> trailerReviewList) {

            if (trailerReviewList.size()>0) {

                //If data passed is Trailer
                if (type == 1) {
                    //Make CardView Visible to show fetched trailers
                    trailersCardView.setVisibility(View.VISIBLE);
                    if (trailerAdapter != null) {
                        trailerAdapter.remove();

                        //For each trailer in the list, add it to the trailer adapter by type casting the Object
                        for (Object trailer : trailerReviewList)
                            trailerAdapter.add((TrailerModel)trailer);
                    }

                    trailerModel = (TrailerModel)trailerReviewList.get(0);

                    //After Trailers are fetched, execute same AsyncTask again to fetch Reviews
                    type=2;
                    new FetchDataAsyncTask().execute(ID);
                }

                //If data passed is Review
                else if (type == 2) {
                    //Make CardView Visible to show fetched reviews
                    reviewsCardView.setVisibility(View.VISIBLE);
                    if (reviewAdapter != null) {
                        reviewAdapter.remove();

                        //For each review in the list, add it to the trailer adapter by type casting the Object
                        for (Object review : trailerReviewList)
                            reviewAdapter.add((ReviewModel)review);
                    }

                    reviewModel = (ReviewModel)trailerReviewList.get(0);
                }
            }
        }
    }
}
