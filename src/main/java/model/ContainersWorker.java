package model;

import java.io.DataInputStream;
import java.net.Socket;

import static model.Main.outPuts;

public class ContainersWorker {
  private Socket socket;
  private ContainersDataHandler containersDataHandler;

  ContainersWorker(Socket socket) {
    this.socket = socket;
    this.containersDataHandler = ContainersDataHandler.getInstance();
  }

  public void handle() {
    try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
      String query;
      while ((query = in.readUTF()) != null) {
        switch (query) {
          case "RegisterMapper":
            containersDataHandler.addMapperAddress(socket.getInetAddress().toString().substring(1));
            containersDataHandler.incrementRunningContainers();
            containersDataHandler.incrementRunningMappers();
            Main.appendText("Registered mapper " + containersDataHandler.getCurrentMappersRunning() + " / " + containersDataHandler.getNumOfMappers() + " " + socket.getInetAddress() + '\n');
            break;
          case "RegisterReducer":
            containersDataHandler.addReducerAddress(socket.getInetAddress().toString().substring(1));
            containersDataHandler.incrementRunningContainers();
            containersDataHandler.incrementRunningReducers();
            Main.appendText("Registered reducer " + containersDataHandler.getCurrentReducersRunning() + " / " + containersDataHandler.getNumOfReducer() + " " + socket.getInetAddress() + '\n');
            break;
          case "Finished":
            containersDataHandler.incrementFinishedMappers();
            outPuts.appendText("Mapper Finished" + containersDataHandler.getFinishedMappers() + " / " + containersDataHandler.getNumOfMappers() + socket.getInetAddress() + '\n');
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
