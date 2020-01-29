package com.atypon.mapreduceworkflow;

import com.atypon.utility.Constants;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Collector {
  private static Map<Object, Object> finalResult;
  @Getter private static CountDownLatch allDataCollectedLatch;

  private Collector() {}

  public static void startCollecting(int numberOfReducers) throws IOException {
    allDataCollectedLatch =
        new CountDownLatch(
            numberOfReducers); // initialize the latch to let the method receive data from all
                               // reducers to stop other threads from executing.
    finalResult = new ConcurrentSkipListMap<>();
    try (ServerSocket server = new ServerSocket(Constants.COLLECTOR_PORT)) {
      AtomicInteger dataReceivedFromReducers =
          new AtomicInteger(0); // keep track of number of data received.
      while (dataReceivedFromReducers.get()
          != numberOfReducers) { // exit when the data has been received from all reducers
        Socket skt = server.accept();
        dataReceivedFromReducers.getAndIncrement();
        Thread t =
            new Thread(
                () -> {
                  try (ObjectInputStream objectInput =
                      new ObjectInputStream(skt.getInputStream())) {
                    Object object = objectInput.readObject();
                    Map<Object, Object> data = (Map<Object, Object>) object;
                    for (Map.Entry<?, ?> entry : data.entrySet()) {
                      finalResult.put(entry.getKey(), entry.getValue());
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  allDataCollectedLatch.countDown();
                });
        t.start();
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  public static void printCollectedDataToFile() {
    try (BufferedWriter dst = new BufferedWriter(new FileWriter("output.txt"))) {
      finalResult
          .keySet()
          .forEach(
              k -> {
                try {
                  dst.write(k + "," + finalResult.get(k) + "\n");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
