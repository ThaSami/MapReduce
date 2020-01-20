package com.atypon.nodes.reducernode;

import com.atypon.utility.Constants;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.atypon.utility.Constants.HOST_IP_ADDRESS;

public class ReducerNode {

  static Map<Object, List<Object>> reducerData = new HashMap<>();
  static Lock lock = new ReentrantLock();
  static int numOfMappers = 0;
  static volatile boolean startReducingFlag = false;


  static void receiveTreeMap() {
    System.out.println("Receiving Data From Mappers");

    try (ServerSocket server = new ServerSocket(Constants.TREE_MAP_RECEIVER_PORT)) {
      AtomicInteger mapsReceived = new AtomicInteger(0);
      while (mapsReceived.get() != numOfMappers) {
        System.out.println("Waiting for client");
        Socket client = server.accept();
        mapsReceived.getAndIncrement();
        System.out.println("Connected to mapper " + client.getInetAddress());

        Thread t =
                new Thread(
                        () -> {
                          System.out.println("Receiving Data ");
                          try (ObjectInputStream objectInput =
                                       new ObjectInputStream(client.getInputStream())) {
                            Object object = objectInput.readObject();

                            Map<Object, Object> data = (Map) object;
                            lock.lock();
                            data.keySet()
                                    .forEach(
                                            k -> {
                                              if (reducerData.containsKey(k)) {
                                                reducerData.get(k).add(data.get(k));
                                              } else {
                                                List<Object> arr = new CopyOnWriteArrayList<>();
                                                arr.add(data.get(k));
                                                reducerData.put(k, arr);
                                              }
                                            });
                            lock.unlock();
                            System.out.println("Data Received");
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        });
        t.start();
        t.join();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Data from Mappers Received");
  }

  static void registerContainer(String host) {

    try (Socket socket = new Socket(host, Constants.MAIN_SERVER_PORT);
         OutputStream outputStream = socket.getOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
      System.out.println("Registering to server");
      dataOutputStream.writeUTF("RegisterReducer");
      dataOutputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Registerd Container");
  }

  static void receiver() {
    try (ServerSocket server = new ServerSocket(Constants.REDUCER_RECEIVER_PORT)) {
      int dataReceived = 0;
      while (dataReceived < 2) {
        dataReceived++;
        Socket sk = server.accept();
        new Thread(
                () -> {
                  try (DataInputStream in = new DataInputStream(sk.getInputStream())) {
                    String query;
                    while ((query = in.readUTF()) != null) {
                      if (query.startsWith("start")) {
                        System.out.println("Start flag received");
                        startReducingFlag = true;

                      } else {
                        numOfMappers = Integer.parseInt(query);
                      }
                    }
                  } catch (IOException e) {
                    //normal
                  }
                })
                .start();
      }
    } catch (Exception e) {

    }
  }

  static void sendResultToCollector(String address, Map<?, ?> result) {
    System.out.println("Sending final Result To collector");
    try (Socket sk = new Socket(address, Constants.COLLECTOR_PORT);
         ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
      objectOutput.writeObject(result);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Reduce Result Sent to Collector");
  }


  static Map<?, ?> startReducing()
          throws MalformedURLException, NoSuchMethodException, ClassNotFoundException,
          InvocationTargetException, IllegalAccessException {

    System.out.println("Starting Reducing function");
    File root = new File("./");
    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
    Class<?> cls = Class.forName("ReducerUtil", false, classLoader);
    Method method = cls.getDeclaredMethod("reduce", Map.class);
    Map<?, ?> result = (Map<?, ?>) method.invoke(cls, reducerData);
    System.out.println("Reducers Finished");
    return result;
  }

  public static void main(String[] args) {
    new Thread(
            () -> {
              registerContainer(HOST_IP_ADDRESS);
              receiver();
            })
            .start();

    receiveTreeMap();

    try {
      Map<?, ?> mappingResult = startReducing();
      sendResultToCollector(HOST_IP_ADDRESS, mappingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
