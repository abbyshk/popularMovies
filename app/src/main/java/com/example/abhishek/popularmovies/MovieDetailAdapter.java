package com.example.abhishek.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhishek.popularmovies.data.FavouriteContract;

import java.util.List;

/**
 * Created by abhishek on 12/09/17.
 */

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.MovieDetailViewHolder> {

    private static final int DETAIL_VIEW = 1000;
    private static final int TRAILER_VIEW = 1010;
    private static final int DEFAULT_VIEW = 1020;

    private String movieDescription;
    private String movieRating;
    private String movieReleasedDate;
    private Context context;
    private TrailerAdapter adapter;
    private List<Reviews> reviews;

    public MovieDetailAdapter(Context context, Bundle bundle) {
        this.context = context;

        if (bundle != null) {
            movieDescription = bundle.getString("description");
            movieRating = String.valueOf(bundle.getDouble("vote_avg"));
            movieReleasedDate = bundle.getString("released_date");
        }

    }

    public MovieDetailAdapter(Context context) {
        this.context = context;
    }

    public interface OnTrailerClickListener {
        void onTrailerClick(int position);
    }


    @Override
    public MovieDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case DETAIL_VIEW:
                view = LayoutInflater
                        .from(context)
                        .inflate(R.layout.content_layout, parent, false);
                break;

            case TRAILER_VIEW:
                view = LayoutInflater
                        .from(context)
                        .inflate(R.layout.trailer_layout, parent, false);
                break;

            default:
                view = LayoutInflater
                        .from(context)
                        .inflate(R.layout.layout_reviews, parent, false);
                break;

        }

        MovieDetailViewHolder viewHolder = new MovieDetailViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return DETAIL_VIEW;
            case 1:
                return TRAILER_VIEW;
            default:
                return DEFAULT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final MovieDetailViewHolder holder, int position) {
        switch (position) {

            case 0:
                holder.movieDescription.setText(movieDescription);
                holder.movieAverageVotes.setText(movieRating);
                holder.movieReleaseDate.setText(movieReleasedDate);
                break;

            case 1:
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.recyclerView.setLayoutManager(layoutManager);
                holder.recyclerView.setAdapter(adapter);
                break;

            default:

                if (reviews == null && position == 2) {
                    holder.emptyReviewView.setText("No Reviews Found");
                } else {
                    Reviews data = reviews.get(position - 2);
                    holder.emptyReviewView.setVisibility(View.GONE);
                    holder.reviewAuthor.setText(data.getReviewAuthor());
                    holder.reviewContent.setText(data.getReviewContent());
                }
                break;

        }
    }

    @Override
    public int getItemCount() {
        return (reviews == null) ? 3 : reviews.size() + 2;
    }

    class MovieDetailViewHolder extends RecyclerView.ViewHolder {

        TextView movieDescription;
        TextView movieReleaseDate;
        TextView movieAverageVotes;
        RecyclerView recyclerView;
        TextView reviewAuthor;
        TextView reviewContent;
        TextView emptyReviewView;


        public MovieDetailViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {

                case DETAIL_VIEW:
                    movieReleaseDate = (TextView) itemView.findViewById(R.id.tv_released_date);
                    movieDescription = (TextView) itemView.findViewById(R.id.tv_description);
                    movieAverageVotes = (TextView) itemView.findViewById(R.id.tv_vote_average);
                    break;

                case TRAILER_VIEW:
                    recyclerView = (RecyclerView) itemView.findViewById(R.id.rv_trailers);
                    break;

                default:
                    reviewAuthor = (TextView) itemView.findViewById(R.id.tv_author);
                    reviewContent = (TextView) itemView.findViewById(R.id.tv_review);
                    emptyReviewView = (TextView) itemView.findViewById(R.id.reviews_empty);
                    break;

            }

        }
    }

    private class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

        private List<Trailers> data;
        private OnTrailerClickListener clickListener;

        public TrailerAdapter(List<Trailers> trailers, Context listener) {
            data = trailers;
            clickListener = (OnTrailerClickListener) listener;
        }


        @Override
        public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.thumbnail, parent, false);

            return new TrailerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrailerViewHolder holder, int position) {
            Trailers data = this.data.get(position);
            holder.imageView.setImageBitmap(data.getTrailerThumbnail());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;

            public TrailerViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.iv_trailer);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                clickListener.onTrailerClick(position);
            }
        }

        private void setData(List<Trailers> trailers) {
            if (data == trailers)
                return;
            data = trailers;

            notifyDataSetChanged();
        }

        private void clearData() {
            data = null;
            notifyDataSetChanged();
        }

    }

    public void clearData() {
        adapter.clearData();
    }

    public void setTrailerData(List<Trailers> trailers, Context context) {
        adapter = new TrailerAdapter(trailers, context);
        adapter.setData(trailers);
        notifyDataSetChanged();
    }

    public void addReviews(List<Reviews> data) {

        if (reviews == data || data.size() == 0)
            return;

        reviews = data;

        notifyDataSetChanged();

    }

    public void clearReviews() {
        reviews = null;
        notifyDataSetChanged();
    }

    public void addCursorData(Cursor data) {

        if (data == null) {
            movieDescription = null;
            movieRating = null;
            movieReleasedDate = null;
        } else {
            movieDescription = data.getString(
                    data.getColumnIndex(
                            FavouriteContract.FavouriteEntries.COLUMN_DESCRIPTION));

            movieReleasedDate = data.getString(
                    data.getColumnIndex(
                            FavouriteContract.FavouriteEntries.COLUMN_MOVIE_RELEASED_DATE));

            movieRating = data.getString(
                    data.getColumnIndex(
                            FavouriteContract.FavouriteEntries.COLUMN_VOTES));
        }

        notifyDataSetChanged();
    }

}
