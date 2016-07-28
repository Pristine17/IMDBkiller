package com.example.daviddell.imdbkiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.daviddell.imdbkiller.data.MovieContract;

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
import java.util.Vector;

/**
 * Created by DavidDell on 7/6/2016.
 */
class FetchMovieStuff extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieStuff.class.getSimpleName();
    private final Context mContext;
    ArrayList<String> posterPaths = new ArrayList<>();


    public FetchMovieStuff(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        final String DEFAULT = "popular";
        final String MOVIE_BASE_URL;
        BufferedReader reader = null;
        String output[] = new String[7];

        MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "?api_key="+"Insert API key here";

        try {


            Uri builtUri = Uri.parse(MOVIE_BASE_URL);
            String tempURL = builtUri.toString();
            URL url = new URL(tempURL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.

                return null;
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
                return null;
            }

            String JSONstr = buffer.toString();

            GetMovieDataFromJson(JSONstr, params[0]);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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

        return null;

    }

    void GetMovieDataFromJson(String JSONStr, String param) throws JSONException {

        final String OWM_RESULTS = "results";

        final String MOVIE_NAME = "original_title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVG = "vote_average";
        final String PLOT_SYNOPSIS = "overview";
        final String MOVIE_ID = "id";
        final String OWM_POSTERPATHS = "poster_path";



        JSONObject movieJson = new JSONObject(JSONStr);
        JSONArray resultArray = movieJson.getJSONArray(OWM_RESULTS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(resultArray.length());

        for (int i = 0; i < 20; i++) {

            String movie_name;
            String release_date;
            Double vote_avg;
            String plot_synopsis;
            String poster_path;
            int movie_id;


            JSONObject movieobj = resultArray.getJSONObject(i);

            movie_name = movieobj.getString(MOVIE_NAME);
            release_date = movieobj.getString(RELEASE_DATE);
            vote_avg = movieobj.getDouble(VOTE_AVG);
            plot_synopsis = movieobj.getString(PLOT_SYNOPSIS);
            poster_path = movieobj.getString(OWM_POSTERPATHS);
            movie_id = movieobj.getInt(MOVIE_ID);

            Log.e("IDS", movie_id + "");

            ContentValues movieValues = new ContentValues();

            if (param == "popular") {

                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_MOVIE_NAME, movie_name);
                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_VOTE_AVG, vote_avg);
                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_PLOT_SYNOPSIS, plot_synopsis);
                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_POSTER_PATH, poster_path);
                movieValues.put(MovieContract.MovieEntryPopular.COLUMN_MOVIE_ID, movie_id);
                cVVector.add(movieValues);

            } else if (param == "top_rated") {
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_MOVIE_NAME, movie_name);
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_VOTE_AVG, vote_avg);
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_PLOT_SYNOPSIS, plot_synopsis);
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_POSTER_PATH, poster_path);
                movieValues.put(MovieContract.MovieEntryHighestRated.COLUMN_MOVIE_ID, movie_id);

                cVVector.add(movieValues);
            }
        }

        if (param == "top_rated") {
            int deleted = 0;

            if (cVVector.size() > 0) {

                deleted = mContext.getContentResolver().delete(MovieContract.MovieEntryHighestRated.CONTENT_URI, null, null);
            }
            Log.e(param, "Deleted " + deleted);

            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntryHighestRated.CONTENT_URI, cvArray);
            }

            Cursor cur = mContext.getContentResolver().query(MovieContract.MovieEntryHighestRated.CONTENT_URI,
                    null, null, null, null);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {


                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }
            Log.d(param, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");
        } else if (param == "popular") {

            int deleted = 0;

            if (cVVector.size() > 0) {

                deleted = mContext.getContentResolver().delete(MovieContract.MovieEntryPopular.CONTENT_URI, null, null);
            }
            Log.e(param, "Deleted " + deleted);

            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntryPopular.CONTENT_URI, cvArray);
            }

            Cursor cur = mContext.getContentResolver().query(MovieContract.MovieEntryPopular.CONTENT_URI,
                    null, null, null, null);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {


                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }
            Log.e(param, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");


        }

    }


    void GetPosterPaths(String JSONstr) {
        try {

            final String OWM_RESULTS = "results";
            final String OWM_POSTERPATHS = "poster_path";


            JSONObject movieJson = new JSONObject(JSONstr);
            JSONArray resultArray = movieJson.getJSONArray(OWM_RESULTS);

            for (int i = 0; i < 20; i++) {
                JSONObject movieobj = resultArray.getJSONObject(i);
                posterPaths.add(movieobj.getString(OWM_POSTERPATHS));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

