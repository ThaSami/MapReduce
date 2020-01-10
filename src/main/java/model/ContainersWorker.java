package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class ContainersWorker {
    private Socket socket;
    private ContainersHandler containersHandler;

    ContainersWorker(Socket socket) {
        this.socket = socket;
        this.containersHandler = ContainersHandler.getInstance();
    }


    public void handle() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream outputToClient = new DataOutputStream(
                     socket.getOutputStream());
        ) {
            String query;
            while ((query = in.readUTF()) != null) {
                switch (query) {
                    case "RegisterMapper":
                        containersHandler.addMapperAddress(socket.getInetAddress().toString());
                        System.out.printf("Regsitered mapper %s", socket.getInetAddress());
                        break;
                    case "RegisterReducer":
                        containersHandler.addReducerAddress(socket.getInetAddress().toString());
                        System.out.printf("Regsitered reducer %s", socket.getInetAddress());
                        break;
                    default:
                        int id = Integer.parseInt(query);
                        String address = containersHandler.getReducersAddresses(id);
                        outputToClient.writeUTF(address);
                }
            }
        } catch (Exception e) {
            System.out.printf("connection terminated Successfuly with Client %s:%d%n", socket.getInetAddress(), socket.getPort());
        }
    }

}
