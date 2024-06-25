package com.upside.logger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
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
import android.os.PowerManager.WakeLock;
import android.provider.Settings.Secure;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;

public class SensorLogger extends Service implements SensorEventListener {

    public static final String TAG = SensorLogger.class.getName();
    private static final String CHANNEL_ID = "SENSOR_CHANNEL";
    private SensorManager sensorManager;

    private WakeLock mWakeLock = null;
    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public Context context;

    public File file_acc;
    public File file_gyro;
    public FileWriter outfile_acc;
    public FileWriter outfile_gyro;

    // create
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://movementtracing.appspot.com");
    StorageReference logRefAcc;
    StorageReference logRefGyro;


    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        /*
        // get the device's unique ID
        context = this;
        String android_id = Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        // generate random string for each new file
        String sha_acc = randomSHA();
        String sha_gyro = randomSHA();

        String acc_fn = "log_accelerometer" + "_" + android_id + "_" + sha_acc + ".csv";
        String gyro_fn = "log_gyroscope" + "_" + android_id + "_" + sha_gyro + ".csv";

        file_acc = new File(this.downloadsDir, acc_fn);
        file_gyro = new File(this.downloadsDir, gyro_fn);
        logRefAcc = storageRef.child("log/" + android_id + "/accelerometer/" + acc_fn);
        logRefGyro = storageRef.child("log/" + android_id + "/gyroscope/" + gyro_fn);
        /*
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, 1000000, 1000000); // SensorManager.SENSOR_DELAY_NORMAL
                System.out.println("Accelerometer sensor registered");
            } else {
                Log.e("AccelerometerLogging", "Accelerometer sensor not available");
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, 1000000, 1000000); //SensorManager.SENSOR_DELAY_NORMAL
                System.out.println("Gyroscope sensor registered");
            } else {
                Log.e("GyroscopeLogging", "Gyroscope sensor not available");
            }
        }
        */
        /*
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
        }*/
    }

    public void createFile(String userID) {
        // get the device's unique ID

        // generate random string for each new file
        String sha_acc = randomSHA();
        String sha_gyro = randomSHA();

        String acc_fn = "log_accelerometer" + "_" + userID + "_" + sha_acc + ".csv";
        String gyro_fn = "log_gyroscope" + "_" + userID + "_" + sha_gyro + ".csv";

        file_acc = new File(this.downloadsDir, acc_fn);
        file_gyro = new File(this.downloadsDir, gyro_fn);
        logRefAcc = storageRef.child("log/" + userID + "/accelerometer/" + acc_fn);
        logRefGyro = storageRef.child("log/" + userID + "/gyroscope/" + gyro_fn);

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
        String userID = intent.getStringExtra("userID");
        createFile(userID);
        mWakeLock.acquire();
        createNotificationChannel();
        startMeasure();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(2, getNotification());
        startForeground(2, getNotification());
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

        Uri f_acc = Uri.fromFile(file_acc);
        Uri f_gyro = Uri.fromFile(file_gyro);

        logRefAcc.putFile(f_acc);
        logRefGyro.putFile(f_gyro);

        mWakeLock.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.e("HELLO", "SENSOR SERVICE ALIVE");
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

    @SuppressLint("ObsoleteSdkInt")
    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Logging Service")
                .setContentText("Logging Movement")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.S) {

            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        return builder.build();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Logger Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    public static String randomSHA(){

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

    protected void startMeasure() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // SensorManager.SENSOR_DELAY_NORMAL
                System.out.println("Accelerometer sensor registered");
            } else {
                Log.e("AccelerometerLogging", "Accelerometer sensor not available");
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL); //SensorManager.SENSOR_DELAY_NORMAL
                System.out.println("Gyroscope sensor registered");
            } else {
                Log.e("GyroscopeLogging", "Gyroscope sensor not available");
            }
        }
    }
}