package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.gui.Main;

import java.net.Socket;

public class Finished implements Command {

  Socket socket;
  private ContainersDataTracker containersDataTracker;

  public Finished(Socket socket) {
    this.socket = socket;
    this.containersDataTracker = ContainersDataTracker.getInstance();
  }

  @Override
  public void execute() {
    containersDataTracker.incrementFinishedMappers();
    Main.appendText(
            "Mapper Finished"
                    + containersDataTracker.getFinishedMappers()
                    + " / "
                    + containersDataTracker.getNumOfMappers()
                    + socket.getInetAddress()
                    + '\n');
  }
}
