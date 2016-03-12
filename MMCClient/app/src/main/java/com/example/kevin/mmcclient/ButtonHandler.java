package com.example.kevin.mmcclient;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by Kevin on 12/9/15.
 */
public class ButtonHandler {

    private boolean IsMouseClicked;
    private boolean IsScrolled;
    private boolean IsMoved;
    private MainActivity handle;
    private TCPConnection TC;
    private int MouseClickCount;
    private float[] StartPoint;
    private float LastPointY;
    private long m_TimerStart;

    private final static int CTL_MOUSEHOLDJUDGETRANGE = 10;

    public ButtonHandler(MainActivity hand, String ServerIP) {
        handle = hand;
        IsMoved = false;
        MouseClickCount = 0;
        StartPoint = new float[]{0, 0};
        LastPointY = 0;
        TC = new TCPConnection(handle, ServerIP);
        RLMouseClickHandler();
        MidMouseEventHandler();
    }
    private void RLMouseClickHandler() {
        Button LeftButton = (Button) handle.findViewById(R.id.leftmouse);
        Button RightButton = (Button) handle.findViewById(R.id.rightmouse);

        LeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MouseClickCount ++;
                try {
                    if (MouseClickCount == 2) {
                        TC.SendControlMsg(new RMCPImp().CreateRMCP(1, 0, 0, false, true, false, false, false));
                        MouseClickCount = 0;
                    }

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(200);
                                if (MouseClickCount == 1) {
                                    TC.SendControlMsg(new RMCPImp().CreateRMCP(1, 0, 0, true, false, false, false, false));
                                }
                                MouseClickCount = 0;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });


        RightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TC.SendControlMsg(new RMCPImp().CreateRMCP(1, 0, 0, false, false, true, false, false));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void MidMouseEventHandler() {
        Button MidButton = (Button) handle.findViewById(R.id.midmouse);

        MidButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float[] mousefloat = new float[]{0, 0};
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            IsMoved = false;
                            StartPoint[0] = event.getX();
                            StartPoint[1] = event.getY();
                            LastPointY = event.getY();
                            m_TimerStart = System.currentTimeMillis();
                            IsMoved = false;
                            break;

                        case MotionEvent.ACTION_MOVE:

                            if (!IsMoved) {
                                mousefloat[0] = event.getX() - StartPoint[0];
                                mousefloat[1] = event.getY() - StartPoint[1];
                            }

                            TC.SendControlMsg(new RMCPImp().CreateRMCP(0, 0, (event.getY() - LastPointY) / 2, false, false, false, false, true));

                            if (mousefloat[0] > CTL_MOUSEHOLDJUDGETRANGE || mousefloat[1] > CTL_MOUSEHOLDJUDGETRANGE) {
                                IsMoved = true;
                            }
                            LastPointY = event.getY();
                            break;

                        case MotionEvent.ACTION_UP:
                            long deltatime = System.currentTimeMillis() - m_TimerStart;
                            if (deltatime < 130 && !IsMoved) {
                                TC.SendControlMsg(new RMCPImp().CreateRMCP(1, 0, 0, false, false, false, true, false));
                            }
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }
}
