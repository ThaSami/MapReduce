import utility.Constants;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapperNode {

    static List<String> reducersAddresses;
    static List<TreeMap<Object, Object>> shuffleResult;
    static volatile boolean startMapping = false;

    static void shuffler(Map<?, ?> result) {
        System.out.println("Started Shuffling");
        int numberOfReducers = reducersAddresses.size();

        shuffleResult = new ArrayList<>(numberOfReducers);
        for (int i = 0; i < numberOfReducers; i++) {
            shuffleResult.add(i, new TreeMap());
        }
        for (Object k : result.keySet()) {
            shuffleResult.get(Math.abs(k.hashCode()) % numberOfReducers).put(k, result.get(k));
        }
        System.out.println("Shuffling Finished");
    }

    static void sendShuffleResultToReducer() {
        System.out.println("Sending Shuffle Result To reducers");
        int numberOfReducers = reducersAddresses.size();
        for (int i = 0; i < numberOfReducers; i++) {

            try (Socket sk = new Socket(reducersAddresses.get(i), Constants.TREE_MAP_RECEIVER_PORT);
                 ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream())) {
                objectOutput.writeObject(shuffleResult.get(i));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("sent Shuffle Result To reducers");
    }

    static void reportToMainServer(String host, String query) {
        try (Socket socket = new Socket(host, Constants.MAIN_SERVER_PORT);
             OutputStream outputStream = socket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            System.out.println("Sending : " + query + " to main server");
            dataOutputStream.writeUTF(query);
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ReceiveFile() {
        System.out.println("Receiving File");
        try (ServerSocket serverSocket = new ServerSocket(Constants.MAPPERS_FILE_RECEIVER_PORT);
             Socket socket = serverSocket.accept();
             InputStream in = socket.getInputStream();
             OutputStream out = new FileOutputStream("myData.txt")) {

            byte[] bytes = new byte[8192];

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("File Received");
    }

    static void ReceiveReducersAdresses() {
        System.out.println("Waiting for Reducers Addresses");
        try (ServerSocket myServerSocket =
                     new ServerSocket(Constants.MAPPERS_REDUCERADDRESS_RECEIVER_PORT);
             Socket skt = myServerSocket.accept();
             ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream())) {
            Object object = objectInput.readObject();
            reducersAddresses = (ArrayList<String>) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Reducers addresses received");
        startMapping = true;
    }

    static Map<?, ?> startMapping() {
        System.out.println("Starting mapping");
        Map<?, ?> result = null;
        try {
            File root = new File("./");
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("MapperUtil", false, classLoader);
            Method method = cls.getDeclaredMethod("mapping", String.class);
            result = (Map<?, ?>) method.invoke(cls, "./myData.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Mapping Finished");
        return result;
    }

    public static void main(String[] args) {

        new Thread(
                MapperNode::ReceiveReducersAdresses)
                .start();

        new Thread(
                () -> {
                    reportToMainServer("192.168.8.102", "RegisterMapper");
                })
                .start();

        ReceiveFile();

        while (!startMapping) {
        }
        Map<?, ?> mappingResult = startMapping();
        shuffler(mappingResult);
        sendShuffleResultToReducer();
        reportToMainServer("192.168.8.102", "Finished");
    }
}
