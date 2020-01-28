package com.atypon.nodes.senders;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataSender {

  private DataSender() {
  }

  public static <T> void sendObject(String address, int port, T dataToSend) throws Exception {
    try (Socket sk = new Socket(address, port);
         ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
      objectOutput.writeObject(dataToSend);
    } catch (Exception e) {
      throw new Exception("couldn't send data");
    }
  }

  public static void sendString(String address, int port, String dataToSend) {
    try (Socket socket = new Socket(address, port);
         OutputStream outputStream = socket.getOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
      dataOutputStream.writeUTF(String.valueOf(dataToSend));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
