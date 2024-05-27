package com.upside.logger;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start, stop, startTracing, stopTracing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // have to enable the permission to access the usage stats
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        // assigning ID of startButton

        startTracing = (Button) findViewById(R.id.startTracingButton);
        stopTracing = (Button) findViewById(R.id.stopTracingButton);
        // to the object start
        start = (Button) findViewById( R.id.startButton );
        // assigning ID of stopButton
        // to the object stop
        stop = (Button) findViewById( R.id.stopButton );

        // declaring listeners for the
        // buttons to make them respond
        // correctly according to the process
        startTracing.setOnClickListener( this );
        stopTracing.setOnClickListener( this );
        start.setOnClickListener( this );
        stop.setOnClickListener( this );
    }

    public void onClick(View view) {

        // process to be performed
        // if start button is clicked
        if(view == start){

            // starting the service
            startService(new Intent( this, SensorLogger.class ) );
            Toast.makeText(MainActivity.this, "Logging started", Toast.LENGTH_SHORT).show();

        }

        // process to be performed
        // if stop button is clicked
        else if (view == stop){

            // stopping the service
            stopService(new Intent( this, SensorLogger.class ) );
            Toast.makeText(MainActivity.this, "Logging stopped", Toast.LENGTH_SHORT).show();

        } else if (view == startTracing) {
            startService(new Intent(this, Tracer.class));
            Toast.makeText(MainActivity.this, "Tracing started", Toast.LENGTH_SHORT).show();

        } else if (view == stopTracing){
            stopService(new Intent(this, Tracer.class));
            Toast.makeText(MainActivity.this, "Tracing stopped", Toast.LENGTH_SHORT).show();
        }
    }
}