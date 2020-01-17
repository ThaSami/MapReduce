package model;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import utility.Constants;
import utility.FilesUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ContainersDataHandler {
  private static ContainersDataHandler containersDataHandler = null;
  @Setter
  @Getter
  private int numOfContainers = 20;
  @Setter
  @Getter
  private int numOfMappers = 0;
  @Setter
  @Getter
  private int numOfReducer = 0;
  @Setter
  @Getter
  private int runningContainers = 0;
  private int finishedMappers = 0;
  private List<String> mappersAddresses;
  private ArrayList<String> reducersAddresses;

  private ContainersDataHandler() {
    this.mappersAddresses = new ArrayList<>();
    this.reducersAddresses = new ArrayList<>();
  }

  public static ContainersDataHandler getInstance() {
    if (containersDataHandler == null) containersDataHandler = new ContainersDataHandler();

    return containersDataHandler;
  }

  @Synchronized
  public void incrementRunningContainers() {
    this.runningContainers++;
  }

  @Synchronized
  public void addMapperAddress(String address) {
    mappersAddresses.add(address);
  }

  @Synchronized
  public void addReducerAddress(String address) {
    reducersAddresses.add(address);
  }

  public String getReducersAddresses(int id) {
    return reducersAddresses.get(id);
  }

  public void sendReducerAddresses() {
    for (String address : reducersAddresses) {
      new Thread(
              () -> {
                try (Socket sk =
                             new Socket(address, Constants.MAPPERS_REDUCER_ADDRESS_RECEIVER_PORT)) {
                  ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream());
                  objectOutput.writeObject(this.reducersAddresses);
                } catch (UnknownHostException e) {
                  e.printStackTrace();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              })
              .start();
    }
  }

  public boolean checkIfMappersFinished() {
    return this.finishedMappers == numOfMappers;
  }

  public void sendFileToMappers() {
    List<String> files = FilesUtil.getFilesInDirectory("./temp/Data");
    int i = 0;
    for (String file : files) {
      if (file.startsWith("Data.txt")) continue;
      FilesUtil.fileUploader(mappersAddresses.get(i++), file);
    }
  }

  public void waitForContainersToRun(int timeOutInSeconds) throws TimeoutException {
    long startTime = Calendar.getInstance().getTimeInMillis();
    int fromMilliToSeconds = 1000;
    while (runningContainers != numOfContainers) {
      long currentTime = Calendar.getInstance().getTimeInMillis();
      if (currentTime - startTime > (timeOutInSeconds * fromMilliToSeconds)) {
        throw new TimeoutException("Container Running TimeOut");
      }
    }
  }

  @Synchronized
  public void incrementFinishedMappers() {
    this.finishedMappers++;
  }

  public void startReducing() {
    for (String reducerAddress : reducersAddresses) {
      try (Socket socket = new Socket(reducerAddress, Constants.MAIN_SERVER_PORT);
           OutputStream outputStream = socket.getOutputStream();
           DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
        dataOutputStream.writeUTF("start");
        dataOutputStream.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
