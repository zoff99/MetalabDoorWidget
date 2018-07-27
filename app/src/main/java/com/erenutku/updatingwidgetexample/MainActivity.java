package com.erenutku.updatingwidgetexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.erenutku.updatingwidgetexample.UpdateService.update_status_on_widget;

public class MainActivity extends AppCompatActivity
{
    int appWidgetId;
    static TextView log_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log_text = (TextView) findViewById(R.id.log_text);

        try
        {
            Intent intent = new Intent();
            String packageName = this.getPackageName();
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (pm.isIgnoringBatteryOptimizations(packageName))
                {
                    // intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                }
                else
                {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                }
                this.startActivity(intent);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Update the widget on first start -------------
        final Context c = this;
        final Thread th = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    final long start_timestamp = System.currentTimeMillis();
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                log_text.setText("updating widget status ...");
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                    update_status_on_widget(c);
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                final long end_timestamp = System.currentTimeMillis();
                                log_text.setText("READY (took " + (long) ((end_timestamp - start_timestamp) / 1000) +
                                                 " seconds)");
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
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
}