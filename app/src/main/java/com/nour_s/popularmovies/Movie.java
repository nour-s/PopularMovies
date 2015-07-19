package com.nour_s.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nour on 12/07/2015.
 */
public class Movie implements Parcelable {

    public final long id;
    public final String title;
    public final String overview;
    public final String poster_path;
    public final double vote_average;
    public final long vote_count;
    public final String release_date;

    public static final String JSON_ID = "id";
    public static final String JSON_TITLE = "title";
    public static final String JSON_OVERVIEW = "overview";
    public static final String JSON_POSTER_PATH = "poster_path";
    public static final String JSON_VOTE_AVERAGE = "vote_average";
    public static final String JSON_VOTE_COUNT = "vote_count";
    public static final String JSON_RELEASE_DATE = "release_date";

    public static final String MOVIE_EXTRA = "nour.popularmovies.MOVIE_EXTRA";

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        vote_count=in.readLong();
        release_date  =  in.readString();
    }

    public Movie(long id,
                 String title, String overview, String poster_path,
                 double vote_average, long vote_count, String release_date) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
    }

    public Movie(Bundle bundle) {
        this(
                bundle.getLong(JSON_ID),
                bundle.getString(JSON_TITLE),
                bundle.getString(JSON_OVERVIEW),
                bundle.getString(JSON_POSTER_PATH),
                bundle.getDouble(JSON_VOTE_AVERAGE),
                bundle.getLong(JSON_VOTE_COUNT),
                bundle.getString(JSON_RELEASE_DATE)
        );
    }


    public static Movie getJson(JSONObject jsonObject) throws JSONException {
        return new Movie(
                jsonObject.getLong(JSON_ID),
                jsonObject.getString(JSON_TITLE),
                jsonObject.getString(JSON_OVERVIEW),
                jsonObject.getString(JSON_POSTER_PATH),
                jsonObject.getDouble(JSON_VOTE_AVERAGE),
                jsonObject.getLong(JSON_VOTE_COUNT),
                jsonObject.getString(JSON_RELEASE_DATE)
        );
    }


    public Uri getImageUrl(String size) {
        final String URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(poster_path)
                .build();

        return builtUri;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong(JSON_ID, id);
        bundle.putString(JSON_TITLE, title);
        bundle.putString(JSON_OVERVIEW, overview);
        bundle.putString(JSON_POSTER_PATH, poster_path);
        bundle.putDouble(JSON_VOTE_AVERAGE, vote_average);
        bundle.putLong(JSON_VOTE_COUNT, vote_count);
        bundle.putString(JSON_RELEASE_DATE, release_date);

        return bundle;
    }

    public String getRating() {
        return  "" + vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
