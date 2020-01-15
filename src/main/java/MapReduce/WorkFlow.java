package MapReduce;

import lombok.AllArgsConstructor;
import model.ContainersDataHandler;
import model.Test;
import utility.FilesUtil;
import utility.MapperImageUtil;
import utility.ReducerImageUtil;

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
        ReducerImageUtil.prepareReducerCode(reducingMethod, customImport);
        Test.outPuts.appendText("Reducer Code Prepared Successfully\n");
        MapperImageUtil.prepareMapperDockerFile();
        Test.outPuts.appendText("Mapper DockerFile Created Successfully\n");
        ReducerImageUtil.prepareReducerDockerFile();
        Test.outPuts.appendText("Reducer DockerFile created Successfully\n");
        MapperImageUtil.prepareDockerCompose(numOfMappers, numOfReducer);
        Test.outPuts.appendText("docker-compose created Successfully\n");

    }

}
