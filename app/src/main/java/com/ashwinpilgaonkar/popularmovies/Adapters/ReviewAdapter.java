package com.ashwinpilgaonkar.popularmovies.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ashwinpilgaonkar.popularmovies.Models.ReviewModel;
import com.ashwinpilgaonkar.popularmovies.R;
import com.ashwinpilgaonkar.popularmovies.UI.MainActivity;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private final LayoutInflater LayoutInflater;
    private final ReviewModel Review = new ReviewModel();
    private List<ReviewModel> ReviewObjects;

    public ReviewAdapter(Context context, List<ReviewModel> ReviewObjects) {
        LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ReviewObjects = ReviewObjects;
    }

    public void add(ReviewModel object){
        synchronized (Review){
            ReviewObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (Review){
            ReviewObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ReviewObjects.size();
    }

    @Override
    public ReviewModel getItem(int position) {
        return ReviewObjects.get(position);
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
            view = LayoutInflater.inflate(R.layout.review_layout, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }

        final ReviewModel review = getItem(position);
        viewholder = (ViewHolder) view.getTag();

        viewholder.author.setText(review.getAuthor());

        if(MainActivity.theme.contentEquals("light"))
            viewholder.author.setTextColor(0xff000000);

        viewholder.content.setText(Html.fromHtml(review.getContent()));

        return view;
    }

    private static class ViewHolder {
        final TextView author;
        public final TextView content;

        ViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.review_author);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }
}