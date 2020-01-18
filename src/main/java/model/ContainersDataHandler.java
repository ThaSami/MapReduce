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
  @Getter
  private int currentMappersRunning = 0;
  @Getter
  private int currentReducersRunning = 0;
  @Getter
  private int finishedMappers = 0;

  private List<String> mappersAddresses;
  private ArrayList<String> reducersAddresses; // since List is not Serializable

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
  public void incrementFinishedMappers() {
    this.finishedMappers++;
  }

  @Synchronized
  public void incrementRunningMappers() {
    this.currentMappersRunning++;
  }

  @Synchronized
  public void incrementRunningReducers() {
    this.currentReducersRunning++;
  }

  @Synchronized
  public void addReducerAddress(String address) {
    reducersAddresses.add(address);
  }

  public void sendReducerAddresses() throws InterruptedException {
    for (String address : reducersAddresses) {
      Thread t =
              new Thread(
                      () -> {
                        try (Socket sk =
                                     new Socket(address, Constants.MAPPERS_REDUCERADDRESS_RECEIVER_PORT)) {
                          ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream());
                          objectOutput.writeObject(this.reducersAddresses);
                        } catch (UnknownHostException e) {
                          e.printStackTrace();
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
              });
      t.start();
      t.join();
    }
  }

  public boolean checkIfMappersFinished() {
    return this.finishedMappers == this.numOfMappers;
  }

  public void sendFileToMappers(String rootDirectory) {
    List<String> files = FilesUtil.getFilesInDirectory(rootDirectory);
    int i = 0;
    for (String file : files) {
      FilesUtil.fileUploader(mappersAddresses.get(i), rootDirectory, file);
      System.out.println(mappersAddresses.get(i));
      i++;
    }
  }

  public void waitForContainersToRun(int timeOutInSeconds) throws TimeoutException {
    long startTime = Calendar.getInstance().getTimeInMillis();
    int fromMilliToSeconds = 1000;
    long currentTime;
    while (runningContainers != numOfContainers) {
      currentTime = Calendar.getInstance().getTimeInMillis();
      if (currentTime - startTime > (timeOutInSeconds * fromMilliToSeconds)) {
        throw new TimeoutException("Container Running TimeOut");
      }
    }
  }

  public void waitForMappersToFinish() {
    while (this.getNumOfMappers() != this.getFinishedMappers()) {
    }
  }

  public void startReducing() {
    for (String reducerAddress : reducersAddresses) {
      try (Socket socket = new Socket(reducerAddress, Constants.REDUCER_START_RECEIVER_PORT);
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
