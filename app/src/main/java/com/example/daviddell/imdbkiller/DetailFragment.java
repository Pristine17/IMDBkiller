package com.example.daviddell.imdbkiller;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daviddell.imdbkiller.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DavidDell on 7/7/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    Uri mUri;
    String param;

    String completeYoutubeLink;


    private TextView mMovieNameView;
    private ImageView mIconView;
    private TextView mReleaseDateView;
    private TextView mDurationView;
    private TextView mVoteAvgView;
    private Button mFavoriteButton;
    private TextView mPlotSynopsis;

    public static final int COL_MOVIE_ID = 0;
    public static final int COLUMN_MOVIE_NAME = 1;
    public static final int COLUMN_RELEASE_DATE = 2;
    public static final int COLUMN_VOTE_AVG = 3;
    public static final int COLUMN_PLOT_SYNOPSIS = 4;
    public static final int COLUMN_TRAILER_VIDEO = 5;
    public static final int COLUMN_USER_REVIEW = 6;
    public static final int COLUMN_POSTER_PATH = 7;

    private static final int DETAIL_LOADER = 0;

    static int movie_id_for_trailer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        mMovieNameView = (TextView) rootView.findViewById(R.id.mMovieNameView);
        mIconView = (ImageView) rootView.findViewById(R.id.mIconView);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.mReleaseDateView);
        mDurationView = (TextView) rootView.findViewById(R.id.mDurationView);
        mVoteAvgView = (TextView) rootView.findViewById(R.id.mVoteAvgView);
        mFavoriteButton = (Button) rootView.findViewById(R.id.mFavoriteButton);
        mPlotSynopsis = (TextView) rootView.findViewById(R.id.mPlotSynopsis);


        Button b = (Button) rootView.findViewById(R.id.TrailerButton);
        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                StartYoutube();
            }
        });


        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = getArguments().getParcelable(DETAIL_URI);
        }
        Log.e("YOLO", mUri.toString());
        param = arguments.getString("PARAM");

        return rootView;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mUri,
                MovieContract.MovieEntryPopular.PROJECTION_ALL,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            int movie_id_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_MOVIE_ID);
            movie_id_for_trailer = data.getInt(movie_id_index);

            int movie_name_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_MOVIE_NAME);
            String movie_name = data.getString(movie_name_index);
            int release_date_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_RELEASE_DATE);
            String release_date = data.getString(release_date_index);
            release_date = release_date.substring(0, 4);
            int vote_avg_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_VOTE_AVG);
            Double vote_avg = data.getDouble(vote_avg_index);
            String vote_avg_real = vote_avg + "/10";
            int plot_synopsis_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_PLOT_SYNOPSIS);
            String plot_synopsis = data.getString(plot_synopsis_index);
            int poster_path_index = data.getColumnIndex(MovieContract.MovieEntryPopular.COLUMN_POSTER_PATH);
            String poster_path = data.getString(poster_path_index);

            String baseUrl = "http://image.tmdb.org/t/p/w185/";
            String completeUrl = baseUrl + poster_path;

            Picasso.with(getActivity()).load(completeUrl).into(mIconView);


            mMovieNameView.setText(movie_name);
            mReleaseDateView.setText(release_date);
            mVoteAvgView.setText(vote_avg_real);
            mPlotSynopsis.setText(plot_synopsis);
        }
        String arr[] = data.getColumnNames();


        String release_date = data.getString(COLUMN_RELEASE_DATE);


    }

    void StartYoutube() {
        new getTrailerID().execute();
    }

    public class getTrailerID extends AsyncTask<Void, Void, Void> {

        getTrailerID() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            final String MOVIE_BASE_URL;


            BufferedReader reader = null;
            String output[] = new String[7];

            MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id_for_trailer + "/videos" + "?api_key="+"Insert API key here";

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

                }
                String JSONstr = buffer.toString();

                JSONObject movieObj = new JSONObject(JSONstr);
                JSONArray movieArr = movieObj.getJSONArray("results");
                JSONObject firstObj = movieArr.getJSONObject(0);
                String id = firstObj.getString("key");
                String BaseYoutubeLink = "https://www.youtube.com/watch?v=";
                completeYoutubeLink = BaseYoutubeLink + id;
                ReallyStartYoutube(completeYoutubeLink);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        void ReallyStartYoutube(String link) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
