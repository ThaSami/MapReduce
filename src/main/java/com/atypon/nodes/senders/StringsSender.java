package com.atypon.nodes.senders;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StringsSender implements Sender {

    private StringsSender() {
    }

    @Override
    public <T> void send(String address, int port, T dataToSend) {
        try (Socket socket = new Socket(address, port);
             OutputStream outputStream = socket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.writeUTF(String.valueOf(dataToSend));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
