package com.atypon.nodes;

import com.atypon.nodes.senders.DataSender;
import com.atypon.utility.Constants;

import java.io.DataInputStream;
import java.net.Socket;

public class SwarmForwarderWorker {
  private Socket socket;
  private String hostAddress;

  public SwarmForwarderWorker(Socket socket, String hostAddress) {
    this.socket = socket;
    this.hostAddress = hostAddress;
  }

  public void handle() {
    try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
      String query;
      while ((query = in.readUTF()) != null) {
        if (query.startsWith("RegisterMapper")) {
          DataSender.sendString(
              hostAddress,
              Constants.MAIN_SERVER_PORT,
              "SwarmRegisterMapper," + socket.getInetAddress());
        } else {
          DataSender.sendString(
              hostAddress,
              Constants.MAIN_SERVER_PORT,
              "SwarmRegisterReducer," + socket.getInetAddress());
        }
      }
    } catch (Exception e) { // normal
      System.out.printf(
          "connection terminated Successfuly with Client %s:%d%n",
          socket.getInetAddress(), socket.getPort());
    }
  }
}
