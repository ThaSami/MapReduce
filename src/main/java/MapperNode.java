import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapperNode {

  static List<String> reducersAddresses;
  static List<TreeMap<Object, Object>> shuffleResult;

  static void shuffler(Map<?, ?> result) {
    int numberOfReducers = reducersAddresses.size();

    shuffleResult = new ArrayList<>(numberOfReducers);
    for (int i = 0; i < numberOfReducers; i++) {
      shuffleResult.add(i, new TreeMap());
    }
    for (Object k : result.keySet()) {
      shuffleResult.get(Math.abs(k.hashCode()) % numberOfReducers).put(k, result.get(k));
    }
  }

  static void RegisterContainer(String host) {

    try (Socket socket = new Socket(host, 7777);
         OutputStream outputStream = socket.getOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
      System.out.println("Registering to server");
      dataOutputStream.writeUTF("RegisterMapper");
      dataOutputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void ReceiveFile() {
    try (ServerSocket serverSocket = new ServerSocket(6666);
         Socket socket = serverSocket.accept();
         InputStream in = socket.getInputStream();
         OutputStream out = new FileOutputStream("myData")) {

      byte[] bytes = new byte[8192];

      int count;
      while ((count = in.read(bytes)) > 0) {
        out.write(bytes, 0, count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void ReceiveReducersAdresses() {
    System.out.println("Receivng Reducers Addresses");
    try (ServerSocket myServerSocket = new ServerSocket(49999);
         Socket skt = myServerSocket.accept();
         ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream())) {
      Object object = objectInput.readObject();
      reducersAddresses = (ArrayList<String>) object;
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Reducers addresses recieved");
  }

  public static void main(String[] args) {

    new Thread(
            () -> {
              RegisterContainer(args[0]);
            })
            .start();

    new Thread(
            () -> {
              ReceiveReducersAdresses();
            })
            .start();

    new Thread(
            () -> {
              ReceiveFile();
              try {
                File root = new File("./");
                URLClassLoader classLoader =
                        URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
                Class<?> cls =
                        Class.forName("MapperUtil", false, classLoader);
                Method method = cls.getDeclaredMethod("mapping", String.class);
                Map<?, ?> result = (Map<?, ?>) method.invoke(cls, "./myData");
                shuffler(result);
                // TODO Send TreeMaps to Reducers
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
            .start();
  }
}
