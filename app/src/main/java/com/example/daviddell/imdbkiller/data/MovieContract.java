package com.example.daviddell.imdbkiller.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DavidDell on 7/5/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.daviddell.imdbkiller.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE_POPULAR = "moviesPopular";

    public static final String PATH_MOVIE_HIGHEST_RATED = "moviesHighestRated";

    public static final class MovieEntryPopular implements BaseColumns {

        public static final String TABLE_NAME = "movie_popular";

        public static final String COLUMN_MOVIE_NAME = "movie_name";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVG = "vote_avg";


        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";


        public static final String COLUMN_TRAILER_VIDEO = "trailer_video";

        public static final String COLUMN_USER_REVIEW = "user_review";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_MOVIE_ID = "movie_ID";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_POPULAR;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_POPULAR;

        public static final String[] PROJECTION_ALL =
                {_ID, COLUMN_MOVIE_NAME, COLUMN_RELEASE_DATE, COLUMN_VOTE_AVG, COLUMN_PLOT_SYNOPSIS, COLUMN_TRAILER_VIDEO, COLUMN_USER_REVIEW, COLUMN_POSTER_PATH, COLUMN_MOVIE_ID};

        public static final String SORT_ORDER_DEFAULT =
                COLUMN_MOVIE_NAME + " ASC";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }


    }

    public static final class MovieEntryHighestRated implements BaseColumns {

        public static final String TABLE_NAME = "movie_highest_rated";

        public static final String COLUMN_MOVIE_NAME = "movie_name";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVG = "vote_avg";


        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";


        public static final String COLUMN_TRAILER_VIDEO = "trailer_video";

        public static final String COLUMN_USER_REVIEW = "user_review";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_MOVIE_ID = "movie_ID";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_HIGHEST_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_HIGHEST_RATED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_HIGHEST_RATED;

        public static final String[] PROJECTION_ALL =
                {_ID, COLUMN_MOVIE_NAME, COLUMN_RELEASE_DATE, COLUMN_VOTE_AVG, COLUMN_PLOT_SYNOPSIS, COLUMN_TRAILER_VIDEO, COLUMN_USER_REVIEW, COLUMN_POSTER_PATH, COLUMN_MOVIE_ID};

        public static final String SORT_ORDER_DEFAULT =
                COLUMN_MOVIE_NAME + " ASC";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }


    }

}
