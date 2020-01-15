package model;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import utility.Constants;
import utility.FilesUtil;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ContainersDataHandler {
  @Setter
  @Getter
  public static int numOfContainers = 20;
  public static int runningContainers = 0;
  public static int finishedMappers = 0;
  private static ContainersDataHandler containersDataHandler = null;
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
  public static void incrementRunningContainers() {
    runningContainers++;
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

  public void sendFileToReducers() {
    List<String> files = FilesUtil.getFilesInDirectory("./temp/Data");
    int i = 0;
    for (String file : files) {
      if (file.startsWith("Data.txt"))
        continue;
      FilesUtil.fileUploader(mappersAddresses.get(i++), file);
    }
  }

  public void waitForContainersToRun(int timeOutInSeconds) throws TimeoutException {
    long startTime = Calendar.getInstance().getTimeInMillis();

    while (runningContainers != numOfContainers) {
      if (Calendar.getInstance().getTimeInMillis() - startTime > timeOutInSeconds) {
        throw new TimeoutException("Container Running TimeOut");
      }
    }
  }

  @Synchronized
  public static void incrementFinishedMappers() {
    finishedMappers++;
  }
}
