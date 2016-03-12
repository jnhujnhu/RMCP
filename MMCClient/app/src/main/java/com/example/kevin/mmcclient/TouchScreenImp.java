package com.example.kevin.mmcclient;


import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class TouchScreenImp {

    private MainActivity handle;
    private float[] StartPoint;
    private float[] StartPoint_2;
    private float[] LastPoint;
    private float[] LastPoint_2;
    private String ServerIP;
    private TCPConnection TC;
    private long m_TimerStart;
    private boolean IsMoved;
    private boolean IsCrowded;
    private boolean IsDoubleClick;
    private boolean IsSingleClick;
    private boolean IsScrolled;
    private final static int CTL_MOUSEHOLDJUDGETRANGE = 10;
    private final static int DOUBLE_TAP_TIMEOUT = 130;
    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    public TouchScreenImp(MainActivity hand, String Server) {
        handle = hand;
        IsMoved = false;
        IsCrowded = false;
        IsDoubleClick = false;
        IsSingleClick = false;
        IsScrolled = false;
        StartPoint = new float[]{0, 0};
        LastPoint = new float[]{0, 0};
        StartPoint_2 = new float[]{0, 0};
        LastPoint_2 = new float[]{0, 0};
        ServerIP = Server;
        TC = new TCPConnection(handle, ServerIP);
        TouchPadInit();
    }

    private void MaintainConnection() {
        new Thread(new MaintainThread()).start();
    }

    class MaintainThread implements Runnable {
        @Override
        public void run() {
            while(!IsCrowded) {
                try {
                    TC.SendControlMsg(new RMCPImp().CreateRMCP(3, 0, 0, false, false, false, false, false));
                    Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isConsideredDoubleTap(MotionEvent firstUp, MotionEvent secondDown) {
        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
        return deltaX * deltaX + deltaY * deltaY < 5000;
    }

    private void TouchPadInit() {
        Button m_TouchPad = (Button) handle.findViewById(R.id.touchpad);

        MaintainConnection();
        final float[] mousefloat = new float[]{0, 0};
        final float[] mousefloat_2 = new float[]{0, 0};
        m_TouchPad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointercount = event.getPointerCount();
                if(pointercount == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            IsScrolled = false;
                            IsDoubleClick = false;
                            IsSingleClick = false;
                            if (!IsMoved && mPreviousUpEvent != null && mCurrentDownEvent != null && isConsideredDoubleTap(mPreviousUpEvent, event)) {
                                try {
                                    IsDoubleClick = true;
                                    TC.SendControlMsg(new RMCPImp().CreateRMCP(0, 0, 0, false, true, false, false, false));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mCurrentDownEvent = MotionEvent.obtain(event);

                            StartPoint[0] = event.getX();
                            StartPoint[1] = event.getY();
                            LastPoint[0] = event.getX();
                            LastPoint[1] = event.getY();
                            m_TimerStart = System.currentTimeMillis();
                            IsMoved = false;
                            IsCrowded = true;
                            break;

                        case MotionEvent.ACTION_MOVE:

                            if (!IsMoved) {
                                mousefloat[0] = event.getX() - StartPoint[0];
                                mousefloat[1] = event.getY() - StartPoint[1];
                            }

                            if(!IsScrolled && !IsDoubleClick && !IsSingleClick) {
                                try {
                                    TC.SendControlMsg(new RMCPImp().CreateRMCP(0, event.getX() - LastPoint[0], event.getY() - LastPoint[1], false, false, false, false, false));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (mousefloat[0] > CTL_MOUSEHOLDJUDGETRANGE || mousefloat[1] > CTL_MOUSEHOLDJUDGETRANGE) {
                                IsMoved = true;
                            }

                            LastPoint[0] = event.getX();
                            LastPoint[1] = event.getY();
                            break;

                        case MotionEvent.ACTION_UP:
                            IsScrolled = true;
                            mPreviousUpEvent = MotionEvent.obtain(event);
                            long deltatime = System.currentTimeMillis() - m_TimerStart;
                            if (deltatime < 130 && !IsMoved) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(300);
                                            if (!IsDoubleClick) {
                                                IsSingleClick = true;
                                                TC.SendControlMsg(new RMCPImp().CreateRMCP(0, 0, 0, true, false, false, false, false));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                IsCrowded = false;
                                MaintainConnection();
                                break;
                            }
                    }
                }
                else if(pointercount == 2) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            StartPoint[0] = event.getX(0);
                            StartPoint[1] = event.getY(0);
                            LastPoint_2[0] = event.getX(0);
                            LastPoint_2[1] = event.getY(0);
                            StartPoint_2[0] = event.getX(1);
                            StartPoint_2[1] = event.getY(1);
                            m_TimerStart = System.currentTimeMillis();
                            IsMoved = false;
                            IsCrowded = true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!IsMoved) {
                                mousefloat[0] = event.getX(0) - StartPoint[0];
                                mousefloat[1] = event.getY(0) - StartPoint[1];
                                mousefloat_2[0] = event.getX(1) - StartPoint_2[0];
                                mousefloat_2[1] = event.getY(1) - StartPoint_2[1];
                            }

                            try {
                                TC.SendControlMsg(new RMCPImp().CreateRMCP(0, 0, (event.getY(0) - LastPoint_2[1])/5, false, false, false, false, true));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (mousefloat[0] > CTL_MOUSEHOLDJUDGETRANGE || mousefloat[1] > CTL_MOUSEHOLDJUDGETRANGE
                                    || mousefloat_2[0] > CTL_MOUSEHOLDJUDGETRANGE || mousefloat_2[1] > CTL_MOUSEHOLDJUDGETRANGE) {
                                IsMoved = true;
                                IsScrolled = true;
                            }

                            LastPoint_2[0] = event.getX(0);
                            LastPoint_2[1] = event.getY(0);
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            mPreviousUpEvent = MotionEvent.obtain(event);
                            long deltatime = System.currentTimeMillis() - m_TimerStart;
                            if (deltatime < 130 && !IsMoved) {
                                    try {
                                        Thread.sleep(100);
                                        TC.SendControlMsg(new RMCPImp().CreateRMCP(0, 0, 0, false, false, true, false, false));
                                        Thread.sleep(100);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                            }
                            IsCrowded = false;
                            MaintainConnection();
                            break;
                    }
                }
                return true;
            }
        });

    }

}
