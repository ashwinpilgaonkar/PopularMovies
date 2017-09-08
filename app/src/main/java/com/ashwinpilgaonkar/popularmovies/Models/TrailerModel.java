package com.ashwinpilgaonkar.popularmovies.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class TrailerModel {
    private String id;
    private String key;
    private String name;

    public TrailerModel() {
    }

    public TrailerModel(JSONObject trailers) throws JSONException {
        this.id = trailers.getString("id");
        this.key = trailers.getString("key");
        this.name = trailers.getString("name");
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
