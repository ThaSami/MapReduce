package com.atypon.nodes;

import com.atypon.nodes.receivers.ArrayListReceiver;
import com.atypon.nodes.receivers.FileReceiver;
import com.atypon.nodes.receivers.Receiver;
import com.atypon.nodes.senders.DataSender;
import com.atypon.nodes.shufflers.HashShuffler;
import com.atypon.nodes.shufflers.Shuffler;
import com.atypon.utility.Constants;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import static com.atypon.utility.Constants.MAIN_SERVER_IP;

public class MapperNode {

  static List<String> reducersAddresses;
  static List<Map<Object, Object>> shuffleResult;

  static Map<?, ?> startMapping(String path) {
    Map<?, ?> result = null;
    try {
      File root = new File("./");
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
      Class<?> cls = Class.forName("MapperUtil", false, classLoader);
      Method method = cls.getDeclaredMethod("mapping", String.class);
      result = (Map<?, ?>) method.invoke(cls, path);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  static void sendShuffleResultToReducers(List<Map<Object, Object>> shuffleResult)
      throws InterruptedException {
    int numberOfReducers = reducersAddresses.size();
    int i = 0;
    for (i = 0; i < numberOfReducers; i++) {

      try (Socket sk = new Socket(reducersAddresses.get(i), Constants.MAPPERS_TO_REDUCERS_PORT);
          ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
        objectOutput.writeObject(shuffleResult.get(i));
      } catch (Exception e) {
        System.out.println("Sending failed trying again in 3 seconds");
        Thread.sleep(3000);
        i--; // recover
      }
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {

    Thread t =
        new Thread(
            () -> {
              Receiver reducerArrayListReceiver = new ArrayListReceiver();
              System.out.println("waiting for reducers addresses: ");

              reducersAddresses =
                  reducerArrayListReceiver.start(Constants.MAINSERVER_TO_MAPPERS_PORT);
              System.out.println("Reducers addresses received");

              System.out.println("Receiving txt file from Main server");
              Receiver data = new FileReceiver("myData");
              System.out.println("File Received");

              System.out.println("Starting mapping Function");
              Map<?, ?> mappingResult =
                  startMapping(data.start(Constants.MAPPERS_FILE_RECEIVER_PORT));
              System.out.println("Mapping Finished");

              System.out.println("Shuffling Started");
              Shuffler hashShuffler = new HashShuffler(mappingResult, reducersAddresses.size());

              shuffleResult = hashShuffler.shuffle();
              System.out.println("shuffling finished");

              try {
                sendShuffleResultToReducers(shuffleResult);
                System.out.println("data sent to reducers");

              } catch (InterruptedException e) {
                e.printStackTrace();
              }

              try {
                DataSender.sendString(MAIN_SERVER_IP, Constants.MAIN_SERVER_PORT, "Finished");
              } catch (IOException e) {
                e.printStackTrace();
              }
              System.out.println("Finished");
            });
    t.start();

    DataSender.sendString(args[0], Integer.parseInt(args[1]), "RegisterMapper");
    t.join();
  }
}
