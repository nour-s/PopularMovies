package com.nour_s.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MainActivityFragment extends Fragment {

    ImageAdapter imageAdapter;
    GridView gridview;
    public static final int MAX_NO_PAGES = 100;
    private int loadedPages =0;

    public MainActivityFragment() { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList(imageAdapter.movi);
        outState.putParcelableArrayList("key", imageAdapter.movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_movies, container, false);


        imageAdapter = new ImageAdapter(getActivity());
        prepareGridView(view);

        if (savedInstanceState != null)
        {
            ArrayList<Movie> m = savedInstanceState.getParcelableArrayList("key");
            imageAdapter.addMovies(new HashSet<Movie>(m));
        }
        else
        {
            fillMovies();
        }

        return view;

    }

    private void prepareGridView(View view) {

        gridview = (GridView) view.findViewById(R.id.gridview_movies);
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v,
                                    int position,
                                    long id) {

                ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
                Movie movie = adapter.getItem(position);

                if (movie == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(Movie.MOVIE_EXTRA, movie.getBundle());
                getActivity().startActivity(intent);
            }
        });
    }

    private void fillMovies() {
        if(loadedPages>= MAX_NO_PAGES)
            return;

        new MoviesTask().execute(loadedPages + 1);
    }

    public class MoviesTask extends AsyncTask<Integer,Void,Collection<Movie>>
    {
        public final String LOG_TAG = MoviesTask.class.getSimpleName();

        @Override
        protected Collection<Movie> doInBackground(Integer... params) {

            int page = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            final String API_URL = "http://api.themoviedb.org/3/movie/";
            final String URL_PAGE_PARAM = "page";
            final String API_KEY_PARAM = "api_key";
            final String URL_SORTING = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString(
                            getString(R.string.set_sorting_key),
                            getString(R.string.set_sorting_default)
                    );

            try {
                Uri builtUri = Uri.parse(API_URL)
                        .buildUpon()
                        .appendPath(URL_SORTING)
                        .appendQueryParameter(URL_PAGE_PARAM, String.valueOf(page))
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();


                Log.d(LOG_TAG, "REQUESTED QUERY: " + builtUri.toString());
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                responseJsonStr = buffer.toString();

            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return parseMoviesFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        private Collection<Movie> parseMoviesFromJson(String jsonStr)
                throws JSONException {
            final String MOVIES_NODE = "results";

            JSONObject jObject  = new JSONObject(jsonStr);
            JSONArray movies = jObject.getJSONArray(MOVIES_NODE);

            ArrayList result = new ArrayList<>();
            for (int i = 0; i < movies.length(); i++) {
                result.add(Movie.getJson(movies.getJSONObject(i)));
            }

            return result;
        }

        @Override
        protected void onPostExecute(Collection<Movie> movies) {
            if(movies != null) {
                loadedPages++;
                imageAdapter.addMovies(movies);
            }
        }
    }


}
