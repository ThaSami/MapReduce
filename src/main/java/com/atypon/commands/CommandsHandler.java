package com.atypon.commands;

import java.lang.reflect.Constructor;
import java.net.Socket;

public class CommandsHandler {

  private CommandsHandler() {
  }

  public static void execute(String command, Socket socket) {
    Constructor<?> constructor;
    Command cmd = null;

    if (command.contains("Swarm")) {
      String[] commands = command.split(",");
      try {
        Class<?> klass = Class.forName("com.atypon.commands." + commands[0]);
        constructor = klass.getDeclaredConstructor(String.class);
        cmd = (Command) constructor.newInstance(commands[1]);
        cmd.execute();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        Class<?> klass = Class.forName("com.atypon.commands." + command);

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
}
