package com.example.daviddell.imdbkiller;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by DavidDell on 6/23/2016.
 */
public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_movie_imageview);
        String posterPath = cursor.getString(MainFragment.COLUMN_POSTER_PATH);
        String baseUrl = "http://image.tmdb.org/t/p/w185/";
        String completeUrl = baseUrl + posterPath;

        Picasso.with(context).load(completeUrl).into(iconView);
    }

}
