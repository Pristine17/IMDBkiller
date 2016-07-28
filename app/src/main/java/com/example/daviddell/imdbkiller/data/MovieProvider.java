package com.example.daviddell.imdbkiller.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by DavidDell on 7/5/2016.
 */
public class MovieProvider extends ContentProvider {

    private static final int MOVIE_POPULAR_LIST = 1;
    private static final int MOVIE_POPULAR_ID = 2;
    private static final int MOVIE_HIGHEST_RATED_LIST = 3;
    private static final int MOVIE_HIGHEST_RATED_ID = 4;
    private static final UriMatcher URI_MATCHER;

    private MovieDbHelper mOpenHelper;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "moviesPopular",
                MOVIE_POPULAR_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "moviesPopular/#",
                MOVIE_POPULAR_ID);

        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "moviesHighestRated",
                MOVIE_HIGHEST_RATED_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "moviesHighestRated/#",
                MOVIE_HIGHEST_RATED_ID);

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_POPULAR_LIST:
                builder.setTables(MovieContract.MovieEntryPopular.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MovieContract.MovieEntryPopular.SORT_ORDER_DEFAULT;
                }
                break;
            case MOVIE_POPULAR_ID:
                builder.setTables(MovieContract.MovieEntryPopular.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(MovieContract.MovieEntryPopular._ID + " = " +
                        uri.getLastPathSegment());
                break;

            case MOVIE_HIGHEST_RATED_LIST:
                builder.setTables(MovieContract.MovieEntryHighestRated.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MovieContract.MovieEntryHighestRated.SORT_ORDER_DEFAULT;
                }
                break;
            case MOVIE_HIGHEST_RATED_ID:
                builder.setTables(MovieContract.MovieEntryHighestRated.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(MovieContract.MovieEntryHighestRated._ID + " = " +
                        uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }

        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_POPULAR_LIST:
                return MovieContract.MovieEntryPopular.CONTENT_TYPE;
            case MOVIE_POPULAR_ID:
                return MovieContract.MovieEntryPopular.CONTENT_ITEM_TYPE;
            case MOVIE_HIGHEST_RATED_LIST:
                return MovieContract.MovieEntryHighestRated.CONTENT_TYPE;
            case MOVIE_HIGHEST_RATED_ID:
                return MovieContract.MovieEntryHighestRated.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = URI_MATCHER.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIE_POPULAR_LIST: {

                long _id = db.insert(MovieContract.MovieEntryPopular.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntryPopular.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case MOVIE_HIGHEST_RATED_LIST: {

                long _id = db.insert(MovieContract.MovieEntryHighestRated.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntryHighestRated.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int delCount = 0;

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_POPULAR_LIST:
                delCount = db.delete(
                        MovieContract.MovieEntryPopular.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_POPULAR_ID:
                String idStr = uri.getLastPathSegment();
                String where = MovieContract.MovieEntryPopular._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        MovieContract.MovieEntryPopular.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            case MOVIE_HIGHEST_RATED_LIST:
                delCount = db.delete(
                        MovieContract.MovieEntryHighestRated.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_HIGHEST_RATED_ID:
                String idStr2 = uri.getLastPathSegment();
                String where2 = MovieContract.MovieEntryHighestRated._ID + " = " + idStr2;
                if (!TextUtils.isEmpty(selection)) {
                    where2 += " AND " + selection;
                }
                delCount = db.delete(
                        MovieContract.MovieEntryHighestRated.TABLE_NAME,
                        where2,
                        selectionArgs);
                break;


            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updateCount = 0;

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_POPULAR_LIST:
                updateCount = db.update(
                        MovieContract.MovieEntryPopular.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_POPULAR_ID:
                String idStr = uri.getLastPathSegment();
                String where = MovieContract.MovieEntryPopular._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        MovieContract.MovieEntryPopular.TABLE_NAME,
                        contentValues,
                        where,
                        selectionArgs);
                break;

            case MOVIE_HIGHEST_RATED_LIST:
                updateCount = db.update(
                        MovieContract.MovieEntryHighestRated.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_HIGHEST_RATED_ID:
                String idStr2 = uri.getLastPathSegment();
                String where2 = MovieContract.MovieEntryHighestRated._ID + " = " + idStr2;
                if (!TextUtils.isEmpty(selection)) {
                    where2 += " AND " + selection;
                }
                updateCount = db.update(
                        MovieContract.MovieEntryHighestRated.TABLE_NAME,
                        contentValues,
                        where2,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;


    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MOVIE_POPULAR_LIST:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntryPopular.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case MOVIE_HIGHEST_RATED_LIST:
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntryHighestRated.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
