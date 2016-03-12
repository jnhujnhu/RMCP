package com.example.kevin.mmcclient;

import android.os.Message;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPConnection {

    private Socket socket;
    private String ServerIP;
    private static final int Serverport = 12345;
    private String m_RMCP;
    private MainActivity handle;
    private boolean IsConnectionLost;

    public TCPConnection(MainActivity hand, String Server) {
        ServerIP = Server;
        IsConnectionLost = false;
        handle = hand;
    }

    public void SendControlMsg(String RMCP) {
        m_RMCP = RMCP;
        if(!IsConnectionLost) {
            new Thread(new SendControlMsgImp()).start();
        }
    }

    class SendControlMsgImp implements Runnable {
        @Override
        public void run() {
            synchronized (this) {
                try {
                    socket = new Socket(ServerIP, Serverport);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(m_RMCP);
                    out.close();

                } catch (Exception e) {
                   if(e.getMessage().contains("ECONNREFUSED")) {
                        IsConnectionLost = true;
                        Message msg = new Message();
                        msg.obj = "Damn!";
                        handle.HandleConnectionError.sendMessage(msg);
                   }
                }
            }
        }
    }
}
