package com.example.abhishek.popularmovies;

import android.graphics.Bitmap;

/**
 * Created by abhishek on 12/09/17.
 */

public class Trailers {

    private String movieKey;
    private Bitmap trailerThumbnail;

    public Trailers(String movieKey, Bitmap trailerThumbnail) {
        this.movieKey = movieKey;
        this.trailerThumbnail = trailerThumbnail;
    }

    public String getMovieKey() {
        return movieKey;
    }

    public Bitmap getTrailerThumbnail() {
        return trailerThumbnail;
    }

}
