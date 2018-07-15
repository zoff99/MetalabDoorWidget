package com.erenutku.updatingwidgetexample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class UpdatingWidget extends AppWidgetProvider
{
    private static final String TAG = "AWP";
    private PendingIntent service;
    private static final int UPDATE_INERVAL_IN_MINUTES = 15;
    private static Random random = new Random();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d(TAG, "onUpdate");

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, UpdateService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        int rand_secs = randomBetween(3, 24);

        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                             (UPDATE_INERVAL_IN_MINUTES * 60000) + (rand_secs * 1000), service);


        //if you need to call your service less than 60 sec
        //answer is here:
        //http://stackoverflow.com/questions/29998313/how-to-run-background-service-after-every-5-sec-not-working-in-android-5-1


        final int N = appWidgetIds.length;
        int dummy_number = 1;

        // loop for each app widget
        for (int j = 0; j < N; j++)

        {
            int appWidgetId = appWidgetIds[j];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.updating_widget);
        }

    }

    /**
     * Min is inclusive and max is exclusive in this case
     **/
    public static int randomBetween(int min, int max)
    {
        return random.nextInt(max - min) + min;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive");
        Log.d(TAG, "action=" + intent.getAction());
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
    {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d(TAG, "onAppWidgetOptionsChanged");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);
        Log.d("UpdatingWidget: ", "onDeleted");

    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
        Log.d("UpdatingWidget: ", "onEnabled");

    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
        Log.d("UpdatingWidget: ", "onDisabled");

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds)
    {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.d("UpdatingWidget: ", "onRestored");

    }
}




