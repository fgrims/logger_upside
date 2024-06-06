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
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tracer extends Service {

    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public File file;
    public FileWriter outfile;

    boolean flag = false; // set to true if you want to write the processes on a file

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

        file = new File(this.downloadsDir, "processes.txt");

        try{
            if(!this.file.exists()) {
                boolean created = this.file.createNewFile();
                if(created)
                    Log.e("TRACING", "file for processes created");
                else
                    Log.e("TRACING", "file for processes not created");
            }
            this.outfile = new FileWriter(this.file.getAbsoluteFile());
        } catch (IOException e) {
            Log.e("TRACING", "ERROR" + e);
        }

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
                Log.e("HELLO", "ALIVE");
                // list the process running from 10 seconds ago to now
                runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, System.currentTimeMillis()-10000, System.currentTimeMillis());
                //Log.e("Running process", "List size: " + runningAppProcessInfo.size());

                if (first_time) {
                    // if this is the first log
                    // write the entire log in ArrayList whitelist
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

                // if flag is true, write the processes in a file
                // this is used the first time the tablet is used to log the native processes
                if (flag) writeOnFile(runningAppProcessInfo);

                for (UsageStats process : runningAppProcessInfo) {
                    String processName = process.getPackageName();
                    //Log.e("Running Processes", "Process running: " + processName);
                    if (!whitelist.contains(processName)) {
                        // if there's a process not in the white list
                        // add it to alarm list with timestamp

                        //Log.e("CHECK", "Name: " + processName + " LastTimeForegroundServiceUsed: " + process.getLastTimeVisible());
                        if(!alarmProcess.containsKey(processName)) {
                            // if the process appear the first time in the whitelist
                            // add it
                            Log.e("ALARM", "ALARM " + processName + " NOT AUTHORIZED THE FIRST TIME!");
                            alarmProcess.put(processName, process.getLastTimeVisible());
                        } else if (alarmProcess.containsKey(processName) && process.getLastTimeVisible() > alarmProcess.get(processName)) {
                            // if the process already is in the alarm
                            // and its timestamp is greater than the one already saved
                            // then alarm and update

                            //Log.e("HERE", processName + " -> this last time = " + process.getLastTimeVisible() + " || before " + alarmProcess.get(processName));

                            alarmProcess.replace(processName, process.getLastTimeVisible());
                            int size = alarmProcess.size();
                            Log.e("ALARM", "ALARM 2: " + processName + " NOT AUTHORIZED ANOTHER TIME!");
                            Log.e("TEST", "HASH MAP SIZE : " + size);
                        }
                    }
                }
                // for debuggig
                /*
                for (String i : alarmProcess.keySet()) {
                    Log.e("ALARM LIST","key: " + i + " value: " + alarmProcess.get(i));
                }*/
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
        // for debug purposes
        whitelist.remove("tv.twitch.android.app");
        whitelist.remove("com.google.android.youtube");
        whitelist.remove("com.hp.printercontrol");
        whitelist.remove("com.netflix.mediaclient");
        whitelist.remove("com.amazon.avod.thirdpartyclient");
        whitelist.remove("com.example.era");

    }

    public void writeOnFile(List<UsageStats> l) {
        // method used the first time the tablet turned on
        // create a file with all native package used to create the whitelist

        for (UsageStats process : l) {
            String processName = process.getPackageName();
            try {
                outfile.append(processName);
                outfile.append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        flag = false;
        Log.e("TRACING", "WRITE ON FILE");

        try{
            outfile.flush();
            outfile.close();
        } catch (IOException e) {
            Log.e("TRACING", "Error closing file");
        }
    }
}
