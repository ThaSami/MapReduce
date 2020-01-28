package com.atypon.nodes.receivers;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiHashMapsReceiver implements Receiver {

  private int numOfHashMapsToReceive;

  private List<Map<Object, Object>> dataReceived;
  private Lock lock = new ReentrantLock();

  public MultiHashMapsReceiver(int numOfHashMapsToReceive) {
    this.numOfHashMapsToReceive = numOfHashMapsToReceive;
    dataReceived = new ArrayList<>();
    for (int i = 0; i < numOfHashMapsToReceive; i++) {
      dataReceived.add(i, new HashMap());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<Object, Object>> start(int port) {

    try (ServerSocket server = new ServerSocket(port)) {
      AtomicInteger mapsReceived = new AtomicInteger(0);
      while (mapsReceived.get() != numOfHashMapsToReceive) {
        Socket client = server.accept();

        Thread t =
                new Thread(
                        () -> {
                          try (ObjectInputStream objectInput =
                                       new ObjectInputStream(client.getInputStream())) {
                            Object object = objectInput.readObject();
                            Map<Object, Object> data = (Map<Object, Object>) object;
                            lock.lock();

                            dataReceived.get(mapsReceived.get()).putAll(data);

                            mapsReceived.getAndIncrement();
                            lock.unlock();
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
    return dataReceived;
  }

  public List<Map<Object, Object>> getDataReceived() {
    return dataReceived;
  }
}
