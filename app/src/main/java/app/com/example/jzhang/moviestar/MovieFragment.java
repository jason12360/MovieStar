package app.com.example.jzhang.moviestar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    ArrayList<MovieData> myList = new ArrayList<MovieData>();
    GridView lvDetail;
    Context context ;
    private MyAdapter mMovieAdapter;


    public MovieFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        lvDetail = (GridView) rootView.findViewById(R.id.gridView);

        mMovieAdapter = new MyAdapter(context, myList, inflater);
        lvDetail.setAdapter(mMovieAdapter);
        lvDetail.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                MovieData movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra("myMovie",movie);
                startActivity(intent);
            }
        });

        return rootView;
    }



    private void updateWeather(){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_popular));
        movieTask.execute(order);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    public class FetchMovieTask extends AsyncTask<String, Void,MovieData[]> {
        private final String LOG_TAG = "movie background";

        private MovieData[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_MOVIETITLE = "original_title";
            final String OWM_MOVIEIMG = "poster_path";
            final String OWM_MOVIEOVERVIEW = "overview";
            final String OWM_MOVIERATE = "vote_average";
            final String OWM_MOVIEDATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);
            Log.v(LOG_TAG, movieArray.toString());

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

           MovieData[] resultStrs = new MovieData[10];

            for(int i = 0; i < 10; i++) {
                // For now, using the format "Day, description, hi/low"
               MovieData movieData = new MovieData();
                String preImg = "https://image.tmdb.org/t/p/w500/";
                // Get the JSON object representing the day
                JSONObject movieObject = movieArray.getJSONObject(i);

                String movieTitle = movieObject.getString(OWM_MOVIETITLE);
                movieData.setMovieName(movieTitle);

                String movieImg = movieObject.getString(OWM_MOVIEIMG);
                movieData.setMovieImg(preImg + movieImg);

                String movieOverview = movieObject.getString(OWM_MOVIEOVERVIEW);
                movieData.setMovieOverview(movieOverview);

                String movieRate = movieObject.getString(OWM_MOVIERATE);
                movieData.setMovieRating(movieRate);

                String movieDate = movieObject.getString(OWM_MOVIEDATE);
                movieData.setMovieDate(movieDate);

                resultStrs[i] = movieData;
            }


            return resultStrs;

        }
        @Override
        protected MovieData[] doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String appid = "0fb7c9421bd2a58fcfe50a2b14dc8996";
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM,appid).build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "content " + movieJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(MovieData[] result) {
            if (result != null) {
                myList.clear();
                for(MovieData movie : result) {
                    myList.add(movie);
                }
                mMovieAdapter.notifyDataSetChanged();     // New data is back from the server.  Hooray!
            }
        }
    }



}
