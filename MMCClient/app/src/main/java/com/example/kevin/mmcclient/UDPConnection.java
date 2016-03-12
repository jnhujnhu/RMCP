package com.example.kevin.mmcclient;


import android.os.Message;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class UDPConnection {

    private static final int Serverport = 12345;
    private static MainActivity m_handle;
    private static String ServerIP;
    public boolean isConnected;

    public UDPConnection(MainActivity handle) {
        m_handle = handle;
        isConnected = false;
    }

    public String getServerIP() {
        return ServerIP;
    }

    public String StartSearching () {
        new Thread(new SearServerThread()).start();
        new Thread(new ReceSeverThread()).start();
        return ServerIP;
    }

    public void StartSearchingByIP (String IP) {
        ServerIP = IP;
        new Thread(new ConServerThread()).start();
        new Thread(new ReceSeverThread()).start();
    }

    class ConServerThread implements Runnable {
        @Override
        public void run() {
            try {
                int BroadcastPort = 12345;
                String message = "darkerthanblackhistory";
                InetAddress adds = InetAddress.getByName(ServerIP);
                DatagramSocket ds = new DatagramSocket();
                DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), adds, BroadcastPort);
                ds.send(dp);
                ds.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SearServerThread implements Runnable {
        @Override
        public void run() {
            try {
                String BroadcastIP = "255.255.255.255";
                int BroadcastPort = 12345;
                BroadcastIP = getLocalBroadcastIpAddress();
                String message = "darkerthanblackhistory";
                InetAddress adds = InetAddress.getByName(BroadcastIP);
                DatagramSocket ds = new DatagramSocket();
                DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), adds, BroadcastPort);
                ds.send(dp);
                ds.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& !inetAddress.getHostAddress().contains(":")) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getLocalBroadcastIpAddress() throws Exception{
        InetAddress address = getLocalIpAddress();
        NetworkInterface netInterface = NetworkInterface.getByInetAddress(address);
        if (!netInterface.isLoopback() && netInterface.isUp()) {
            List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                if (interfaceAddress.getBroadcast() != null) {
                    return interfaceAddress.getBroadcast().getHostAddress();
                }
            }
        }
        return null;
    }

    class ReceSeverThread implements Runnable {
        @Override
        public void run() {
            byte[] buf;
            StringBuffer sbuf;
            String RecStr = "darkerthanblackhistory";
            try {
                DatagramSocket ds = new DatagramSocket(null);
                ds.setReuseAddress(true);
                ds.bind(new InetSocketAddress(Serverport));
                DatagramPacket rdp;
                while (RecStr.equals("darkerthanblackhistory")) {
                    sbuf = new StringBuffer();
                    buf = new byte[1024];
                    rdp = new DatagramPacket(buf, buf.length);
                    ds.receive(rdp);
                    int i;
                    for (i = 0; i < 1024; i++) {
                        if (buf[i] == 0) {
                            break;
                        }
                        sbuf.append((char) buf[i]);
                    }
                    RecStr = sbuf.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ServerIP = RecStr;
            isConnected = true;
            Message msg = new Message();
            msg.obj = ServerIP+" Connected!";
            m_handle.handler.sendMessage(msg);
        }
    }
}
