package com.example.daviddell.imdbkiller;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.daviddell.imdbkiller.data.MovieContract;


/**
 * Created by DavidDell on 6/23/2016.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    Movie movies[] = new Movie[20];
    MovieAdapter movieAdapter;
    GridView gridView;
    Spinner spinner;
    private int mPosition = ListView.INVALID_POSITION;

    Callback mCallback;


    String param;
    private boolean isFirstTime = true;

    private static final int MOVIE_LOADER = 0;


    private final String FRAG_TAG = "main_fragment";

    public MainFragment() {
    }

    private static final String[] MOVIE_POPULAR_COLUMNS = {

            MovieContract.MovieEntryPopular._ID,
            MovieContract.MovieEntryPopular.COLUMN_POSTER_PATH

    };

    private static final String[] MOVIE_HIGHEST_RATED_COLUMNS = {

            MovieContract.MovieEntryPopular._ID,
            MovieContract.MovieEntryPopular.COLUMN_POSTER_PATH

    };

    static final int COL_MOVIE_ID = 0;
    static final int COLUMN_POSTER_PATH = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        gridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        spinner = (Spinner) rootView.findViewById(R.id.movie_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.movies_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    if (param == "popular") {
                        mCallback.onItemSelected(MovieContract.MovieEntryPopular.buildMovieUri(cursor.getLong(COL_MOVIE_ID)), param);
                    } else if (param == "top_rated") {
                        mCallback.onItemSelected(MovieContract.MovieEntryHighestRated.buildMovieUri(cursor.getLong(COL_MOVIE_ID)), param);
                    }

                }
                mPosition = position;
            }
        });


        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        gridView.setAdapter(movieAdapter);


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Callback");
        }
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri, String param);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (param == "top_rated") {
            String sortOrder = MovieContract.MovieEntryHighestRated.SORT_ORDER_DEFAULT;
            Uri movieUri = MovieContract.MovieEntryHighestRated.CONTENT_URI;

            return new CursorLoader(getActivity(), movieUri, MOVIE_HIGHEST_RATED_COLUMNS, null, null, sortOrder);
        } else {
            String sortOrder = MovieContract.MovieEntryPopular.SORT_ORDER_DEFAULT;
            Uri movieUri = MovieContract.MovieEntryPopular.CONTENT_URI;

            return new CursorLoader(getActivity(), movieUri, MOVIE_POPULAR_COLUMNS, null, null, sortOrder);
        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {


        String selection = parent.getItemAtPosition(pos).toString();
        if (selection.equals("Popularity")) {
            param = "popular";
        } else {
            param = "top_rated";
        }

        if (!isFirstTime) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        } else {
            isFirstTime = false;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int tempID = item.getItemId();
        if (tempID == R.id.action_refresh) {
            update(param);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        String temp = spinner.getSelectedItem().toString();

        if (temp.equals("Popularity")) {
            param = "popular";
        } else {
            param = "top_rated";
        }

        super.onStart();
    }

    void update(String param) {
        new FetchMovieStuff(getActivity()).execute(param);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRetainInstance(true);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main_fragment_menu, menu);
    }


}
