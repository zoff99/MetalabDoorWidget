package com.erenutku.updatingwidgetexample;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateService extends Service
{

    final static String STATUS_URL = "https://hodors.cyber.coffee/status.json";
    private static final String TAG = "MetalabWS";
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "onBind");
        return null;
    }

    String get_url_content(String url) throws IOException
    {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }

    public void update_status_on_widget()
    {
        Log.d(TAG, "get_status:start");

        String door_status = "Closed";
        boolean door_is_open = false;
        String status = "ERROR";
        try
        {
            status = get_url_content(STATUS_URL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (status.contains("\"open\""))
        {
            door_status = "Open";
            door_is_open = true;
        }

        String datetime_string = DateUtils.formatDateTime(this, System.currentTimeMillis(),
                                                          DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE |
                                                          DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR |
                                                          DateUtils.FORMAT_SHOW_TIME);

        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.updating_widget);
        ComponentName thisWidget = new ComponentName(context, UpdatingWidget.class);


        if (door_is_open)
        {
            remoteViews.setViewVisibility(R.id.box, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.box2, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.box3, View.INVISIBLE);
        }
        else
        {
            remoteViews.setViewVisibility(R.id.box, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.box2, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.box3, View.INVISIBLE);
        }

        remoteViews.setTextViewText(R.id.tvWidget, door_status + "\n" + datetime_string);
        remoteViews.setTextViewText(R.id.tvWidget2, door_status + "\n" + datetime_string);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        Log.d(TAG, "get_status:end");
    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand:start");

        final Thread th = new Thread(new Runnable()
        {
            public void run()
            {
                update_status_on_widget();
            }
        });
        th.start();

        int res = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand:end");
        return res;
    }


}
