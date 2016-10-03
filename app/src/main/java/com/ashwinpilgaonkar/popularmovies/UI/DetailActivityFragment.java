package com.ashwinpilgaonkar.popularmovies.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashwinpilgaonkar.popularmovies.R;
import com.squareup.picasso.Picasso;

public class DetailActivityFragment extends Fragment {

    View v;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        initUI();
        return v;
    }

    private void initUI(){
        //Initialize UI Elements
        ImageView poster = (ImageView) v.findViewById(R.id.poster);
        TextView title_textView = (TextView) v.findViewById(R.id.title_textView);
        TextView rdate_textView = (TextView) v.findViewById(R.id.rdate_textView);
        TextView rating_textView = (TextView) v.findViewById(R.id.rating_textView);
        TextView overview_textView = (TextView) v.findViewById(R.id.overview_textView);

        //Retrieve messages passed
        Bundle extras = getActivity().getIntent().getExtras();
        String POSTER_PATH = extras.getString("POSTER_PATH");
        String TITLE = extras.getString("TITLE");
        String OVERVIEW = extras.getString("OVERVIEW");
        String RDATE = extras.getString("RDATE");
        String RATING = extras.getString("RATING");

        //Update UI
        Picasso.with(getActivity()).load(POSTER_PATH).into(poster);
        title_textView.setText(TITLE);
        rdate_textView.setText("Release Date: "+RDATE);
        rating_textView.setText("Average Rating: "+RATING+"/10");
        overview_textView.setText(OVERVIEW);
    }
}
