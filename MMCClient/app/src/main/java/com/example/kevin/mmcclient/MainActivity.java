package com.example.kevin.mmcclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {


    private m_Dialog dialog;
    private final static int CTL_TOUCHSCREEN = 0;
    private final static int CTL_GRAVITY = 1;
    private final static int CTL_LINEARACCELERATION = 2;
    private AccelerationImp ACI;
    private GravityImp GVI;
    private TouchScreenImp TSI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new m_Dialog(MainActivity.this);
        (findViewById(R.id.touchpad)).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        switch(dialog.getMode()){
            case CTL_TOUCHSCREEN:
                (findViewById(R.id.touchpad)).setVisibility(View.INVISIBLE);
                (findViewById(R.id.leftmouse)).setVisibility(View.VISIBLE);
                (findViewById(R.id.midmouse)).setVisibility(View.VISIBLE);
                (findViewById(R.id.rightmouse)).setVisibility(View.VISIBLE);
                break;
            case CTL_GRAVITY:
                if(GVI != null) {
                    GVI.DisableSensor();
                }
                break;
            case CTL_LINEARACCELERATION:
                if(ACI != null) {
                    ACI.DisableSensor();
                }
                break;
        }
        if(dialog.DialogIsClosed) {
            dialog = new m_Dialog(MainActivity.this);
        }
    }

    Handler HandleEntry = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MMC_init();
            super.handleMessage(msg);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView Servermsg = (TextView) dialog.getView().findViewById(R.id.SearchMsg);
            Servermsg.setText((String)msg.obj);
            super.handleMessage(msg);
        }
    };

    Handler HandleConnectionError = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Warning").setMessage("Connection Refused!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canCloseDialog(dialog, false);
                            MainActivity.this.finish();
                        }
                    }).create();
                    builder.setCancelable(false);
                    builder.show();
        }
    };

    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MMC_init() {
        switch(dialog.getMode()){
            case CTL_TOUCHSCREEN:
                (findViewById(R.id.leftmouse)).setVisibility(View.GONE);
                (findViewById(R.id.midmouse)).setVisibility(View.GONE);
                (findViewById(R.id.rightmouse)).setVisibility(View.GONE);
                Button touchpad = (Button) findViewById(R.id.touchpad);
                touchpad.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) touchpad.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                touchpad.setLayoutParams(params);
                TSI = new TouchScreenImp(MainActivity.this, dialog.getServerIP());
                break;
            case CTL_GRAVITY:
                GVI = new GravityImp(MainActivity.this, dialog.getServerIP());
                GVI.EnableSensor();
                break;
            case CTL_LINEARACCELERATION:
                ACI = new AccelerationImp(MainActivity.this, dialog.getServerIP());
                ACI.EnableSensor();
                break;
        }
    }

}
