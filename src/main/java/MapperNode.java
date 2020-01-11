import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class MapperNode {
  public static void main(String[] args) throws IOException {


    Socket socket = new Socket(args[0], 7777);
    System.out.println("Connected!");

    // get the output stream from the socket.
    OutputStream outputStream = socket.getOutputStream();
    // create a data output stream from the output stream so we can send data through it
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

    System.out.println("Sending string to the ServerSocket");

    // write the message we want to send
    dataOutputStream.writeUTF("RegisterMapper");
    dataOutputStream.flush(); // send the message
    dataOutputStream.close(); // close the output stream when we're done.

    System.out.println("Closing socket and terminating program.");
    socket.close();

    //TODO: import the class dynamically.
  }
}
