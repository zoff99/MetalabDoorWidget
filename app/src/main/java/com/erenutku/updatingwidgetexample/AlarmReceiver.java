package com.erenutku.updatingwidgetexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
    private static final String TAG = "AlaramRecv";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Log.d(TAG, "onReceive");
    }
}
