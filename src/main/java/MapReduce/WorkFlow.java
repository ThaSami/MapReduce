package MapReduce;

import lombok.AllArgsConstructor;
import model.ContainersDataHandler;
import model.Test;
import utility.FilesUtil;
import utility.MapperImageUtil;

@AllArgsConstructor
public class WorkFlow {
    private int numOfMappers;
    private int numOfReducer;
    private String txtFilePath;
    private String mappingMethod;
    private String reducingMethod;
    private String customImport;

    public void StartWorkFlow() {
        ContainersDataHandler.setNumOfContainers(numOfMappers + numOfReducer);
        FilesUtil.splitter(txtFilePath, numOfMappers);
        Test.outPuts.appendText("Splitted Files\n");
        MapperImageUtil.prepareMapperCode(mappingMethod, customImport);
        Test.outPuts.appendText("Mapper Code Prepared Successfully\n");
        MapperImageUtil.prepareReducerCode(reducingMethod, customImport);
        Test.outPuts.appendText("Reducer Code Prepared Successfully\n");
        MapperImageUtil.prepareMapperDockerFile();
        Test.outPuts.appendText("Mapper DockerFile Created Successfully\n");
        MapperImageUtil.prepareReducerDockerFile();
        Test.outPuts.appendText("Reducer DockerFile created Successfully\n");
        MapperImageUtil.prepareDockerCompose(numOfMappers, numOfReducer);
        Test.outPuts.appendText("docker-compose created Successfully\n");

    }

}
