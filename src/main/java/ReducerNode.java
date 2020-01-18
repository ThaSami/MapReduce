import utility.Constants;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReducerNode {

    static NavigableMap<Object, List<Object>> reducerData = new ConcurrentSkipListMap<>();

    static volatile boolean startReducing = false;

    static void receiveTreeMap() {
        System.out.println("Receiving Data From Mappers");

        try (ServerSocket server = new ServerSocket(Constants.TREE_MAP_RECEIVER_PORT)) {
            while (!startReducing) {
                System.out.println("Waiting for client");
                Socket client = server.accept();
                System.out.println("Connected to mapper " + client.getInetAddress());
                Thread t =
                        new Thread(
                                () -> {
                                    System.out.println("Started thread ");
                                    try (ObjectInputStream objectInput =
                                                 new ObjectInputStream(client.getInputStream())) {
                                        Object object = objectInput.readObject();
                                        Map<Object, Object> data = (TreeMap<Object, Object>) object;
                                        for (Object k : data.keySet()) {
                                            if (reducerData.containsKey(k)) {
                                                reducerData.get(k).add(data.get(k));
                                            } else {
                                                ArrayList<Object> arr = new ArrayList<>();
                                                arr.add(data.get(k));
                                                reducerData.put(k, arr);
                                            }
                                        }
                                        System.out.println("Thread Finished");
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

    static void receiveStartFlag() {
        System.out.println("Waiting for Start");
        try (ServerSocket server = new ServerSocket(Constants.REDUCER_START_RECEIVER_PORT)) {
            Socket sk = server.accept();
            DataInputStream in = new DataInputStream(sk.getInputStream());
            String query;
            while ((query = in.readUTF()) != null) {
                if (query.startsWith("start")) startReducing = true;
            }
            in.close();
        } catch (Exception e) {
            // normal
        }
        System.out.println("Start flag received");
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
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
            ClassNotFoundException, MalformedURLException {
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
                    registerContainer("192.168.8.102");
                    receiveStartFlag();
                })
                .start();
        receiveTreeMap();

        try {
            Map<?, ?> mappingResult = startReducing();
            sendResultToCollector("192.168.8.102", mappingResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
