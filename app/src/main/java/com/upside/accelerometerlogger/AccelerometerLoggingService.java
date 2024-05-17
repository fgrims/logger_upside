package com.upside.accelerometerlogger;

import android.app.Service;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class AccelerometerLoggingService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public File file = new File(this.downloadsDir, "accelerometer_log.txt");
    public FileWriter outfile;
    public BufferedWriter bw;


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                System.out.println("Accelerometer sensor registered");
            } else {
                Log.e("AccelerometerLogging", "Accelerometer sensor not available");
            }
        }

        try{
            if (!this.file.exists()) {
                this.file.createNewFile();
            }
            this.outfile = new FileWriter(this.file.getAbsoluteFile());
            String[] header = { "accelerometer.x", "accelerometer.y", "accelerometer.z" };
            this.bw = new BufferedWriter(this.outfile);
            this.bw.write(Arrays.toString(header));

        } catch (IOException e) {
            Log.e("AccelerometerLogging", "Error opening file for writing");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This is necessary for the service to continue running in the background
        // Return START_STICKY to ensure the service restarts if it's killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister sensor listener on destroy
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            System.out.println("Accelerometer sensor unregistered");
        }

        try{
            this.bw.close();
        } catch (IOException e) {
            Log.e("AccelerometerLogging", "Error closing file");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String[] data = { Float.toString(x), Float.toString(y), Float.toString(z)};
            try {
                this.bw.write(Arrays.toString(data));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Accelerometer data written to file");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }
}