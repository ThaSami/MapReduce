import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapperNode {

  static List<String> reducersAddresses;


  static void RegisterContainer(String host) {

    try (
            Socket socket = new Socket(host, 7777);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);) {
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
    try (
            ServerSocket myServerSocket = new ServerSocket(49999);
            Socket skt = myServerSocket.accept();
            ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream());) {
      Object object = objectInput.readObject();
      reducersAddresses = (ArrayList<String>) object;
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Reducers addresses recieved");
  }


  public static void main(String[] args) throws IOException {

    new Thread(() -> {
      RegisterContainer(args[0]);

    }).start();

    new Thread(() -> {
      ReceiveFile();
    }).start();

    new Thread(() -> {
      ReceiveReducersAdresses();
    }).start();

    //TODO: import the class dynamically.

    File root = new File("./");

    try {
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
      Class<?> cls = Class.forName("MapperUtil", true, classLoader); // Should print "hello".
      Method method = cls.getDeclaredMethod("mapping", String.class);
      Map<?, ?> result = (Map<?, ?>) method.invoke(cls, "./myData");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
