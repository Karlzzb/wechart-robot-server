

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 * 测试usb与pc通信 通过adb端口转发方式
 * 
 * @author chl
 * 
 */
public class PcClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PcClient.class);
    
    private static final String socket_host = "127.0.0.1";
    private static final int socket_port = 12580;
    
    private Socket socket;
    private App app;

    public PcClient(App app) {
        try {
            // adb 指令
            Runtime.getRuntime().exec(
                    "adb shell am broadcast -a NotifyServiceStop");
            Thread.sleep(3000);
            Runtime.getRuntime().exec("adb forward tcp:12580 tcp:10086"); // 端口转换
            Thread.sleep(3000);
            Runtime.getRuntime().exec(
                    "adb shell am broadcast -a NotifyServiceStart");
            Thread.sleep(3000);
        } catch (Exception e) {
            LOGGER.error("PcClient initial failed!", e);
        }
        
        this.app = app;
        Thread OpenConnection = new Thread(new OpenConnection(socket_host, socket_port));
        OpenConnection.start();
        
    }
    
    
    private class OpenConnection  extends Thread {
        private String host;
        private int port;
        
        public OpenConnection(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                InetAddress serveraddr = null;
                serveraddr = InetAddress.getByName(host);
                LOGGER.debug("host【{}】 port【{}】 connecting ", host, port);
                socket = new Socket(serveraddr, port);
                LOGGER.debug("host【{}】 port【{}】 connecting ", host, port);
                BufferedInputStream in = new BufferedInputStream(
                        socket.getInputStream());
                boolean flag = true;
                while (flag) {
                    String strFormsocket = readFromSocket(in);
                    if (strFormsocket == null || strFormsocket.isEmpty()) {
                        continue;
                    }
                    LOGGER.debug("host【{}】 port【{}】 receive data: {}", host, port, strFormsocket);
                    app.webwxsendmsg(strFormsocket);
                }

            } catch (UnknownHostException e1) {
                LOGGER.error("PcClient failed!", e1);
            } catch (Exception e2) {
                LOGGER.error("PcClient failed!", e2);
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("PcClient close connection failed!", e);
                }
            }
        }
        
    }

    /* 从InputStream流中读数据 */
    public String readFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 4000;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer);
            if (numReadedBytes < 0) {
                return null;
            }
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

            tempbuffer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}
