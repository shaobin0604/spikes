package com.mobodev.themeonstop;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by bshao on 1/15/18.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ThemeOnStop";

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, activity.getClass().getSimpleName() + " onActivityDestroyed");
    }
}
