package com.upside.logger;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start, stop, startTracing, stopTracing;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        if (!checkPermission()) {
            // TODO: add pop up
            // have to enable the permission to access the usage stats
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        // check if battery optimization is disabled
        // no restrictions are needed
        checkBatteryPermission();

        startTracing = findViewById(R.id.startTracingButton);
        stopTracing = findViewById(R.id.stopTracingButton);

        start = findViewById( R.id.startButton );
        stop = findViewById( R.id.stopButton );

        startTracing.setOnClickListener( this );
        stopTracing.setOnClickListener( this );

        start.setOnClickListener( this );
        stop.setOnClickListener( this );
    }

    public void onClick(View view) {

        // if start button is clicked
        if(view == start){

            // starting the service
            //startService(new Intent( this, SensorLogger.class ) );
            /*
            Intent serviceIntent = new Intent(this, SensorLogger.class);
            serviceIntent.putExtra("userID", "PROVA");
            startForegroundService(serviceIntent);
            */
            startForegroundService(new Intent(this, SensorLogger.class));
            Toast.makeText(MainActivity.this, "Logging started", Toast.LENGTH_SHORT).show();

        }

        // if stop button is clicked
        else if (view == stop){

            // stopping the service
            stopService(new Intent( this, SensorLogger.class ) );
            Toast.makeText(MainActivity.this, "Logging stopped", Toast.LENGTH_SHORT).show();

        } else if (view == startTracing) {

//            startForegroundService(new Intent(this, Tracer.class));
//            Toast.makeText(MainActivity.this, "Tracing started", Toast.LENGTH_SHORT).show();

        } else if (view == stopTracing){
//            stopService(new Intent(this, Tracer.class));
//            Toast.makeText(MainActivity.this, "Tracing stopped", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            Log.e("Permission Error", "Error:" + e);
            return false;
        }
    }

    private void checkBatteryPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }
}