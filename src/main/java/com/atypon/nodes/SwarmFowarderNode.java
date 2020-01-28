package com.atypon.nodes;

import com.atypon.utility.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SwarmFowarderNode {
  public static void main(String[] args) {

    try (ServerSocket server = new ServerSocket(Constants.COLLECTOR_PORT)) {
      System.out.println("IP collection server started at " + new Date());
      while (true) {
        Socket client = server.accept();
        SwarmForwarderWorker thread = new SwarmForwarderWorker(client, args[0]);
        thread.handle();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
