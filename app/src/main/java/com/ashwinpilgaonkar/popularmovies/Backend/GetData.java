package com.ashwinpilgaonkar.popularmovies.Backend;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class GetData {

    public String JSONData;
    private Context context;

    public GetData(String choice, Context mContext){

        context = mContext;
        FetchDataTask fetchDataTask = new FetchDataTask();

        try {
            JSONData = fetchDataTask.execute(choice).get();
        }

        catch (InterruptedException e) {
            Toast toast = Toast.makeText(context, "InterruptedException Error!", Toast.LENGTH_SHORT);
            toast.show();
        }

        catch (ExecutionException e) {
            Toast toast = Toast.makeText(context, "ExecutionException Error!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            final String API_KEY = "API_KEY"; //Add your own API KEY Here

            if (params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the entire JSON String
            String MovieJsonStr = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "?api_key=" + API_KEY);

                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                    return null;

                MovieJsonStr = buffer.toString();

            }

            catch (IOException e) {
                Toast toast = Toast.makeText(context, "IOException Error!", Toast.LENGTH_SHORT);
                toast.show();
            }

            finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    }

                    catch (IOException e) {
                        Toast toast = Toast.makeText(context, "IOException Error!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
            return MovieJsonStr;
        }
    }
}