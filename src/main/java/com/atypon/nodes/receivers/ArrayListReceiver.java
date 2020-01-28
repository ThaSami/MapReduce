package com.atypon.nodes.receivers;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ArrayListReceiver implements Receiver {

  private ArrayList<Object> dataReceived;

  public ArrayListReceiver() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public ArrayList<Object> start(int port) {
    try (ServerSocket myServerSocket = new ServerSocket(port);
         Socket skt = myServerSocket.accept();
         ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream())) {
      Object object = objectInput.readObject();
      dataReceived = (ArrayList<Object>) object;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dataReceived;
  }
}
