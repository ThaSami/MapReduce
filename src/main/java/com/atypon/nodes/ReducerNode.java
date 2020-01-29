package com.atypon.nodes;

import com.atypon.nodes.receivers.ArrayListReceiver;
import com.atypon.nodes.receivers.MultiHashMapsReceiver;
import com.atypon.nodes.receivers.Receiver;
import com.atypon.nodes.senders.DataSender;
import com.atypon.utility.Constants;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReducerNode {

  static Map<Object, List<Object>> reducerData = new HashMap<>();
  static List<String> mappersAddresses = new ArrayList<>();

  static Map<?, ?> startReducing()
      throws MalformedURLException, NoSuchMethodException, ClassNotFoundException,
          InvocationTargetException, IllegalAccessException {
    File root = new File("./");
    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
    Class<?> cls = Class.forName("ReducerUtil", false, classLoader);
    Method method = cls.getDeclaredMethod("reduce", Map.class);
    return (Map<?, ?>) method.invoke(cls, reducerData);
  }

  // combines list of maps into one map with list of values map<key,list<values>
  static void combiner(List<Map<Object, Object>> listOfMaps) {

    listOfMaps.forEach(
        entry ->
            entry.forEach(
                (k, v) -> {
                  if (!reducerData.containsKey(k)) {
                    reducerData.put(k, new ArrayList<>());
                  }
                  reducerData.get(k).add(v);
                }));
  }

  public static void main(String[] args) throws Exception {

    new Thread(
            () -> {
              System.out.println("Registering to server");
              DataSender.sendString(args[0], Integer.parseInt(args[1]), "RegisterReducer");
            })
        .start();

    System.out.println("Receiving mappers addresses");
    Receiver mapperAddressesReceiver = new ArrayListReceiver();
    mappersAddresses = mapperAddressesReceiver.start(Constants.MAINSERVER_TO_REDUCERS_PORT);
    System.out.println("Mapper's addresses received");

    System.out.println("Receiving data from mappers");
    Receiver data = new MultiHashMapsReceiver(mappersAddresses.size());
    combiner(data.start(Constants.MAPPERS_TO_REDUCERS_PORT));
    System.out.println("Data from mappers Received and combined");

    Map<?, ?> mappingResult = null;
    try {
      System.out.println("Starting Reduce Function");
      mappingResult = startReducing();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("Sending reducing result to Collector");
      DataSender.sendObject(Constants.MAIN_SERVER_IP, Constants.COLLECTOR_PORT, mappingResult);
      System.out.println("Result sent.");
    } catch (Exception e) {
      System.out.println("sending failed trying again");
      Thread.sleep(2000);
      DataSender.sendObject(Constants.MAIN_SERVER_IP, Constants.COLLECTOR_PORT, mappingResult);
    }
  }
}
