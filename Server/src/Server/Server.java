package Server;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;


public class Server {
    private final static int PORT = 12345;
    private final static int CTL_TOUCHSCREEN = 0;
    private final static int CTL_GRAVITY = 1;
    private final static int CTL_LINEARACCELERATION = 2;
    private final static int CTL_MAINTAIN = 3;
    private final static int CTL_LEFTMOUSE = 0;
    private final static int CTL_RIGHTMOUSE = 1;
    private final static int CTL_MIDMOUSE = 2;

    public static final String Magic = "RemoteMouseControlProtocol1.0";
    public static final String Delimiter = " ";

    private final static int POS_MAGIC = 0;
    private final static int POS_MODE = 1;
    private final static int POS_PARAMETERX = 2;
    private final static int POS_PARAMETERY = 3;
    private final static int POS_LEFTMOUSESCLICK = 4;
    private final static int POS_LEFTMOUSEDCLICK = 5;
    private final static int POS_RIGHTMOUSESCLICK = 6;
    private final static int POS_MIDMOUSESCLICK = 7;
    private final static int POS_MOUSESCROLL = 8;

    private static String ServerIp;
    private static long Lastmillis;
    private static ImpControl IC;

    public Server(){
        try {
            IC = new ImpControl();
            InetAddress address = InetAddress.getLocalHost();
            ServerIp = address.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerIP() {
        return ServerIp;
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket client = serverSocket.accept();
                Lastmillis = System.currentTimeMillis();
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                MouseControl MC = new MouseControl();
                AccelerationImp AI = new AccelerationImp();
                GravityImp GI = new GravityImp();
                TouchScreenImp TI = new TouchScreenImp();

                DataInputStream input = new DataInputStream(socket.getInputStream());
                String clientInputStr = input.readUTF();
                String[] ReceivedStr;
                int ChooseMode;

                if(!clientInputStr.equals("")) {
                    long DeltaTime = System.currentTimeMillis() - Lastmillis;
                    ReceivedStr = clientInputStr.split(Delimiter);
                    if(!ReceivedStr[POS_MAGIC].equals(Magic)) {
                        throw new IOException("Bad Magic String:" + ReceivedStr[POS_MAGIC]);
                    }
                    ChooseMode = Integer.parseInt(ReceivedStr[POS_MODE]);
                    if(ReceivedStr[POS_MOUSESCROLL].equals("0")) {
                        switch (ChooseMode) {
                            case CTL_TOUCHSCREEN:
                                MC.MouseMove(TI.ComputeMouseMove(Float.parseFloat(ReceivedStr[POS_PARAMETERX]), Float.parseFloat(ReceivedStr[POS_PARAMETERY])), IC);
                                break;
                            case CTL_GRAVITY:
                                Point Poschange = GI.ComputeMouseMove(Float.parseFloat(ReceivedStr[POS_PARAMETERX]), Float.parseFloat(ReceivedStr[POS_PARAMETERY]));
                                Point temp_change = new Point();
                                temp_change.x = Poschange.x/2;
                                temp_change.y = Poschange.y/2;
                                for(int i = 0; i < 2;i++) {
                                    MC.MouseMove(temp_change, IC);
                                }
                                break;
                            case CTL_LINEARACCELERATION:
                                MC.MouseMove(AI.ComputeMouseMove(Float.parseFloat(ReceivedStr[POS_PARAMETERX]), Float.parseFloat(ReceivedStr[POS_PARAMETERY]), DeltaTime, IC), IC);
                                break;
                            case CTL_MAINTAIN:
                                break;
                        }
                    }
                    else {
                        MC.MouseScroll((int) Float.parseFloat(ReceivedStr[POS_PARAMETERY]));
                    }
                    if(ReceivedStr[POS_LEFTMOUSESCLICK].equals("1")) {
                        MC.MouseSingleClick(CTL_LEFTMOUSE);
                    }
                    if(ReceivedStr[POS_LEFTMOUSEDCLICK].equals("1")) {
                        MC.LeftMouseDoubleClick();
                    }
                    if(ReceivedStr[POS_RIGHTMOUSESCLICK].equals("1")) {
                        MC.MouseSingleClick(CTL_RIGHTMOUSE);
                    }
                    if(ReceivedStr[POS_MIDMOUSESCLICK].equals("1")) {
                        MC.MouseSingleClick(CTL_MIDMOUSE);
                    }
                    Lastmillis = System.currentTimeMillis();
                }
                input.close();
            } catch (Exception e) {
                System.out.println("Server Run Error: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("Server finally Error:" + e.getMessage());
                    }
                }
            }
        }
    }
}    