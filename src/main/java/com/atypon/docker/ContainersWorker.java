package com.atypon.docker;

import com.atypon.commands.CommandsHandler;
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
                CommandsHandler.execute(query, socket);
            }
        } catch (Exception e) {
            System.out.printf(
                    "connection terminated Successfuly with Client %s:%d%n",
                    socket.getInetAddress(), socket.getPort());
        }
    }
}
