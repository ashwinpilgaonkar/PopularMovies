package com.ashwinpilgaonkar.popularmovies.Models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieModel implements Parcelable {

    private int id;
    private String title;
    private String release_date;
    private String poster_path;
    private String vote_avg;
    private String overview;

    public static final int ID = 0;
    public static final int MOVIE_ID = 1;
    public static final int MOVIE_TITLE = 2;
    public static final int RELEASE_DATE = 3;
    public static final int POSTER_PATH = 4;
    public static final int VOTE_AVERAGE = 5;
    public static final int OVERVIEW = 6;

    public MovieModel(){
    }

    public MovieModel(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.release_date = movie.getString("release_date");
        this.poster_path = movie.getString("poster_path");
        this.vote_avg = movie.getString("vote_average");
        this.overview = movie.getString("overview");
    }

    public MovieModel(Cursor cursor) {
        this.id = cursor.getInt(MOVIE_ID);
        this.title = cursor.getString(MOVIE_TITLE);
        this.release_date = cursor.getString(RELEASE_DATE);
        this.poster_path = cursor.getString(POSTER_PATH);
        this.vote_avg = cursor.getString(VOTE_AVERAGE);
        this.overview = cursor.getString(OVERVIEW);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(vote_avg);
        dest.writeString(overview);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    private MovieModel(Parcel in){
        id = in.readInt();
        title = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_avg = in.readString();
        overview = in.readString();
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAvg() {
        return vote_avg;
    }

    public String getReleaseDate() {
        return release_date;
    }
}