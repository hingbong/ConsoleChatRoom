package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

  private static final Client client = new Client();
  /*
   * java.net.Socket 套接字
   * Socket封装了底层TCP协议的通讯细节,使用它可以通过TCP协议与服务端建立连接,
   * 并以两条流进行IO操作完成与服务端的数据交换.
   */
  private Socket socket;

  private Client() {
    try {
      /*
       * 实例化过程就是连接服务端的过程,若指定的地址和端口的服务端没有响应会抛出异常
       */
      System.out.println("connecting to the server");
      socket = new Socket("localhost", 8089);
      System.out.println("connected");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    client.start();
  }

//  public void newServer(String host, int port) {
//    try {
//      /*
//       * 实例化过程就是连接服务端的过程,若指定的地址和端口的服务端没有响应会抛出异常
//       */
//      System.out.println("connecting to the server");
//      socket = new Socket(host, port);
//      System.out.println("connected");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  public void start() {
    // in
    Thread get = new Thread(() -> {
      try {
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
            StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String message;
        while ((message = bufferedReader.readLine()) != null) {
          System.out.println(message);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    get.setDaemon(true);
    get.start();

    //out
    try {
      OutputStream outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
      Scanner scanner = new Scanner(System.in);
      String Message;
      // send message
      while (true) {
        Message = scanner.nextLine();
        if ("exit".equals(Message)) {
          break;
        }
        printWriter.println(Message);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
