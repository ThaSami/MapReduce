package com.atypon.gui;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.docker.ContainersWorker;
import com.atypon.workflow.Context;
import com.atypon.workflow.Workflow;
import com.atypon.workflow.WorkflowManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.atypon.utility.Constants.MAIN_SERVER_PORT;

public class Main extends Application {

    public static TextArea outPuts = new TextArea();
    private TextField numOfMappers = new TextField();
    private TextField numOfReducers = new TextField();
    private TextArea customImport =
            new TextArea(
                    "\n"
                            + "import java.util.TreeMap;\n"
                            + "import java.util.regex.Matcher;\n"
                            + "import java.util.regex.Pattern;\n"
                            + "import java.util.Map;\n"
                            + "import java.util.List;");
    private TextArea mapperFunction =
            new TextArea(
                    "\n"
                            + "public static Map<?,?> mapping(String file){ \n"
                            + "       Pattern pattern = Pattern.compile(\"[a-zA-Z]+\");\n"
                            + "        TreeMap<String,Integer> wordCount = new TreeMap<String,Integer>();\n"
                            + "\n"
                            + "        try (\n"
                            + "                BufferedReader src = new BufferedReader(new FileReader(file));\n"
                            + "\n"
                            + "        ){\n"
                            + "\n"
                            + "            Matcher matcher ;\n"
                            + "            String str = src.readLine();\n"
                            + "            while(str!=null){\n"
                            + "                if(!str.equals(\"\")){\n"
                            + "                    matcher = pattern.matcher(str);\n"
                            + "                    while(matcher.find()){\n"
                            + "                        String word = matcher.group();\n"
                            + "                        if(!wordCount.containsKey(word))\n"
                            + "                            wordCount.put(word,1);\n"
                            + "                        else\n"
                            + "                            wordCount.put(word,wordCount.get(word)+1);\n"
                            + "                    }\n"
                            + "                }\n"
                            + "                str = src.readLine();\n"
                            + "            }\n"
                            + "        }\n"
                            + "        catch(IOException e){\n"
                            + "            e.printStackTrace();\n"
                            + "        }\n"
                            + "return wordCount;\n"
                            + "}\n");
    private TextArea reducerFunction =
            new TextArea(
                    "\n"
                            + "\n"
                            + "public static Map<?,?> reduce(Map<Object, List<Object>> map){\n"
                            + "        Map<Object, Object> secondMap = new TreeMap<>();\n"
                            + "\n"
                            + "        map.forEach((k, v) -> {\n"
                            + "            int sum = v.stream().mapToInt(entry ->  (int)entry).sum();\n"
                            + "            secondMap.put(k, sum);\n"
                            + "        });\n"
                            + "        return secondMap;\n"
                            + "\n"
                            + "}\n"
                            + "\n");
    private TextField textFilePath = new TextField();
    private Button btExecute = new Button("Execute");

    public static void main(final String... args) {

        new Thread(
                () -> {
                    try (ServerSocket server = new ServerSocket(MAIN_SERVER_PORT)) {

                        outPuts.appendText("Server started at " + new Date() + '\n');
                        while (true) {
                            Socket client = server.accept();
                            ContainersWorker thread = new ContainersWorker(client);
                            thread.handle();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
                .start();

        launch(args);
    }

    public static void appendText(String text) {
        Platform.runLater(
                () -> {
                    outPuts.appendText(text);
                });
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane pane = new GridPane();
        pane.add(new Label("Number Of Mappers"), 0, 0);
        pane.add(numOfMappers, 1, 0);
        pane.add(new Label("Number Of Reducers"), 0, 1);
        pane.add(numOfReducers, 1, 1);
        pane.add(new Label("Text File Path"), 0, 2);
        pane.add(textFilePath, 1, 2);
        pane.add(new Label("Custom Imports"), 0, 3);
        pane.add(customImport, 1, 3);
        pane.add(new Label("Mapper Function"), 0, 4);
        pane.add(mapperFunction, 1, 4);
        pane.add(new Label("Reducer Function"), 0, 5);
        pane.add(reducerFunction, 1, 5);

        pane.add(btExecute, 1, 6);
        GridPane.setHalignment(btExecute, HPos.RIGHT);

        pane.add(new Label("OutPuts"), 0, 7);
        pane.add(outPuts, 1, 7);

        pane.setAlignment(Pos.TOP_CENTER);
        numOfMappers.setPrefColumnCount(15);
        numOfReducers.setPrefColumnCount(30);
        textFilePath.setPrefColumnCount(10);
        mapperFunction.setPrefColumnCount(2);
        reducerFunction.setPrefColumnCount(3);
        customImport.setPrefColumnCount(3);

        Scene scene = new Scene(pane, 800, 800);
        primaryStage.setTitle("Map Reduce");
        primaryStage.setScene(scene);
        primaryStage.show();

        btExecute.setOnAction(
                e -> {
                    try {

                        btExecute.setDisable(true);
                        int mappersNumber = Integer.parseInt(numOfMappers.getText());
                        int reducersNumber = Integer.parseInt(numOfReducers.getText());
                        String mappingMethod = mapperFunction.getText();
                        String reduceMethod = reducerFunction.getText();
                        String txtFilePath = textFilePath.getText();
                        String customImports = customImport.getText();

                        Map<String, Object> params = new HashMap<>();
                        params.put("numOfMappers", mappersNumber);
                        params.put("numOfReducers", reducersNumber);
                        params.put("mappingMethod", mappingMethod);
                        params.put("reducingMethod", reduceMethod);
                        params.put("txtFilePath", txtFilePath);
                        params.put("customImports", customImports);
                        params.put("ContainersTimeOut", 120);

                        ContainersDataTracker tracker = ContainersDataTracker.getInstance();
                        tracker.setNumOfMappers(new AtomicInteger(mappersNumber));
                        tracker.setNumOfReducer(new AtomicInteger(reducersNumber));
                        tracker.setNumOfContainers(new AtomicInteger((mappersNumber + reducersNumber)));
                        tracker.setFinishedMappersLatch(mappersNumber);
                        tracker.setWaitForContainersLatch(mappersNumber + reducersNumber);

                        Context data = new Context(params);
                        WorkflowManager workflowManager = new WorkflowManager("./src/main/resources/");

                        new Thread(() -> {
                            for (Workflow workflow : workflowManager.getWorkflows()) {
                                workflow.start(data);
                            }
                        }).start();


/*
                        MainWorkFlow mainWorkFlow =
                                new MainWorkFlow(
                                        mappersNumber,
                                        reducersNumber,
                                        txtFilePath,
                                        mappingMethod,
                                        reduceMethod,
                                        customImports);
                        new Thread(
                                () -> {
                                    mainWorkFlow.start();
                                })
                                .start();
*/
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
  }
}
