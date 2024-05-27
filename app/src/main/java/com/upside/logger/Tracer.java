package com.upside.logger;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.List;

public class Tracer extends Service {

    // list the active processes each `time` ms
    //final private int time = 1000;
    //public ActivityManager am;
    Context context = this;
    UsageStatsManager manager;

    // list the active processes each `time` ms
    protected static final long DEFAULT_TIMEOUT = 1000;
    private ProcessTracer tracer;
    public List<UsageStats> runningAppProcessInfo;
    private HandlerThread mThread;
    private Handler mHandler;


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
    /*
    Runnable processTracer = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis(), System.currentTimeMillis() + 1000);
                } catch (Exception e) {
                    Log.e("Running Processes", "Error: " + e);
                }
                for(UsageStats process : runningAppProcessInfo){
                    String processName = process.getPackageName();
                    Log.d("Running Processes", "Process running: " + processName);
                }
            } finally {
                mHandler.postDelayed(processTracer, time);
            }
        }
    };
    void startTracing() {
        Log.e("Running Processes", "try to show processes");
        processTracer.run();
    }
    */

    private class ProcessTracer implements Runnable {
        @Override
        public void run() {
            //Log.e("Running Processes", "Tracing..");
            try {
                // list the process running from 10 seconds ago to now
                runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()-10000, System.currentTimeMillis());
                for (UsageStats process : runningAppProcessInfo) {
                    String processName = process.getPackageName();
                    Log.e("Running Processes", "Process running: " + processName);
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
}
