package com.atypon.docker;

import com.atypon.utility.Constants;
import com.atypon.utility.FilesUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ContainersHandler {

  private static ContainersHandler containersDataHandler = null;
  private ContainersDataTracker containersDataTracker;

  private ContainersHandler() {
    containersDataTracker = ContainersDataTracker.getInstance();
  }

  public static ContainersHandler getInstance() {
    if (containersDataHandler == null) {
      containersDataHandler = new ContainersHandler();
    }

    return containersDataHandler;
  }

  public void sendReducerAddressesToMappers() throws InterruptedException, IOException {
    Thread.sleep(3000);
    for (String address : containersDataTracker.getMappersAddresses()) {
      try (Socket sk = new Socket(address, Constants.MAINSERVER_TO_MAPPERS_PORT)) {
        ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream());
        objectOutput.writeObject(containersDataTracker.getReducersAddresses());
      } catch (Exception e) {
        throw e;
      }
    }
  }

  public void sendMappersAddressesToReducers() throws InterruptedException, IOException {
    Thread.sleep(3000);
    for (String address : containersDataTracker.getReducersAddresses()) {
      try (Socket sk = new Socket(address, Constants.MAINSERVER_TO_REDUCERS_PORT)) {
        ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream());
        objectOutput.writeObject(containersDataTracker.getMappersAddresses());
      } catch (Exception e) {
        throw e;
      }
    }
  }

  public void sendFilesToMappers(String rootDirectory) throws IOException, InterruptedException {
    Thread.sleep(3000);
    List<String> filesAbsPath = FilesUtil.getFilesAbsPathInDirectory(rootDirectory);
    int i = 0;
    for (String fileAbsPath : filesAbsPath) {
      System.out.println("Sending : " + fileAbsPath);
      FilesUtil.fileUploader(
          containersDataTracker.getMappersAddresses().get(i),
          fileAbsPath,
          Constants.MAPPERS_FILE_RECEIVER_PORT);
      System.out.println(containersDataTracker.getMappersAddresses().get(i));
      i++;
    }
  }
}
