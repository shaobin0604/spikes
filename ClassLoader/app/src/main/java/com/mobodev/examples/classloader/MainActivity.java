package com.mobodev.examples.classloader;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        printClassLoaders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void printClassLoaders() {
        int i = 1;
        ClassLoader classLoader = getClassLoader();
        if (classLoader != null) {
            Timber.i("[onCreate] classLoader %d : %s", i, classLoader);
            while (classLoader.getParent() != null) {
                classLoader = classLoader.getParent();
                i++;
                Timber.i("[onCreate] classLoader %d : %s", i, classLoader);
            }
        }

        Timber.i("getSystemClassLoader: %s", ClassLoader.getSystemClassLoader());
        Timber.i("getSystemClassLoader.parent: %s", ClassLoader.getSystemClassLoader().getParent());
        Timber.i("getSystemClassLoader.parent.parent: %s", ClassLoader.getSystemClassLoader().getParent().getParent());

        Timber.i("java.lang.String ClassLoader: %s", String.class.getClassLoader());
        Timber.i("android.app.Activity ClassLoader: %s", Activity.class.getClassLoader());
        Timber.i("androidx.appcompat.app.AppCompatActivity ClassLoader: %s", AppCompatActivity.class.getClassLoader());
    }
}
