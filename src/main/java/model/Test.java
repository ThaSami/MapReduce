package model;

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
import java.util.Scanner;

import static util.FilesUtil.checkOS;
import static util.FilesUtil.split;

public class Test extends Application {

    private TextField numOfMappers = new TextField();
    private TextField numOfReducers = new TextField();
    private TextArea mapperFunction = new TextArea();
    private TextArea ReducerFunction = new TextArea("public static Map<T,T> reduce(Map<T,T> map){" +
            "" +
            "" +
            "" +
            "" +
            "" +
            "" +
            "}");
    private TextField TextFilePath = new TextField("public static Map<T,T> mapping(String file){" +
            "" +
            "" +
            "" +
            "" +
            "" +
            "" +
            "}");

    private TextArea outPuts = new TextArea();


    // Button for sending a student to the server
    private Button btRegister = new Button("RUN IT BABY");

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        GridPane pane = new GridPane();
        pane.add(new Label("Number Of Mappers"), 0, 0);
        pane.add(numOfMappers, 1, 0);
        pane.add(new Label("Number Of Reducers"), 0, 1);
        pane.add(numOfReducers, 1, 1);
        pane.add(new Label("Text File Path"), 0, 2);
        pane.add(TextFilePath, 1, 2);
        pane.add(new Label("Reducer Function"), 0, 5);
        pane.add(ReducerFunction, 1, 5);
        pane.add(new Label("Mapper Function"), 0, 3);
        pane.add(mapperFunction, 1, 3);

        pane.add(btRegister, 1, 6);
        GridPane.setHalignment(btRegister, HPos.RIGHT);

        pane.add(new Label("OutPuts"), 0, 7);
        pane.add(outPuts, 1, 7);

        pane.setAlignment(Pos.TOP_CENTER);
        numOfMappers.setPrefColumnCount(15);
        numOfReducers.setPrefColumnCount(30);
        TextFilePath.setPrefColumnCount(10);
        mapperFunction.setPrefColumnCount(2);
        ReducerFunction.setPrefColumnCount(3);

        // Create a scene and place it in the stage
        Scene scene = new Scene(pane, 700, 700);
        primaryStage.setTitle("Map Reduce"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        btRegister.setOnAction(e -> {
            try {
                if (!checkOS("Linux")) {
                    System.out.println("This programme has to be run UnderLinux");
                    System.exit(1);
                }

            } catch (Exception ex) {
                System.err.println(ex);
            }
        });


    }




  public static void main(final String... args) {


/*
    System.out.println("Enter txt File Path: ");
      Scanner sc = new Scanner(System.in);
      String fileName = sc.next();
    System.out.println("Enter Number of mappers: ");
      int numOfMappers = sc.nextInt();
      split(fileName,numOfMappers);
     System.out.println("Enter Number of reducers: ");
      int numOfReducers = sc.nextInt();

*/

      new Thread(() -> {
          try (ServerSocket server = new ServerSocket(7777)) {

              System.out.println("Server started at " + new Date());
              System.out.println();

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


