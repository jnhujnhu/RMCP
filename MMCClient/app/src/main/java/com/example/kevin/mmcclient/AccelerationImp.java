package com.example.kevin.mmcclient;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;


public class AccelerationImp {

    private MainActivity handle;
    private SensorManager m_SensorManager;
    private Sensor Accelerator;
    float[] AcceleratorValues = new float[3];
    private TCPConnection TC;
    private String ServerIP;
    private ButtonHandler BH;

    public AccelerationImp(MainActivity hand, String Server) {

        ServerIP = Server;
        TC = new TCPConnection(handle, ServerIP);
        handle = hand;
        BH = new ButtonHandler(handle, Server);
    }

    public void EnableSensor() {

        m_SensorManager = (SensorManager) handle.getSystemService(Context.SENSOR_SERVICE);
        Accelerator = m_SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(m_SensorManager == null) {
            Log.v("sensor..", "Sensors not supported");
        }

        m_SensorManager.registerListener(m_SensorListener, Accelerator, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void DisableSensor() {
        if(m_SensorManager != null) {
            m_SensorManager.unregisterListener(m_SensorListener);
            m_SensorManager = null;
        }
    }

    final SensorEventListener m_SensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
            {
                AcceleratorValues = event.values;
            }
            try {
                TC.SendControlMsg(new RMCPImp().CreateRMCP(2, AcceleratorValues[0], AcceleratorValues[1], false, false, false, false, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };


}
