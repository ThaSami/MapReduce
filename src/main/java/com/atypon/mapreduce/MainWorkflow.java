package com.atypon.mapreduce;

import lombok.AllArgsConstructor;
import com.atypon.docker.ContainersHandler;
import com.atypon.gui.Main;
import com.atypon.utility.FilesUtil;
import com.atypon.utility.MapperImageUtil;
import com.atypon.utility.ReducerImageUtil;

import java.io.IOException;

@AllArgsConstructor
public class MainWorkflow {
  int numOfMappers;
  int numOfReducers;
  private String txtFilePath;
  private String mappingMethod;
  private String reducingMethod;
  private String customImport;

  public void start() throws InterruptedException {

    if (FilesUtil.checkIfNotExist(txtFilePath)) {
      Main.appendText("TEXT FILE NOT FOUND, make sure it is readable");
      System.exit(1);
    }

    try {
      FilesUtil.copy(txtFilePath, "./Data/Data.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }

    ContainersHandler handler = ContainersHandler.getInstance();
    // handler.setNumOfMappers(numOfMappers);
    // handler.setNumOfReducer(numOfReducers);

    // handler.setNumOfContainers(numOfMappers + numOfReducers);

    FilesUtil.splitter("./temp/Data/Data.txt", numOfMappers);

    Main.appendText("Splitted Files\n");

    MapperImageUtil.prepareMapperCode(mappingMethod, customImport);
    Main.appendText("Mapper Code Prepared Successfully\n");

    ReducerImageUtil.prepareReducerCode(reducingMethod, customImport);
    Main.appendText("Reducer Code Prepared Successfully\n");

    MapperImageUtil.prepareMapperDockerFile();
    Main.appendText("Mapper DockerFile Created Successfully\n");

    ReducerImageUtil.prepareReducerDockerFile();
    Main.appendText("Reducer DockerFile created Successfully\n");

    MapperImageUtil.prepareDockerCompose(numOfMappers, numOfReducers);
    Main.appendText("docker-compose created Successfully\n");

    // Todo run compose

    try {
      Main.appendText("Waiting For Containers to Run\n");
      //  handler.waitForContainersToRun(120);
    } catch (Exception e) {
      e.printStackTrace();
      Main.appendText("time out");
    }

    Main.appendText("Sending Reducers Addresses To Mappers\n");
    try {
      handler.sendReducerAddressesToMappers();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // send files
    Main.appendText("Sending Files To Mappers\n");
    handler.sendFilesToMappers("./temp/Data/");

    // wait for mappers to finish working
    Main.appendText("Waiting for mappers to finish working\n");
    //handler.waitForMappersToFinish();

    Main.appendText("Mappers Finished\n");

    Main.appendText("Starting Reduce phase\n");
    //handler.sendStartFlagToReducers();

    Main.appendText("Collector Initialized\n");
    //   Collector.startCollecting(numOfReducers);

    Collector.printCollectedDataToFile();
    Main.appendText("Data Print\n");
  }
}
