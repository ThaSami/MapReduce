package MapReduce;

import lombok.AllArgsConstructor;
import model.Test;
import util.FilesUtil;
import util.ImagesBuilder;

@AllArgsConstructor
public class WorkFlow {
    private int numOfMappers;
    private int numOfReducer;
    private String txtFilePath;
    private String mappingMethod;
    private String reducingMethod;

    public void StartWorkFlow() {
        FilesUtil.split(txtFilePath, numOfMappers);
        Test.outPuts.appendText("Splitted Files\n");
        ImagesBuilder.prepareMapperCode(mappingMethod);
        Test.outPuts.appendText("Mapper Code Prepared Successfully\n");
        ImagesBuilder.prepareReducerCode(reducingMethod);
        Test.outPuts.appendText("Reducer Code Prepared Successfully\n");
        ImagesBuilder.prepareMapperDockerFile();
        Test.outPuts.appendText("Mapper DockerFile Created Successfully\n");
        ImagesBuilder.prepareReducerDockerFile();
        Test.outPuts.appendText("Reducer DockerFile created Successfully\n");


    }

}
