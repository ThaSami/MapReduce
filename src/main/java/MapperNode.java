import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class MapperNode {


  static void RegisterContainer(DataOutputStream dataOutputStream) throws IOException {
    System.out.println("Registering to server");
    dataOutputStream.writeUTF("RegisterMapper");
    dataOutputStream.flush();

  }

  static void dataReciever() {

  }


  public static void main(String[] args) throws IOException {

    Socket socket = new Socket(args[0], 7777);
    // get the output stream from the socket.
    OutputStream outputStream = socket.getOutputStream();
    // create a data output stream from the output stream so we can send data through it
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

    dataOutputStream.close();

    //Recieve Num of Reducers and there Addresses


    socket.close();
    //TODO: import the class dynamically.
  }
}
