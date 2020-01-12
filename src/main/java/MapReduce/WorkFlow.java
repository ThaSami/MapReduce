package MapReduce;

import lombok.AllArgsConstructor;
import model.ContainersHandler;
import model.Test;
import utility.FilesUtil;
import utility.DockerImagesCreator;

@AllArgsConstructor
public class WorkFlow {
    private int numOfMappers;
    private int numOfReducer;
    private String txtFilePath;
    private String mappingMethod;
    private String reducingMethod;

    public void StartWorkFlow() {
        ContainersHandler.setNumOfContainers(numOfMappers + numOfReducer);
        FilesUtil.split(txtFilePath, numOfMappers);
        Test.outPuts.appendText("Splitted Files\n");
        DockerImagesCreator.prepareMapperCode(mappingMethod);
        Test.outPuts.appendText("Mapper Code Prepared Successfully\n");
        DockerImagesCreator.prepareReducerCode(reducingMethod);
        Test.outPuts.appendText("Reducer Code Prepared Successfully\n");
        DockerImagesCreator.prepareMapperDockerFile();
        Test.outPuts.appendText("Mapper DockerFile Created Successfully\n");
        DockerImagesCreator.prepareReducerDockerFile();
        Test.outPuts.appendText("Reducer DockerFile created Successfully\n");


    }

}
