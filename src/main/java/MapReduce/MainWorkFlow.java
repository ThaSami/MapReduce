package MapReduce;

import lombok.AllArgsConstructor;
import model.ContainersDataHandler;
import utility.FilesUtil;
import utility.MapperImageUtil;
import utility.ReducerImageUtil;

import java.io.IOException;

import static model.Test.outPuts;

@AllArgsConstructor
public class MainWorkFlow implements WorkFlow {
  int numOfMappers;
  int numOfReducers;
  private String txtFilePath;
  private String mappingMethod;
  private String reducingMethod;
  private String customImport;

  public static boolean checkOS(String name) {
    String osName = System.getProperty("os.name").toLowerCase();

    return osName.startsWith(name.toLowerCase());
  }

  public void start() {

    if (FilesUtil.checkIfNotExist(txtFilePath)) {
      outPuts.appendText("TEXT FILE NOT FOUND, make sure it is readable");
      System.exit(1);
    }

    try {
      FilesUtil.copy(txtFilePath, "./Data/Data.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }

    ContainersDataHandler handler = ContainersDataHandler.getInstance();
    handler.setNumOfMappers(numOfMappers);
    handler.setNumOfReducer(numOfReducers);

    handler.setNumOfContainers(numOfMappers + numOfReducers);

    FilesUtil.splitter("./temp/Data/Data.txt", numOfMappers);

    outPuts.appendText("Splitted Files\n");

    MapperImageUtil.prepareMapperCode(mappingMethod, customImport);
    outPuts.appendText("Mapper Code Prepared Successfully\n");

    ReducerImageUtil.prepareReducerCode(reducingMethod, customImport);
    outPuts.appendText("Reducer Code Prepared Successfully\n");

    MapperImageUtil.prepareMapperDockerFile();
    outPuts.appendText("Mapper DockerFile Created Successfully\n");

    ReducerImageUtil.prepareReducerDockerFile();
    outPuts.appendText("Reducer DockerFile created Successfully\n");

    MapperImageUtil.prepareDockerCompose(numOfMappers, numOfReducers);
    outPuts.appendText("docker-compose created Successfully\n");

    // Todo run compose
    /*
       try {
         handler.waitForContainersToRun(30);
       } catch (Exception e) {
         e.printStackTrace();
         outPuts.appendText("time out");
         System.exit(1);
       }

    */
    // send Reducer Addressses
    handler.sendReducerAddresses();

    // send files
    handler.sendFileToMappers();

    // wait for mappers to finish working
    if (handler.checkIfMappersFinished()) {
      handler.startReducing();
    }
  }
}
