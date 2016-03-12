package Server;


import javax.swing.*;


public class ServerInterface {

    private JPanel frame;
    private JLabel serverip;
    private JLabel connectstatus;
    private static String DeviceIp;
    private Server m_Server;

    public ServerInterface() {
        new Thread(new StartServer()).start();
    }

    class StartServer implements Runnable {
        @Override
        public void run() {
            m_Server = new Server();
            serverip.setText(m_Server.getServerIP());
            System.out.println("Server started...\n");
            Connection m_Connection = new Connection();
            DeviceIp = m_Connection.ReceiveBroadcast(m_Server.getServerIP());
            connectstatus.setText("Connected to " + DeviceIp);
            m_Server.init();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MMCServer");
        frame.setContentPane(new ServerInterface().frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
