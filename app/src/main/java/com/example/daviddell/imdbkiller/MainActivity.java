package com.example.daviddell.imdbkiller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private final String FRAG_TAG = "main_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemSelected(Uri movieUri, String param) {
        Log.e("FINAL", movieUri.toString());

        Intent intent = new Intent(this, DetailActivity.class)
                .setData(movieUri);
        intent.putExtra("PARAM", param);

        startActivity(intent);
    }
}
