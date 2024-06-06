package com.upside.logger;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import android.provider.Settings.Secure;
import android.content.Context;

public class SensorLogger extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public Context context;

    public File file_acc;
    public File file_gyro;
    public FileWriter outfile_acc;
    public FileWriter outfile_gyro;


    @Override
    public void onCreate() {
        super.onCreate();

        // get the device's unique ID
        context = this;
        String android_id = Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        // generate random string for each new file
        String sha_acc = randomSHA();
        String sha_gyro = randomSHA();

        file_acc = new File(this.downloadsDir, "log_accelerometer" + "_" + android_id + "_" + sha_acc + ".csv");
        file_gyro = new File(this.downloadsDir, "log_gyroscope" + "_" + android_id + "_" + sha_gyro + ".csv");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                System.out.println("Accelerometer sensor registered");
            } else {
                Log.e("AccelerometerLogging", "Accelerometer sensor not available");
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                System.out.println("Gyroscope sensor registered");
            } else {
                Log.e("GyroscopeLogging", "Gyroscope sensor not available");
            }
        }

        try{
            if(!this.file_gyro.exists()) {
                boolean created = this.file_gyro.createNewFile();
                if(created)
                    Log.e("GyroscopeLogging", "file_gyro created");
                else
                    Log.e("GyroscopeLogging", "file_gyro not created");
            }
            this.outfile_gyro = new FileWriter(this.file_gyro.getAbsoluteFile());
            String[] header_gyro = { "gyroscope.x", "gyroscope.y", "gyroscope.z" };
            outfile_gyro.append(String.join(",", header_gyro));
            outfile_gyro.append("\n");

        } catch (IOException e) {
            Log.e("GyroscopeLogging", "Error opening file_gyro for writing");
        }

        try{
            if (!this.file_acc.exists()) {
                boolean created = this.file_acc.createNewFile();
                if (created)
                    Log.e("AccelerometerLogging", "file_acc created");
                else
                    Log.e("AccelerometerLogging", "file_acc not created");
            }
            this.outfile_acc = new FileWriter(this.file_acc.getAbsoluteFile());
            String[] header = { "accelerometer.x", "accelerometer.y", "accelerometer.z" };
            outfile_acc.append(String.join(",", header));
            outfile_acc.append("\n");
        } catch (IOException e) {
            Log.e("AccelerometerLogging", "Error opening file_acc for writing");
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
            System.out.println("Accelerometer and Gyroscope sensors unregistered");
        }

        try{
            //this.bw.close();
            outfile_acc.flush();
            outfile_acc.close();
        } catch (IOException e) {
            Log.e("AccelerometerLogging", "Error closing file_acc");
        }
        try{
            outfile_gyro.flush();
            outfile_gyro.close();
        } catch (IOException e) {
            Log.e("GyroscopeLogging", "Error closing file_gyro");
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
                outfile_acc.append(String.join(",", data));
                outfile_acc.append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Accelerometer data written to file_acc");
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String[] data = { Float.toString(x), Float.toString(y), Float.toString(z)};
            try {
                outfile_gyro.append(String.join(",", data));
                outfile_gyro.append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Gyroscope data written to file_acc");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public String randomSHA(){

        SecureRandom r = new SecureRandom();
        byte[] randNum = new byte[1000];
        r.nextBytes(randNum);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

        try {
            md.update(randNum);
            byte[] hashValue = md.digest();

            byte[] encoded = Base64.getEncoder().encode(hashValue);
            String hash = new String(encoded, StandardCharsets.UTF_8);
            hash = hash.replaceAll("[^a-zA-Z0-9]", "");
            Log.e("RANDOM HASH VALUE", "RANDOM HASH:" + hash);
            return hash;
        } catch (Exception e) {
            Log.e("HASH ERROR", "ERROR:" + e);
            return null;
        }
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}