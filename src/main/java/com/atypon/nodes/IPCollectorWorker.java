package com.atypon.nodes;

import com.atypon.commands.CommandsHandler;
import com.atypon.nodes.senders.DataSender;
import com.atypon.utility.Constants;

import java.io.DataInputStream;
import java.net.Socket;

public class IPCollectorWorker {
    private Socket socket;
    private String address;

    public IPCollectorWorker(Socket socket, String address) {
        this.socket = socket;
        this.address = address;
    }

    public void handle() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            String query;
            while ((query = in.readUTF()) != null) {
                if (query.startsWith("RegisterMapper")) {
                    DataSender.sendString(address, Constants.MAIN_SERVER_PORT, "SwarmRegisterMapper," + socket.getInetAddress());
                } else {
                    DataSender.sendString(address, Constants.MAIN_SERVER_PORT, "SwarmRegisterReducer," + socket.getInetAddress());
                }
            }
        } catch (Exception e) {
            System.out.printf(
                    "connection terminated Successfuly with Client %s:%d%n",
                    socket.getInetAddress(), socket.getPort());
        }


    }
}
