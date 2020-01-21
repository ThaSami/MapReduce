package com.atypon.nodes.receivers;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver implements Receiver {

    private String fileName;

    public FileReceiver(String fileName) {
        this.fileName = fileName;
    }


    @Override
    public String start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept();
             InputStream in = socket.getInputStream();
             OutputStream out = new FileOutputStream(fileName)) {

            byte[] bytes = new byte[8192];

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "./" + fileName;
    }

}
