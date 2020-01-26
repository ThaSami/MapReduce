package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.gui.Main;


public class SwarmRegisterMapper implements Command {

    private String ip;
    private ContainersDataTracker containersDataTracker;

    public SwarmRegisterMapper(String ip) {
        this.ip = ip;
        this.containersDataTracker = ContainersDataTracker.getInstance();
    }

    @Override
    public void execute() {
        containersDataTracker.addMapperAddress(this.ip.substring(1));
        containersDataTracker.incrementRunningContainers();
        containersDataTracker.incrementRunningMappers();
        Main.appendText(
                "Registered mapper "
                        + containersDataTracker.getCurrentMappersRunning()
                        + " / "
                        + containersDataTracker.getNumOfMappers()
                        + " "
                        + this.ip
                        + '\n');
    }
}
