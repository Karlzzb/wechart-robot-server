package com.karl.werobot;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 测试usb与pc通信 通过adb端口转发方式
 * 
 * @author chl
 * 
 */
public class PcClient {
    
    private Socket socket;

    public PcClient() {
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
            e.printStackTrace();

        }
        try {
            InetAddress serveraddr = null;
            serveraddr = InetAddress.getByName("127.0.0.1");
            System.out.println("TCP 1111" + "C: Connecting...");
            socket = new Socket(serveraddr, 12580);
            String str = "hi,chenhl";
            System.out.println("TCP 221122" + "C:RECEIVE");
            BufferedInputStream in = new BufferedInputStream(
                    socket.getInputStream());
            boolean flag = true;
            while (flag) {
                String strFormsocket = readFromSocket(in);
                if (strFormsocket == null || strFormsocket.isEmpty()) {
                    continue;
                }
                
                System.out.println("the data sent by server is:"
                        + strFormsocket);
            }

        } catch (UnknownHostException e1) {
            System.out.println("TCP 331133" + "ERROR:" + e1.toString());
        } catch (Exception e2) {
            System.out.println("TCP 441144" + "ERROR:" + e2.toString());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("TCP 5555" + "ERROR:" + e.toString());
            }
        }
    }

    /* 从InputStream流中读数据 */
    public static String readFromSocket(InputStream in) {
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
