package com.atypon.commands;

import com.atypon.docker.ContainersDataTracker;

import java.net.Socket;

import static com.atypon.gui.Main.outPuts;

public class MapperFinished implements Command {

    private ContainersDataTracker containersDataTracker;
    Socket socket;

    public MapperFinished(Socket socket) {
        this.socket = socket;
        this.containersDataTracker = ContainersDataTracker.getInstance();
    }

    @Override
    public void execute() {
        containersDataTracker.incrementFinishedMappers();
        outPuts.appendText(
                "Mapper Finished"
                        + containersDataTracker.getFinishedMappers()
                        + " / "
                        + containersDataTracker.getNumOfMappers()
                        + socket.getInetAddress()
                        + '\n');
    }
}
