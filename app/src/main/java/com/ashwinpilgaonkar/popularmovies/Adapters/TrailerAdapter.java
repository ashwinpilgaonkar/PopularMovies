package com.ashwinpilgaonkar.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashwinpilgaonkar.popularmovies.Models.TrailerModel;
import com.squareup.picasso.Picasso;
import com.ashwinpilgaonkar.popularmovies.R;

import java.util.List;

public class TrailerAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater LayoutInflater;
    private final TrailerModel Trailer = new TrailerModel();
    private List<TrailerModel> TrailerObjects;

    public TrailerAdapter(Context context, List<TrailerModel> TrailerObjects) {
        this.context = context;
        this.LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.TrailerObjects = TrailerObjects;
    }

    public void add(TrailerModel object){
        synchronized (Trailer){
            TrailerObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (Trailer){
            TrailerObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return TrailerObjects.size();
    }

    @Override
    public TrailerModel getItem(int position) {
        return TrailerObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewholder;

        if (view == null){
            view = LayoutInflater.inflate(R.layout.trailer_layout, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }

        final TrailerModel trailermodel = getItem(position);
        viewholder = (ViewHolder) view.getTag();

        String TrailerThumbnailURL = "https://img.youtube.com/vi/" + trailermodel.getKey() + "/0.jpg";
        Picasso.with(context).load(TrailerThumbnailURL).into(viewholder.thumbnail);
        viewholder.name.setText(trailermodel.getName());

        return view;
    }

    public static class ViewHolder {
        public final ImageView thumbnail;
        public final TextView name;

        public ViewHolder(View view) {
            thumbnail = (ImageView) view.findViewById(R.id.trailer_image);
            name = (TextView) view.findViewById(R.id.trailer_name);
        }
    }
}