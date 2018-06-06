package com.example.abhishek.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.abhishek.popularmovies.data.FavouriteContract;
import com.example.abhishek.popularmovies.data.FavouriteContract.FavouriteEntries;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailAdapter.OnTrailerClickListener {

    private static final int LOADER_TRAILERS_ID = 0;
    private static final int LOADER_REVIEWS_ID = 2;
    private static final int LOADER_FAVOURITE_DETAIL = 4;

    private String movieName;
    private String movieReleasedDate;
    private String movieDescription;
    private double movieVotes;
    private byte[] moviePoster;
    private byte[] backdropImage;
    private int movieId;
    private List<Trailers> trailersList = new ArrayList<>();
    private MovieDetailAdapter adapter;
    private FloatingActionButton favButton;
    private SharedPreferences sharedPreferences;
    boolean isFavourite;
    private Uri dataUri;
    private Cursor cursor;
    Context context = this;

    private SharedPreferences.OnSharedPreferenceChangeListener listener =

            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    isFavourite =
                            sharedPreferences.getBoolean(key,
                                    getResources().getBoolean(R.bool.favourite_default_value));
                    changeButtonState();
                }
            };

    private LoaderManager.LoaderCallbacks<List<Trailers>> loadTrailers =

            new LoaderManager.LoaderCallbacks<List<Trailers>>() {
                @Override
                public Loader<List<Trailers>> onCreateLoader(int id, Bundle args) {

                    return new AsyncTaskLoader<List<Trailers>>(getApplicationContext()) {

                        @Override
                        protected void onStartLoading() {
                            super.onStartLoading();
                            forceLoad();
                        }

                        @Override
                        public List<Trailers> loadInBackground() {

                            Uri.Builder builder = new Uri.Builder();
                            builder.scheme("https")
                                    .authority("api.themoviedb.org")
                                    .appendPath("3")
                                    .appendPath("movie")
                                    .appendPath(String.valueOf(movieId))
                                    .appendPath("videos")
                                    .appendQueryParameter(getString(R.string.api_param), getString(R.string.api_key));

                            return Utilities.getTrailers(builder.toString());

                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<List<Trailers>> loader, List<Trailers> data) {

                    if (data != null) {
                        trailersList = data;
                        adapter.setTrailerData(data, context);
                    }

                }

                @Override
                public void onLoaderReset(Loader<List<Trailers>> loader) {
                    adapter.clearData();
                }
            };

    private LoaderManager.LoaderCallbacks<List<Reviews>> loadReviews =

            new LoaderManager.LoaderCallbacks<List<Reviews>>() {

                @Override
                public Loader<List<Reviews>> onCreateLoader(int id, Bundle args) {

                    return new AsyncTaskLoader<List<Reviews>>(getApplicationContext()) {

                        @Override
                        protected void onStartLoading() {
                            super.onStartLoading();
                            forceLoad();
                        }

                        @Override
                        public List<Reviews> loadInBackground() {

                            Uri.Builder builder = new Uri.Builder();

                            builder.scheme("https")
                                    .authority("api.themoviedb.org")
                                    .appendPath("3")
                                    .appendPath("movie")
                                    .appendPath(String.valueOf(movieId))
                                    .appendPath("reviews")
                                    .appendQueryParameter(context.getString(R.string.api_param), context.getString(R.string.api_key));

                            return Utilities.getReviews(builder.toString());

                        }

                    };
                }

                @Override
                public void onLoadFinished(Loader<List<Reviews>> loader, List<Reviews> data) {
                    if (data != null)
                        adapter.addReviews(data);
                }

                @Override
                public void onLoaderReset(Loader<List<Reviews>> loader) {
                    adapter.clearReviews();
                }
            };

    private LoaderManager.LoaderCallbacks<Cursor> loadFavourites =

            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getApplicationContext(),
                            dataUri,
                            null,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                    if (data != null && data.moveToFirst()) {
                        cursor = data;
                        showData();
                        adapter.addCursorData(data);
                    }

                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    adapter.addCursorData(null);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        LoaderManager loaderManager = getSupportLoaderManager();

        if (Utilities.getNetworkState(getApplicationContext())) {
            loaderManager.initLoader(LOADER_TRAILERS_ID, null, loadTrailers);
            loaderManager.initLoader(LOADER_REVIEWS_ID, null, loadReviews);
        }

        Bundle bundle = getIntent().getExtras();
        dataUri = getIntent().getData();


        if ((bundle != null && dataUri == null) || (savedInstanceState != null)) {

            if (savedInstanceState != null)
                bundle = savedInstanceState;

            this.movieName = bundle.getString("title");
            this.movieReleasedDate = bundle.getString("released_date");
            this.movieDescription = bundle.getString("description");
            this.movieVotes = bundle.getDouble("vote_avg");
            this.moviePoster = bundle.getByteArray("poster");
            this.movieId = bundle.getInt("movie_id");
            this.backdropImage = bundle.getByteArray("backdrop_image");
            adapter = new MovieDetailAdapter(getApplicationContext(), bundle);
            showData();

        } else {
            adapter = new MovieDetailAdapter(context);
            this.movieId = (int) ContentUris.parseId(dataUri);

            isFavourite = sharedPreferences.getBoolean(String.valueOf(movieId),
                    getResources().getBoolean(R.bool.favourite_default_value));

            loaderManager.initLoader(LOADER_FAVOURITE_DETAIL, null, loadFavourites);
        }

        isFavourite = sharedPreferences.getBoolean(String.valueOf(movieId),
                getResources().getBoolean(R.bool.favourite_default_value));

        favButton = (FloatingActionButton) findViewById(R.id.button_fav);
        changeButtonState();
        favouriteMovies();
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_detail_activity);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void changeButtonState() {
        if (isFavourite)
            favButton.setImageResource(R.drawable.ic_favorite_white_24px);
        else
            favButton.setImageResource(R.drawable.ic_favorite_border_white_24px);
    }

    private void favouriteMovies() {
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFavourite)
                    insertToFavourites();
                else
                    deleteFromFavourites();
            }
        });
    }

    private void insertToFavourites() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(String.valueOf(movieId),
                getResources().getBoolean(R.bool.favourite_movie));
        editor.apply();

        ContentValues contentValues = new ContentValues();

        contentValues.put(FavouriteEntries.COLUMN_NAME, movieName);
        contentValues.put(FavouriteEntries.COLUMN_DESCRIPTION, movieDescription);
        contentValues.put(FavouriteEntries.COLUMN_MOVIE_ID, movieId);
        contentValues.put(FavouriteEntries.COLUMN_VOTES, movieVotes);
        contentValues.put(FavouriteEntries.COLUMN_POSTER, moviePoster);
        contentValues.put(FavouriteEntries.COLUMN_BACKDROP_IMAGE, backdropImage);
        contentValues.put(FavouriteEntries.COLUMN_MOVIE_RELEASED_DATE, movieReleasedDate);

        Uri uri = getContentResolver().insert(FavouriteContract.BASE_URI, contentValues);

        if (ContentUris.parseId(uri) > 0)
            Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
    }

    private void deleteFromFavourites() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(String.valueOf(movieId));
        editor.apply();

        Uri uri = Uri.withAppendedPath(FavouriteContract.BASE_URI, String.valueOf(movieId));

        int noOfRowsDeleted = getContentResolver().delete(uri, null, null);

        if (noOfRowsDeleted > 0)
            Toast.makeText(getApplicationContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
    }

    private void showData() {
        ImageView moviePoster;
        moviePoster = (ImageView) findViewById(R.id.iv_poster);

        if (cursor != null && !cursor.moveToFirst()) {
            finish();
        }

        if (cursor != null) {
            this.movieName = cursor.getString(cursor.getColumnIndex(FavouriteEntries.COLUMN_NAME));
            this.movieDescription = cursor.getString(cursor.getColumnIndex(FavouriteEntries.COLUMN_DESCRIPTION));
            this.movieReleasedDate = cursor.getString(cursor.getColumnIndex(FavouriteEntries.COLUMN_MOVIE_RELEASED_DATE));
            this.movieVotes = cursor.getDouble(cursor.getColumnIndex(FavouriteEntries.COLUMN_VOTES));
            this.moviePoster = cursor.getBlob(cursor.getColumnIndex(FavouriteEntries.COLUMN_POSTER));
            this.backdropImage = cursor.getBlob(cursor.getColumnIndex(FavouriteEntries.COLUMN_BACKDROP_IMAGE));
        }

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbarLayout.setTitle(this.movieName);
        moviePoster.setImageBitmap(Utilities.processImage(this.backdropImage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onTrailerClick(int position) {
        Trailers data = trailersList.get(position);

        String key = data.getMovieKey();

        Uri.Builder builder = new Uri.Builder();

        Uri trailerUrl = builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", key).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUrl);

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemSelected = item.getItemId();

        switch (itemSelected) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("title", this.movieName);
        outState.putString("description", this.movieDescription);
        outState.putString("released_date", this.movieReleasedDate);
        outState.putDouble("vote_avg", this.movieVotes);
        outState.putInt("movie_id", this.movieId);
        outState.putByteArray("poster", this.moviePoster);
        outState.putByteArray("backdrop_image", this.backdropImage);
    }
}
