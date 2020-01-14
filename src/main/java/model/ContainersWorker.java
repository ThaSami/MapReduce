package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class ContainersWorker {
    private Socket socket;
    private ContainersDataHandler containersDataHandler;

    ContainersWorker(Socket socket) {
        this.socket = socket;
        this.containersDataHandler = ContainersDataHandler.getInstance();
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
                        containersDataHandler.addMapperAddress(socket.getInetAddress().toString());
                        Test.outPuts.appendText("Regsitered mapper " + socket.getInetAddress() + '\n');
                        containersDataHandler.incrementRunningContainers();
                        break;
                    case "RegisterReducer":
                        containersDataHandler.addReducerAddress(socket.getInetAddress().toString());
                        Test.outPuts.appendText("Regsitered reducer " + socket.getInetAddress() + '\n');
                        containersDataHandler.incrementRunningContainers();
                        break;
                    case "GetData":

                    default:
                        int id = Integer.parseInt(query);
                        String address = containersDataHandler.getReducersAddresses(id);
                        outputToClient.writeUTF(address);
                }
            }
        } catch (Exception e) {
            System.out.printf("connection terminated Successfuly with Client %s:%d%n", socket.getInetAddress(), socket.getPort());
        }
    }

}
