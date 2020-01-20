package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.gui.Main;

import java.net.Socket;

public class RegisterMapper implements Command {

    private ContainersDataTracker containersDataTracker;
    Socket socket;

    public RegisterMapper(Socket socket) {
        this.socket = socket;
        this.containersDataTracker = ContainersDataTracker.getInstance();
    }

    @Override
    public void execute() {
        containersDataTracker.addMapperAddress(socket.getInetAddress().toString().substring(1));
        containersDataTracker.incrementRunningContainers();
        containersDataTracker.incrementRunningMappers();
        Main.appendText(
                "Registered mapper "
                        + containersDataTracker.getCurrentMappersRunning()
                        + " / "
                        + containersDataTracker.getNumOfMappers()
                        + " "
                        + socket.getInetAddress()
                        + '\n');
    }
}
