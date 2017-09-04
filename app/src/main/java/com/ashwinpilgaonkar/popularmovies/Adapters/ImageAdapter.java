package com.ashwinpilgaonkar.popularmovies.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ashwinpilgaonkar.popularmovies.Backend.Utility;
import com.ashwinpilgaonkar.popularmovies.Models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private final MovieModel mMovie = new MovieModel();
    private List<MovieModel> MovieObjects;

    public ImageAdapter(Context context, List<MovieModel> MovieObjects) {
        this.context = context;
        this.MovieObjects = MovieObjects;
    }

    public void add(MovieModel MovieObject){
        synchronized (mMovie){
            MovieObjects.add(MovieObject);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (mMovie){
            MovieObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<MovieModel> data){
        remove();
        for (MovieModel movie : data){
            add(movie);
        }
    }

    @Override
    public int getCount() {
        return MovieObjects.size();
    }

    @Override
    public MovieModel getItem(int position) {
        return MovieObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent) {
        ImageView posterImage;

        if (view == null) {
            // if it's not recycled, initialize some attributes

            posterImage = new ImageView(context);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            posterImage.setLayoutParams(new GridView.LayoutParams(params));

            posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            posterImage.setAdjustViewBounds(true);
            posterImage.setPadding(1,1,1,1);
        }

        else
            posterImage = (ImageView) view;

        String poster_url = Utility.buildPosterUrl(MovieObjects.get(position).getPosterPath());
        Picasso.with(context).load(poster_url).into(posterImage);

        return posterImage;
    }
}
