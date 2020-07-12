package com.erenutku.updatingwidgetexample;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class UpdateService extends Service
{

    final static String STATUS_URL = "https://hodors.cyber.coffee/status.json";
    private static final String TAG = "MetalabWS";

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "onBind");
        return null;
    }

    static String get_url_content(String url) throws IOException
    {
        Request request = new Request.Builder().url(url).build();


        final OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                writeTimeout(120, TimeUnit.SECONDS).
                readTimeout(120, TimeUnit.SECONDS).
                build();

        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }

    public static void update_status_on_widget(Context c)
    {
        Log.d(TAG, "get_status:start");

        String door_status = "Closed";
        boolean door_is_open = false;
        boolean connection_error = true;
        String status = "ERROR";
        try
        {
            status = get_url_content(STATUS_URL);
            connection_error = false;
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
        else if (status.contains("\"closed\""))
        {
            door_status = "Closed";
            door_is_open = false;
        }
        else
        {
            connection_error = true;
        }

        String datetime_string = DateUtils.formatDateTime(c, System.currentTimeMillis(),
                                                          DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE |
                                                          DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR |
                                                          DateUtils.FORMAT_SHOW_TIME);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.updating_widget);
        ComponentName thisWidget = new ComponentName(c, UpdatingWidget.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        int widget_wanted_text_size_int = 18;
        try
        {
            String widget_wanted_text_size = prefs.getString("widget_text_size", "18");
            widget_wanted_text_size_int = Integer.parseInt(widget_wanted_text_size);
        }
        catch (Exception e)
        {

        }

        // --- set custom text size ---
        remoteViews.setTextViewTextSize(R.id.tvWidget, COMPLEX_UNIT_SP, widget_wanted_text_size_int);
        remoteViews.setTextViewTextSize(R.id.tvWidget2, COMPLEX_UNIT_SP, widget_wanted_text_size_int);
        remoteViews.setTextViewTextSize(R.id.tvWidget3, COMPLEX_UNIT_SP, widget_wanted_text_size_int);
        // --- set custom text size ---

        if (connection_error)
        {
            remoteViews.setViewVisibility(R.id.box, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.box2, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.box3, View.VISIBLE);

            remoteViews.setTextViewText(R.id.tvWidget3, "Error ..." + "\n" + datetime_string);
        }
        else
        {
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

            remoteViews.setTextViewText(R.id.tvWidget3, door_status + "\n" + datetime_string);

        }

        remoteViews.setTextViewText(R.id.tvWidget, door_status + "\n" + datetime_string);
        remoteViews.setTextViewText(R.id.tvWidget2, door_status + "\n" + datetime_string);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        Log.d(TAG, "get_status:end");
    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand:start");

        final Context c = this;
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

        int res = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand:end");
        return res;
    }


}
