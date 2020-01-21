package com.atypon.commands;

import java.lang.reflect.Constructor;
import java.net.Socket;

public class CommandsHandler {

  private CommandsHandler() {
  }

  public static void execute(String command, Socket socket) {

    try {
      Class<?> klass = Class.forName("com.atypon.commands." + command);
      Constructor<?> constructor;
      Command cmd = null;

      if ((constructor = klass.getDeclaredConstructor(Socket.class)) != null) {
        cmd = (Command) constructor.newInstance(socket);
      } else {
        constructor = klass.getDeclaredConstructor();
        cmd = (Command) constructor.newInstance();
      }
      cmd.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
