package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Connection {

    public static final int PORT = 12345;

    public String ReceiveBroadcast(String ServerIp) {
        String DeviceIp = "";
        DatagramSocket ds;
        DatagramPacket dp ,sdp;
        byte[] buf = new byte[1024];
        StringBuffer sbuf = new StringBuffer();
        try {
            ds = new DatagramSocket(PORT);
            dp = new DatagramPacket(buf, buf.length);
            ds.receive(dp);
            DeviceIp = dp.getAddress().getHostAddress();
            int i;
            for (i = 0; i < 1024; i++) {
                if (buf[i] == 0) {
                    break;
                }
                sbuf.append((char) buf[i]);
            }
            System.out.println("Received broadcast message：" + sbuf.toString());
            InetAddress DeviceAddress = InetAddress.getByName(DeviceIp);
            sdp = new DatagramPacket(ServerIp.getBytes(), ServerIp.length(), DeviceAddress, PORT);
            ds.send(sdp);
            ds.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DeviceIp;
    }
}
