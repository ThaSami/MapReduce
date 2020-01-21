package com.atypon.nodes;


import com.atypon.nodes.receivers.ArrayListReceiver;
import com.atypon.nodes.receivers.MultiHashMapsReceiver;
import com.atypon.nodes.receivers.Receiver;
import com.atypon.nodes.senders.DataSender;
import com.atypon.utility.Constants;


import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReducerNode {

    static Map<Object, List<Object>> reducerData = new HashMap<>();
    static List<String> mappersAddresses = new ArrayList<>();


    //TODO get mappers addresses


    static void sendResultToCollector(String address, Map<?, ?> result, int collectPort) throws InterruptedException {
        System.out.println("Sending final Result To collector");
        try (Socket sk = new Socket(address, collectPort);
             ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
            objectOutput.writeObject(result);
        } catch (Exception e) {
            System.out.println("sending failed trying again");
            Thread.sleep(1000);
            sendResultToCollector(address, result, collectPort);
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

    static void combiner(List<Map<Object, Object>> listOfMaps) {

        listOfMaps.forEach(entry -> entry.forEach((k, v) -> {
            if (!reducerData.containsKey(k)) {
                reducerData.put(k, new ArrayList<>());
            }
            reducerData.get(k).add(v);
        }));

    }

    public static void main(String[] args) {


        new Thread(
                () -> {
                    System.out.println("Registering to server");
                    DataSender.sendString(Constants.HOST_IP_ADDRESS, Constants.MAIN_SERVER_PORT, "RegisterReducer");
                })
                .start();

        System.out.println("Receiving mappers addresses");
        Receiver mapperAddressesReceiver = new ArrayListReceiver();
        mappersAddresses = mapperAddressesReceiver.start(Constants.REDUCERS_MAPPERSADDRESSES_RECEIVER_PORT);
        System.out.println("Mapper's addresses received");

        System.out.println("Receiving data from mappers");
        Receiver data = new MultiHashMapsReceiver(mappersAddresses.size());
        combiner(data.start(Constants.TREE_MAP_RECEIVER_PORT));
        System.out.println("Data from mappers Received and combined");

        try {
            Map<?, ?> mappingResult = startReducing();
            sendResultToCollector(Constants.HOST_IP_ADDRESS, mappingResult, Constants.COLLECTOR_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
