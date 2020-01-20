package com.atypon.nodes.receivers;

import com.atypon.utility.Constants;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ArrayListReceiver implements Receiver {

    @Getter
    private ArrayList<Object> dataReceived;

    public ArrayListReceiver() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T start(int port) {
        System.out.println("Waiting for ArrayList To be Received");
        try (ServerSocket myServerSocket =
                     new ServerSocket(port);
             Socket skt = myServerSocket.accept();
             ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream())) {
            Object object = objectInput.readObject();
            dataReceived = (ArrayList<Object>) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) dataReceived;
    }
}
