package com.example.abhishek.popularmovies;

import android.graphics.Bitmap;

/**
 * Created by abhishek on 03/07/17.
 */

public class Movies {

    private String title;
    private String description;
    private String releaseDate;
    private double voteAvg;
    private Bitmap moviePoster;
    private Bitmap backdropImage;
    private int movieId;

    public Movies(String title,
                  String description,
                  int movieId,
                  String releaseDate,
                  double voteAvg,
                  Bitmap moviePoster,
                  Bitmap backdropImage) {

        this.title = title;
        this.description = description;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
        this.voteAvg = voteAvg;
        this.moviePoster = moviePoster;
        this.backdropImage = backdropImage;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAvg() {
        return voteAvg;
    }

    public Bitmap getMoviePoster() {
        return moviePoster;
    }

    public int getMovieId() {
        return movieId;
    }

    public Bitmap getBackdropImage() {
        return backdropImage;
    }
}
