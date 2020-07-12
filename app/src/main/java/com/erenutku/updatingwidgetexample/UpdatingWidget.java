package com.erenutku.updatingwidgetexample;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import static com.erenutku.updatingwidgetexample.UpdateService.update_status_on_widget;

/**
 * Implementation of App Widget functionality.
 */
public class UpdatingWidget extends AppWidgetProvider
{
    private static final String TAG = "AWP";
    private static Random random = new Random();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d(TAG, "onUpdate");


        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++)
        {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.updating_widget);
            views.setOnClickPendingIntent(R.id.button_box, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);


            // Update the widget on first start -------------
            final Context c = context;
            final Thread th = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        update_status_on_widget(c);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
            // Update the widget on first start -------------
        }


        //
        //        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //        final Intent i = new Intent(context, UpdateService.class);
        //
        //        // Update the widget on first start -------------
        //        final Context c = context;
        //        final Thread th = new Thread(new Runnable()
        //        {
        //            public void run()
        //            {
        //                try
        //                {
        //                    update_status_on_widget(c);
        //                }
        //                catch (Exception e)
        //                {
        //                    e.printStackTrace();
        //                }
        //            }
        //        });
        //        th.start();
        //        // Update the widget on first start -------------
        //
        //        if (service == null)
        //        {
        //            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        //        }
        //
        //        int rand_secs = randomBetween(3, 24);
        //
        //        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
        //                             (UPDATE_INERVAL_IN_MINUTES * 60000) + (rand_secs * 1000), service);
        //
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




