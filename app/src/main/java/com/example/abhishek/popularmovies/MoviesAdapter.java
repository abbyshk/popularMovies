package com.example.abhishek.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.abhishek.popularmovies.data.FavouriteContract;

import java.util.List;
import java.util.Objects;

/**
 * Created by abhishek on 03/07/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movies> moviesList;
    private Cursor data;
    private final OnPosterClickListener clickListener;
    private boolean isFavouriteActivity;

    public MoviesAdapter(List<Movies> moviesList, Context clickListener) {
        this.moviesList = moviesList;
        this.clickListener = (OnPosterClickListener) clickListener;
        isFavouriteActivity = false;
    }

    public MoviesAdapter(Context clickListener, Cursor cursor) {
        this.data = cursor;
        this.clickListener = (OnPosterClickListener) clickListener;
        isFavouriteActivity = true;
    }

    public interface OnPosterClickListener {
        void onClickListener(int position);
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MoviesViewHolder inflatedView;
        View view;

        view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movies_list, parent, false);

        inflatedView = new MoviesViewHolder(view);

        return inflatedView;

    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Bitmap moviePoster = null;

        if (!isFavouriteActivity)
            moviePoster = moviesList.get(position).getMoviePoster();

        else {

            if (data.moveToFirst()) {

                data.moveToPosition(position);

                moviePoster = Utilities.processImage(
                        data.getBlob(
                                data.getColumnIndex(
                                        FavouriteContract.FavouriteEntries.COLUMN_POSTER)));
            }
        }

        holder.moviePoster.setImageBitmap(moviePoster);
    }

    @Override
    public int getItemCount() {
        if (data != null && isFavouriteActivity)
            return data.getCount();

        else if (!isFavouriteActivity)
            return moviesList.size();
        else
            return 0;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView moviePoster;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            moviePoster.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onClickListener(position);
        }
    }


    public void addAll(List<Movies> data) {
        if (moviesList == data)
            return;

        moviesList = data;
        this.notifyDataSetChanged();

    }

    public void clear() {
        moviesList.clear();
        this.notifyDataSetChanged();
    }

    public void swapCursor(Cursor cursor) {
        if (data == cursor)
            return;
        data = cursor;
        notifyDataSetChanged();
    }

}
