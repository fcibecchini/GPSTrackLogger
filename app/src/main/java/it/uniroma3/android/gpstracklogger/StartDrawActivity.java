/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.views.DrawView;
import it.uniroma3.android.gpstracklogger.views.ScaleView;


public class StartDrawActivity extends Activity implements SensorEventListener {
    private DrawView drawView;
    private ScaleView scaleView;
    ViewGroup root, scaleRoot;

    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        root = (ViewGroup) findViewById(R.id.drawlayout);
        scaleRoot = (ViewGroup) findViewById(R.id.drawscale);
        drawView = new DrawView(this);
        scaleView = new ScaleView(this);
        root.addView(drawView);
        scaleRoot.addView(scaleView);
        showScala();
        loadButtons();
        if (Session.isCompass())
            loadSensor();
        Toast.makeText(getApplicationContext(), "Premi un punto della mappa per spostarlo al centro", Toast.LENGTH_LONG).show();
    }

    private void loadButtons() {
        ImageButton fit = (ImageButton) findViewById(R.id.fit);
        ImageButton zoomIn = (ImageButton) findViewById(R.id.zoomin);
        ImageButton zoomOut = (ImageButton) findViewById(R.id.zoomout);
        ImageButton restore = (ImageButton) findViewById(R.id.restore);
        fit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit();
            }
        });
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreScale();
            }
        });
    }

    private void loadSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
    }

    @Override
    protected void onPause() {
        if (Session.isCompass()) {
            sensorManager.unregisterListener(this,
                    sensorAccelerometer);
            sensorManager.unregisterListener(this,
                    sensorMagneticField);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Session.isCompass()) {
            sensorManager.registerListener(this,
                    sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorMagneticField,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        drawView.invalidate();
    }

    private void fit() {
        drawView.fit();
        drawView.invalidate();
        showScala();
    }

    private void restoreScale() {
        drawView.restoreScale();
        drawView.invalidate();
        showScala();
    }

    private void zoomIn() {
        if (scaleView.getXMetersPerInch()>30 &&
                scaleView.getYMetersPerInch()>30)
            drawView.setScala(1.5);
        showScala();
        drawView.invalidate();
    }

    private void zoomOut() {
        if (scaleView.getXMetersPerInch()<100000 &&
                scaleView.getYMetersPerInch()<100000)
            drawView.setScala(1 / 1.5);
        showScala();
        drawView.invalidate();
    }

    private void showScala() {
        scaleView.invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Session.isCompass()) {
            switch(event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    for(int i =0; i < 3; i++){
                        valuesAccelerometer[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for(int i =0; i < 3; i++){
                        valuesMagneticField[i] = event.values[i];
                    }
                    break;
            }

            boolean success = SensorManager.getRotationMatrix(
                    matrixR,
                    matrixI,
                    valuesAccelerometer,
                    valuesMagneticField);

            if (success) {
                SensorManager.getOrientation(matrixR, matrixValues);
                float azimuth = (float) Math.toDegrees(matrixValues[0]);
                drawView.setAzimuth(azimuth);
                drawView.invalidate();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
