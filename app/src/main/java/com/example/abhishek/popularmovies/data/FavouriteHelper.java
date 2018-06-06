package com.example.abhishek.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abhishek.popularmovies.data.FavouriteContract.FavouriteEntries;

/**
 * Created by abhishek on 09/09/17.
 */

public class FavouriteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourite_movies.db";
    private static final int DATABASE_VERSION = 2;

    public FavouriteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " +
                FavouriteEntries.TABLE_NAME +
                "( " +
                FavouriteEntries._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouriteEntries.COLUMN_NAME +
                " TEXT NOT NULL, " +
                FavouriteEntries.COLUMN_DESCRIPTION +
                " TEXT NOT NULL, " +
                FavouriteEntries.COLUMN_VOTES +
                " INTEGER, " +
                FavouriteEntries.COLUMN_MOVIE_ID +
                " INTEGER, " +
                FavouriteEntries.COLUMN_BACKDROP_IMAGE +
                " BLOB, " +
                FavouriteEntries.COLUMN_MOVIE_RELEASED_DATE +
                " TEXT NOT NULL, " +
                FavouriteEntries.COLUMN_POSTER +
                " BLOB, UNIQUE(" +
                FavouriteEntries.COLUMN_MOVIE_ID +
                ") ON CONFLICT REPLACE);";

        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
