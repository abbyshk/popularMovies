package com.example.abhishek.popularmovies;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.abhishek.popularmovies.data.FavouriteContract;

public class FavouriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnPosterClickListener {

    private static final int LOADER_LOAD_FAVOURITES = 3;

    private Cursor moviePoster;
    private MoviesAdapter adapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        LinearLayout noNetwork = (LinearLayout) findViewById(R.id.ll_no_network);
        noNetwork.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        progressBar.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.label_favourite));

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_LOAD_FAVOURITES, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                FavouriteContract.BASE_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);
        if (data != null) {
            moviePoster = data;
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void setupRecyclerView() {

        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        adapter = new MoviesAdapter(this, moviePoster);

        int spanCount = Utilities.calculateNumberOfColumns(getApplicationContext());

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getApplicationContext(), spanCount);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClickListener(int position) {

        if (moviePoster.moveToPosition(position)) {

            Uri uri = ContentUris.withAppendedId(
                    FavouriteContract.BASE_URI, moviePoster.getInt(
                            moviePoster.getColumnIndex(
                                    FavouriteContract.FavouriteEntries.COLUMN_MOVIE_ID)));

            Intent intent = new Intent(FavouriteActivity.this, MovieDetailActivity.class);
            intent.setData(uri);
            startActivity(intent);

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
