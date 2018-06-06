package com.example.abhishek.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.abhishek.popularmovies.data.FavouriteContract.FavouriteEntries;

/**
 * Created by abhishek on 09/09/17.
 */

public class FavouriteProvider extends ContentProvider {
    private static final int FAVOURITE_NO_ID = 100;
    private static final int FAVOURITE_WITH_ID = 101;

    FavouriteHelper helper;
    UriMatcher matcher = getUriMatcher();

    @Override
    public boolean onCreate() {
        helper = new FavouriteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = matcher.match(uri);

        switch (match) {

            case FAVOURITE_NO_ID:

                projection = new String[]{FavouriteEntries.COLUMN_POSTER, FavouriteEntries.COLUMN_MOVIE_ID};

                cursor = db.query(FavouriteEntries.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVOURITE_WITH_ID:

                selection = FavouriteEntries.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(FavouriteEntries.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = helper.getWritableDatabase();
        long insertedId;
        int match = matcher.match(uri);

        switch (match) {

            case FAVOURITE_NO_ID:
                insertedId = db.insert(FavouriteEntries.TABLE_NAME,
                        null,
                        values);

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, insertedId);

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int match = matcher.match(uri);

        switch (match) {

            case FAVOURITE_WITH_ID:
                selection = FavouriteEntries.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return db.delete(FavouriteEntries.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Invalid uri: " + uri);

        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private UriMatcher getUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI
                (FavouriteContract.CONTENT_AUTHORITY, FavouriteEntries.TABLE_NAME, FAVOURITE_NO_ID);

        uriMatcher.addURI
                (FavouriteContract.CONTENT_AUTHORITY, FavouriteEntries.TABLE_NAME + "/#", FAVOURITE_WITH_ID);

        return uriMatcher;

    }

}
