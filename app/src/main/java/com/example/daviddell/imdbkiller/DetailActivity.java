package com.example.daviddell.imdbkiller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by DavidDell on 7/7/2016.
 */
public class DetailActivity extends AppCompatActivity {

    DetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        fragment = new DetailFragment();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();


        Bundle arguments = new Bundle();
        arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
        arguments.putString("PARAM", getIntent().getStringExtra("PARAM"));

        fragment.setArguments(arguments);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }


}
