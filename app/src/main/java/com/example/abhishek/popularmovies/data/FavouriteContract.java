package com.example.abhishek.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abhishek on 09/09/17.
 */

public final class FavouriteContract {


    public static final String CONTENT_AUTHORITY = "com.example.abhishek.popularmovies";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri BASE_URI = Uri.withAppendedPath(CONTENT_URI, FavouriteEntries.TABLE_NAME);

    public static class FavouriteEntries implements BaseColumns {

        public static final String TABLE_NAME = "favourite_movies";

        public static String COLUMN_NAME = "movie_name";
        public static String COLUMN_DESCRIPTION = "movie_description";
        public static String COLUMN_VOTES = "movie_votes";
        public static String COLUMN_POSTER = "movie_poster";
        public static String COLUMN_MOVIE_ID = "movie_id";
        public static String COLUMN_MOVIE_RELEASED_DATE = "release_date";
        public static String COLUMN_BACKDROP_IMAGE = "backdrop_image";

    }

}
