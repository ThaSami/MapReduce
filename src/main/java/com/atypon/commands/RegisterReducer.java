package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.gui.Main;

import java.net.Socket;

public class RegisterReducer implements Command {

    private ContainersDataTracker containersDataTracker;
    Socket socket;

    public RegisterReducer(Socket socket) {
        this.socket = socket;
        this.containersDataTracker = ContainersDataTracker.getInstance();
    }

    @Override
    public void execute() {
        containersDataTracker.addReducerAddress(
                socket.getInetAddress().toString().substring(1));
        containersDataTracker.incrementRunningContainers();
        containersDataTracker.incrementRunningReducers();
        Main.appendText(
                "Registered reducer "
                        + containersDataTracker.getCurrentReducersRunning()
                        + " / "
                        + containersDataTracker.getNumOfReducer()
                        + " "
                        + socket.getInetAddress()
                        + '\n');
    }
}
