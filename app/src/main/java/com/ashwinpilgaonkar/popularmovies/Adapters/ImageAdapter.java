package com.ashwinpilgaonkar.popularmovies.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private String[] image;

    public ImageAdapter(Context context, String[] img) {
        this.context = context;
        this.image = img;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int position) {
        return image[position];
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

        Picasso.with(context).load(image[position]).into(posterImage);

        return posterImage;
    }
}
