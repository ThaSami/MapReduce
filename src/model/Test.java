package model;

import java.io.*;
import java.util.Scanner;

public class Test {



  private static void split(String filename,int num){

      ProcessBuilder processBuilder = new ProcessBuilder();
      File file = new File(filename);
      int size = (int)(file.length()/1024)/num+1;
      String command = "split -d -C " + size+"k" + " " + filename + " map";
      processBuilder.command("bash", "-c", command);

      try {

          Process process = processBuilder.start();

          BufferedReader reader =
                  new BufferedReader(new InputStreamReader(process.getInputStream()));

          String line;
          while ((line = reader.readLine()) != null) {
              System.out.println(line);
          }

          int exitCode = process.waitFor();
          System.out.println("\nExited with error code : " + exitCode);

      } catch (IOException|InterruptedException e) {
          e.printStackTrace();
      }

  }
  public static void main(final String... args) {

    System.out.println("Enter txt File Path: ");
      Scanner sc = new Scanner(System.in);
      String fileName = sc.next();
    System.out.println("Enter Number of mappers: ");
      int numOfMappers = sc.nextInt();
      split(fileName,numOfMappers);

   //   map(numOfMappers);

  }


}

