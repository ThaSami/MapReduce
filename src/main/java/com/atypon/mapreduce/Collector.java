package com.atypon.mapreduce;

import com.atypon.docker.ContainersHandler;
import com.atypon.utility.Constants;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Collector {
    private static Map<Object, Object> finalResult;
    @Getter
    private static CountDownLatch allDataCollectedLatch;

    private Collector() {
    }

    public static void startCollecting(int numberOfReducers) {
        allDataCollectedLatch = new CountDownLatch(numberOfReducers);
        finalResult = new ConcurrentSkipListMap<>();
        try (ServerSocket server = new ServerSocket(Constants.COLLECTOR_PORT)) {
            AtomicInteger dataReceivedFromReducers = new AtomicInteger(0);
            while (dataReceivedFromReducers.get() != numberOfReducers) {
                Socket skt = server.accept();
                System.out.println("receiving data from: " + skt.getInetAddress());
                dataReceivedFromReducers.getAndIncrement();
                Thread t =
                        new Thread(
                                () -> {
                                    try (ObjectInputStream objectInput =
                                                 new ObjectInputStream(skt.getInputStream())) {
                                        Object object = objectInput.readObject();
                                        Map<Object, Object> data = (NavigableMap<Object, Object>) object;
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
        } catch (IOException ex) {
            ex.printStackTrace();
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
