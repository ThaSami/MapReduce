package com.atypon.nodes.receivers;

import com.atypon.utility.Constants;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiHashMapsReceiver<T, E> implements Receiver {

  private int numOfHashMapsToReceive;
  private List<Map<T, E>> dataReceived;
  private Lock lock = new ReentrantLock();

  public MultiHashMapsReceiver(int numOfHashMapsToReceive) {
    this.numOfHashMapsToReceive = numOfHashMapsToReceive;
    dataReceived = new CopyOnWriteArrayList<>();
    for (int i = 0; i < numOfHashMapsToReceive; i++) {
      dataReceived.add(i, new HashMap());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<T, E>> start(int port) {
    System.out.println("Receiving Data From Mappers");

    try (ServerSocket server = new ServerSocket(port)) {
      AtomicInteger mapsReceived = new AtomicInteger(0);
      while (mapsReceived.get() != numOfHashMapsToReceive) {
        System.out.println("Waiting for client");
        Socket client = server.accept();
        System.out.println("Connected to mapper " + client.getInetAddress());

        Thread t =
                new Thread(
                        () -> {
                          System.out.println("Receiving Data ");
                          try (ObjectInputStream objectInput =
                                       new ObjectInputStream(client.getInputStream())) {
                            Object object = objectInput.readObject();
                            Map<T, E> data = (Map<T, E>) object;
                            lock.lock();

                            dataReceived.get(mapsReceived.get()).putAll(data);

                            mapsReceived.getAndIncrement();
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
    return dataReceived;
  }
}
