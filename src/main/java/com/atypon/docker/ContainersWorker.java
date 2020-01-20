package com.atypon.docker;

import com.atypon.gui.Main;

import java.io.DataInputStream;
import java.net.Socket;

import static com.atypon.gui.Main.outPuts;

public class ContainersWorker {
    private Socket socket;
    private ContainersDataTracker containersDataTracker;

    public ContainersWorker(Socket socket) {
        this.socket = socket;
        this.containersDataTracker = containersDataTracker.getInstance();
    }

    //command pattern
    public void handle() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            String query;
            while ((query = in.readUTF()) != null) {
                switch (query) {
                    case "RegisterMapper":
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
                        break;
                    case "RegisterReducer":
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
                        break;
                    case "Finished":
                        containersDataTracker.incrementFinishedMappers();
                        outPuts.appendText(
                                "Mapper Finished"
                                        + containersDataTracker.getFinishedMappers()
                                        + " / "
                                        + containersDataTracker.getNumOfMappers()
                                        + socket.getInetAddress()
                                        + '\n');
                        break;
                }
            }
        } catch (Exception e) {
            System.out.printf(
                    "connection terminated Successfuly with Client %s:%d%n",
                    socket.getInetAddress(), socket.getPort());
        }
    }
}
