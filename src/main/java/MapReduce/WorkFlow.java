package MapReduce;

import lombok.AllArgsConstructor;
import util.ImagesBuilder;

@AllArgsConstructor
public class WorkFlow {
    private int numOfMappers;
    private int numOfReducer;
    private String txtFilePath;
    private String mappingMethod;
    private String reducingMethod;

    public void StartWorkFlow() {
        ImagesBuilder.prepareMapperCode(mappingMethod);
        ImagesBuilder.prepareReducerCode(reducingMethod);
        ImagesBuilder.prepareMapperDockerFile();
        ImagesBuilder.prepareReducerDockerFile();

    }

}
