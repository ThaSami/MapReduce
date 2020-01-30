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
import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.atypon.utility.Constants.*;

public class Main extends Application {

  private static TextArea outPuts = new TextArea();
  private TextField numOfMappers = new TextField();
  private TextField numOfReducers = new TextField();
  private TextArea customImport = new TextArea("//please list all your imports here.");
  private TextArea mapperFunction =
      new TextArea("public static Map<?,?> mapping(String file){ \n\n\n\n  }\n");
  private TextArea reducerFunction =
      new TextArea("public static Map<?,?> reduce(Map<Object, List<Object>> map){\n \n \n }");
  private TextField textFilePath = new TextField();
  private TextField whereToExec = new TextField();

  private Button btExecute = new Button("Execute");

  public static void main(final String... args) {

    new Thread(
            () -> {
              try (ServerSocket server = new ServerSocket(MAIN_SERVER_PORT)) {

                Main.appendText("management server started at " + new Date() + '\n');
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

    Platform.runLater(() -> outPuts.appendText(text));
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
    pane.add(new Label("Where to execute (swarm/locally?"), 0, 3);
    pane.add(whereToExec, 1, 3);
    pane.add(new Label("Custom Imports"), 0, 4);
    pane.add(customImport, 1, 4);
    pane.add(new Label("Mapper Function"), 0, 5);
    pane.add(mapperFunction, 1, 5);
    pane.add(new Label("Reducer Function"), 0, 6);
    pane.add(reducerFunction, 1, 6);

    pane.add(btExecute, 1, 7);
    GridPane.setHalignment(btExecute, HPos.RIGHT);

    pane.add(new Label("Outputs"), 0, 8);
    pane.add(outPuts, 1, 8);

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
            String _whereToExec = whereToExec.getText();
            Map<String, Object> params = new HashMap<>();
            params.put("numOfMappers", mappersNumber);
            params.put("numOfReducers", reducersNumber);
            params.put("mappingMethod", mappingMethod);
            params.put("reducingMethod", reduceMethod);
            params.put("txtFilePath", txtFilePath);
            params.put("customImports", customImports);
            params.put("ContainersTimeOut", 120);

            VelocityContext velocityContext = new VelocityContext();

            List<Integer> mappersPorts = new ArrayList<>();
            mappersPorts.add(MAIN_SERVER_PORT);
            mappersPorts.add(MAPPERS_TO_REDUCERS_PORT);
            mappersPorts.add(MAINSERVER_TO_MAPPERS_PORT);
            mappersPorts.add(MAPPERS_FILE_RECEIVER_PORT);

            List<Integer> reducersPorts = new ArrayList<>();
            reducersPorts.add(MAIN_SERVER_PORT);
            reducersPorts.add(MAINSERVER_TO_REDUCERS_PORT);
            reducersPorts.add(MAPPERS_TO_REDUCERS_PORT);

            velocityContext.put("mappersPorts", mappersPorts);
            velocityContext.put("numOfMappers", mappersNumber);
            velocityContext.put("reducersPorts", reducersPorts);
            velocityContext.put("numOfReducers", reducersNumber);
            velocityContext.put("hostAddress", MAIN_SERVER_IP);
            velocityContext.put("hostPort", MAIN_SERVER_PORT);

            params.put("compose-data", velocityContext);

            ContainersDataTracker tracker = ContainersDataTracker.getInstance();
            tracker.setNumOfMappers(new AtomicInteger(mappersNumber));
            tracker.setNumOfReducer(new AtomicInteger(reducersNumber));
            tracker.setNumOfContainers(new AtomicInteger((mappersNumber + reducersNumber)));
            tracker.setFinishedMappersLatch(mappersNumber);
            tracker.setWaitForContainersLatch(mappersNumber + reducersNumber);

            Context data = new Context(params);
            WorkflowManager workflowManager;

            if (_whereToExec.contains("sw") || _whereToExec.contains("Sw")) {
              workflowManager = new WorkflowManager("./src/main/resources/SwarmWorkFlow");
            } else {
              workflowManager = new WorkflowManager("./src/main/resources/LocalWorkFlow");
            }
            new Thread(
                    () -> {
                      for (Workflow workflow : workflowManager.getWorkflows()) {
                        workflow.start(data);
                      }
                    })
                .start();

          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
  }
}
