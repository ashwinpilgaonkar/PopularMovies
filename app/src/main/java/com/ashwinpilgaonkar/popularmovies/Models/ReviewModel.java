package com.ashwinpilgaonkar.popularmovies.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewModel {

    private String id;
    private String author;
    private String content;

    public ReviewModel() {
    }

    public ReviewModel(JSONObject review) throws JSONException {
        this.id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
