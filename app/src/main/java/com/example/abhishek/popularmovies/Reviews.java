package com.example.abhishek.popularmovies;


/**
 * Created by abhishek on 13/09/17.
 */

public class Reviews {

    private String reviewAuthor;
    private String reviewContent;

    public Reviews(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

}
