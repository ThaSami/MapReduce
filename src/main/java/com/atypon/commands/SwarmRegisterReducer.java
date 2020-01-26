package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.gui.Main;


public class SwarmRegisterReducer implements Command {

    private String ip;
    private ContainersDataTracker containersDataTracker;

    public SwarmRegisterReducer(String ip) {
        this.ip = ip;
        this.containersDataTracker = ContainersDataTracker.getInstance();
    }

    @Override
    public void execute() {
        containersDataTracker.addReducerAddress(this.ip.substring(1));
        containersDataTracker.incrementRunningContainers();
        containersDataTracker.incrementRunningReducers();
        Main.appendText(
                "Registered reducer "
                        + containersDataTracker.getCurrentReducersRunning()
                        + " / "
                        + containersDataTracker.getNumOfReducer()
                        + " "
                        + this.ip
                        + '\n');
    }
}
