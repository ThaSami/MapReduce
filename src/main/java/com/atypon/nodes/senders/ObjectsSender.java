package com.atypon.nodes.senders;

import com.atypon.utility.Constants;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ObjectsSender implements Sender {


    @Override
    public <T> void send(String address, int port, T dataToSend) {
        try (Socket sk = new Socket(address, port);
             ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
            objectOutput.writeObject(dataToSend);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
