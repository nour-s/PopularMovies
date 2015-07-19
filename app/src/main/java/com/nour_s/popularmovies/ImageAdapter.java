package com.nour_s.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nour on 14/07/2015.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Movie> movies;

    private final int height;
    private final int width;

    public ImageAdapter(Context c) {
        context = c;
        height = Math.round(context.getResources().getDimension(R.dimen.movieImage_height));
        width = Math.round(context.getResources().getDimension(R.dimen.movieImage_width));
        movies = new ArrayList<>();
    }

    public int getCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        if (position < 0 || position >= movies.size()) {
            return null;
        }
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (movie == null) {
            return null;
        }

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else {
            imageView = (ImageView) convertView;
        }
        Uri imageUrl = movie.getImageUrl(context.getString(R.string.api_image_default_size));
        Picasso.with(context).load(imageUrl).into(imageView);
        return imageView;
    }

    // references to our images

    public void addMovies(Collection<Movie> newList) {
        movies.addAll(newList);
        notifyDataSetChanged();
    }

}