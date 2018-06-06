package com.example.abhishek.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnPosterClickListener,
        LoaderManager.LoaderCallbacks<List<Movies>> {

    private static final int LOADER_ID = 1;

    private MoviesAdapter adapter;
    private List<Movies> moviesList = new ArrayList<>();
    private LoaderManager loaderManager;
    private String requestUrl;
    private ProgressBar progressBar;
    private LinearLayout noNetwork;

    private Uri baseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        makeBaseUri();
        progressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        progressBar.setVisibility(View.GONE);

        noNetwork = (LinearLayout) findViewById(R.id.ll_no_network);
        noNetwork.setVisibility(View.GONE);
        setupRecyclerView();

        Uri.Builder builder = baseUri.buildUpon();
        builder.appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter(getString(R.string.api_param), getString(R.string.api_key));

        requestUrl = builder.toString();

        if (Utilities.getNetworkState(getApplicationContext()))
            loadData();
        else
            noNetwork.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClickListener(int position) {

        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

        Movies data = moviesList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", data.getTitle());
        bundle.putString("description", data.getDescription());
        bundle.putString("released_date", data.getReleaseDate());
        bundle.putDouble("vote_avg", data.getVoteAvg());
        bundle.putInt("movie_id", data.getMovieId());

        Bitmap poster = data.getMoviePoster();
        Bitmap backdropImage = data.getBackdropImage();
        bundle.putByteArray("poster", Utilities.compressImage(poster));
        bundle.putByteArray("backdrop_image", Utilities.compressImage(backdropImage));

        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void setupRecyclerView() {

        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        adapter = new MoviesAdapter(moviesList, this);

        int spanCount = Utilities.calculateNumberOfColumns(getApplicationContext());

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getApplicationContext(), spanCount);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

    }

    private void loadData() {
        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {
        noNetwork.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        return new AsyncTaskLoader<List<Movies>>(this) {

            List<Movies> cachedData;

            @Override
            protected void onStartLoading() {

                if (cachedData != null)
                    deliverResult(cachedData);
                else
                    forceLoad();

            }

            @Override
            public void deliverResult(List<Movies> data) {
                cachedData = data;
                super.deliverResult(data);
            }

            @Override
            public List<Movies> loadInBackground() {
                if (requestUrl == null)
                    return null;
                return Utilities.getMovies(requestUrl);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> data) {

        progressBar.setVisibility(View.GONE);

        boolean networkState = Utilities.getNetworkState(getApplicationContext());

        if (data != null && networkState) {
            moviesList = data;
            adapter.addAll(data);
        } else if (!networkState)
            noNetwork.setVisibility(View.VISIBLE);


    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        switch (itemSelected) {

            case R.id.men_popular:
                loadPopular();
                return true;

            case R.id.men_top_rated:
                loadTopRated();
                return true;

            case R.id.men_favourite:
                Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadPopular() {

        if (!Utilities.getNetworkState(getApplicationContext()))
            return;

        if (Utilities.getNetworkState(getApplicationContext()) && loaderManager == null)
            loadData();

        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("movie")
                .appendPath(getString(R.string.sort_popular))
                .appendQueryParameter(getString(R.string.api_param), getString(R.string.api_key));

        requestUrl = builder.toString();
        adapter.clear();

        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    private void loadTopRated() {

        if (!Utilities.getNetworkState(getApplicationContext()))
            return;

        if (Utilities.getNetworkState(getApplicationContext()) && loaderManager == null)
            loadData();

        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("movie")
                .appendPath(getString(R.string.sort_top))
                .appendQueryParameter(getString(R.string.api_param), getString(R.string.api_key));

        requestUrl = builder.toString();
        adapter.clear();

        loaderManager.restartLoader(LOADER_ID, null, this);

    }

    private void makeBaseUri() {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(getString(R.string.scheme))
                .authority(getString(R.string.content_authority))
                .appendPath("3");

        baseUri = Uri.parse(builder.toString());
    }

}
