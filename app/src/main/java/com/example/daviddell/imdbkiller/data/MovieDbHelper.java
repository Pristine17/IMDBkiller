package com.example.daviddell.imdbkiller.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DavidDell on 7/5/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntryPopular.TABLE_NAME + " (" +
                MovieContract.MovieEntryPopular._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntryPopular.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_TRAILER_VIDEO + " TEXT, " +
                MovieContract.MovieEntryPopular.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntryPopular.COLUMN_USER_REVIEW + " TEXT, " +
                " UNIQUE (" + MovieContract.MovieEntryPopular.COLUMN_MOVIE_NAME + " ) ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_TABLE2 = "CREATE TABLE " + MovieContract.MovieEntryHighestRated.TABLE_NAME + " (" +
                MovieContract.MovieEntryHighestRated._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntryHighestRated.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_TRAILER_VIDEO + " TEXT, " +
                MovieContract.MovieEntryHighestRated.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntryHighestRated.COLUMN_USER_REVIEW + " TEXT, " +
                " UNIQUE (" + MovieContract.MovieEntryHighestRated.COLUMN_MOVIE_NAME + " ) ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntryPopular.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntryHighestRated.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
