package com.upside.logger;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import kotlin.collections.ArrayDeque;

public class Tracer extends Service {

    Context context = this;
    UsageStatsManager manager;

    // list the active processes each `time` ms
    protected static final long DEFAULT_TIMEOUT = 1000;
    private ProcessTracer tracer;
    public List<UsageStats> runningAppProcessInfo;
    private HandlerThread mThread;
    private Handler mHandler;
    public boolean first_time = true;
    ArrayList<String> whitelist = new ArrayList<>();
    HashMap<String, Long> alarmProcess = new HashMap<>();


    public void onCreate() {
        super.onCreate();
        try {
            manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } catch(Exception e) {
            Log.e("Running Processes", "Error: " +e);
        }

        this.getSystemService( ACTIVITY_SERVICE );
        //mHandler = new Handler();
        mThread = new HandlerThread("mThread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        tracer = new ProcessTracer();
        try {
            tracer.run();
            Log.e("Running Processes", "Start Tracing");
        } catch(Exception e) {
            Log.e("Running Processes", "Error:",e);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // This is necessary for the service to continue running in the background
        // Return START_STICKY to ensure the service restarts if it's killed by the system
        mHandler.removeCallbacks(tracer);
        mHandler.postDelayed(tracer, DEFAULT_TIMEOUT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ProcessTracer implements Runnable {
        @Override
        public void run() {
            try {
                // list the process running from 10 seconds ago to now
                runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, System.currentTimeMillis()-10000, System.currentTimeMillis());
                //Log.e("Running process", "List size: " + runningAppProcessInfo.size());

                if (first_time == true) {
                    // if this is the first log
                    // write the entire log in ArrayList
                    for (UsageStats process : runningAppProcessInfo) {
                        String processName = process.getPackageName();
                        whitelist.add(processName);
                    }
                    first_time = false;

                    // for debug
                    deleteElement();
                    Log.d("Whitelist", "size: " + whitelist.size());
                    for (String element : whitelist) {
                        Log.d("Whitelist", "Whitelist " + element);
                    }
                }
                for (UsageStats process : runningAppProcessInfo) {
                    String processName = process.getPackageName();
                    //Log.e("Running Processes", "Process running: " + processName);
                    if (!whitelist.contains(processName)) {
                        // if it is the first time this package is opened
                        // add it to alarm list with timestamp
                        //Log.e("CAPIRE", "Name: " + processName + " LastTimeForegroundServiceUsed: " + process.getLastTimeVisible());
                        if(!alarmProcess.containsKey(processName)) {
                            alarmProcess.put(processName, process.getLastTimeVisible());
                            Log.e("ALARM", "ALARM " + processName + " NOT AUTHORIZED!");
                            //Log.e("ALARM", "this last time = " + process.getLastTimeForegroundServiceUsed());
                        } else if (alarmProcess.containsKey(processName) && process.getLastTimeVisible() > alarmProcess.get(processName)) {
                            Log.e("HERE", "this last time = " + process.getLastTimeVisible() + " || before " + alarmProcess.get(processName));
                            // else if the process already is in the alarm list
                            // and its timestamp is greater than the one already saved
                            // then alarm and update
                            alarmProcess.replace(processName, process.getLastTimeVisible());
                            Log.e("ALARM", "ALARM " + process + " NOT AUTHORIZED!");
                        }
                    }
                }
                for (String i : alarmProcess.keySet()) {
                    Log.e("ALARM LIST","key: " + i + " value: " + alarmProcess.get(i));
                }
            } catch (Exception e) {
                Log.e("Running Processes", "Error: " + e);
            }
            mHandler.postDelayed(this, DEFAULT_TIMEOUT);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(tracer);
        mThread.quitSafely();
        super.onDestroy();
    }

    public void deleteElement() {
        // for debug
        whitelist.remove("tv.twitch.android.app");
        whitelist.remove("com.google.android.youtube");
        whitelist.remove("com.hp.printercontrol");
        whitelist.remove("com.netflix.mediaclient");
        whitelist.remove("com.amazon.avod.thirdpartyclient");
        whitelist.remove("com.example.era");

    }
}
