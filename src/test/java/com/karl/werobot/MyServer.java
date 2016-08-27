package com.karl.werobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8888);
            while (true) {
                System.out.println("execute 1\n");
                Socket client = server.accept();
                System.out.println("execute 2\n");
                InputStream in = client.getInputStream();
                byte[] buffer = new byte[in.available()];  
                in.read(buffer);  
                String msg = new String(buffer);
                System.out.println(msg);
                
                Thread.sleep(5000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
