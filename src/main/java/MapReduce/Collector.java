package MapReduce;

import utility.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Collector {
    private static Map<Object, Object> finalResult;

    private Collector() {
    }

    public static void startCollecting(int numberOfReducers) {
        finalResult = new ConcurrentSkipListMap<>();
        try (ServerSocket server = new ServerSocket(Constants.COLLECTOR_PORT)) {
            int dataReceivedFromReducers = 0;
            while (dataReceivedFromReducers != numberOfReducers) {
                Socket client = server.accept();
                dataReceivedFromReducers++;
                new Thread(
                        () -> {
                            try (ObjectInputStream objectInput =
                                         new ObjectInputStream(client.getInputStream())) {
                                Object object = objectInput.readObject();
                                Map<Object, Object> data = (NavigableMap<Object, Object>) object;
                                for (Object k : data.keySet()) {
                                    finalResult.put(k, data.get(k));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void printCollectedDataToFile() {
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
