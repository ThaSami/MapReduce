package model;

import MapReduce.WorkFlow;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static utility.FilesUtil.checkOS;

public class Test extends Application {

    private TextField numOfMappers = new TextField();
    private TextField numOfReducers = new TextField();
    private TextArea customImport = new TextArea("//Insert your Imports other than \n //import java.io.*;\n");
    private TextArea mapperFunction = new TextArea("public static Map<?,?> mapping(String file){ \n\n\n\n\n}");
    private TextArea reducerFunction = new TextArea("public static Map<?,?> reduce(Map<?,List<?>> map){\n\n\n\n\n\n}");
    private TextField textFilePath = new TextField();

    public static TextArea outPuts = new TextArea();


    private Button btRegister = new Button("RUN IT BABY");

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


        pane.add(btRegister, 1, 6);
        GridPane.setHalignment(btRegister, HPos.RIGHT);

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

        btRegister.setOnAction(e -> {
            try {
                if (!checkOS("Linux")) {
                    outPuts.appendText("This programme has to be run UnderLinux");
                    System.exit(1);
                }
                btRegister.setDisable(true);
                int mappersNumber = Integer.parseInt(numOfMappers.getText());
                int reducersNumber = Integer.parseInt(numOfReducers.getText());
                String mappingMethod = mapperFunction.getText();
                String reduceMethod = reducerFunction.getText();
                String txtFilePath = textFilePath.getText();
                String customImports = customImport.getText();

                WorkFlow workFlow = new WorkFlow(mappersNumber, reducersNumber, txtFilePath, mappingMethod, reduceMethod, customImports);
                workFlow.StartWorkFlow();

            } catch (Exception ex) {
                System.err.println(ex);
            }
        });


    }




  public static void main(final String... args) {


      new Thread(() -> {
          try (ServerSocket server = new ServerSocket(7777)) {

              outPuts.appendText("Server started at " + new Date() + '\n');
              while (true) {
                  Socket client = server.accept();
                  ContainersWorker thread = new ContainersWorker(client);
                  thread.handle();
              }
          } catch (IOException ex) {
              ex.printStackTrace();
          }
      }).start();

      launch(args);
  }


}


