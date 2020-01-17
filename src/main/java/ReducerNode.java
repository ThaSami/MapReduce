import utility.Constants;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReducerNode {

    static NavigableMap<Object, List<Object>> reducerData = new ConcurrentSkipListMap<>();
    static boolean startReducing = false;

    static void ReceiveTreeMap() {
        new Thread(
                () -> {
                    try (ServerSocket server = new ServerSocket(Constants.TREE_MAP_RECEIVER_PORT)) {
                        while (!startReducing) {
                            Socket client = server.accept();
                            new Thread(
                                    () -> {
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
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .start();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
                .start();
    }

    static void RegisterContainer(String host) {

        try (Socket socket = new Socket(host, Constants.MAIN_SERVER_PORT);
             OutputStream outputStream = socket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            System.out.println("Registering to server");
            dataOutputStream.writeUTF("RegisterReducer");
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void receiveStartFlag() {
        try (ServerSocket server = new ServerSocket(Constants.REDUCER_START_RECEIVER_PORT)) {
            Socket sk = server.accept();
            DataInputStream in = new DataInputStream(sk.getInputStream());
            String query;
            while ((query = in.readUTF()) != null) {
                if (query.startsWith("start")) startReducing = true;
                System.out.println("Start flag received");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("sent Shuffle Result To reducers");
    }

    public static void main(String[] args) {
        new Thread(
                () -> {
                    RegisterContainer(args[0]);
                    receiveStartFlag();
                })
                .start();

        ReceiveTreeMap();
        try {
            File root = new File("./");
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("ReducerUtil", false, classLoader);
            Method method = cls.getDeclaredMethod("reducer", Map.class);
            Map<?, ?> result = (Map<?, ?>) method.invoke(cls, reducerData);
            sendResultToCollector(args[0], result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
