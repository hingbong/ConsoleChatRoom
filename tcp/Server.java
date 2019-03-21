package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

  private static final Server SERVER = new Server();
  /*
   *  运行在服务端的ServerSocket主要有两个作用:
   * 1,向系统申请端口
   * 2,监听端口,当客户端由该端口连接,服务端会自动实例化一个Socket.
   */
  private static ServerSocket serverSocket;

  static {
    try {
      serverSocket = new ServerSocket(8089);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ConcurrentHashMap<String, PrintWriter> allOut = new ConcurrentHashMap<>();


  private Server() {
  }

  public static void main(String[] args) {
    SERVER.start();
  }

  public void start() {
    try {
      /*
       * accept()服务器会持续运行监听,当客户端连接会返回Socket实例,多次调用accept()可以与多个客户端进行连接
       */
      //noinspection InfiniteLoopStatement
      while (true) {
        Socket socket = serverSocket.accept();
        /*
         * 启动一个线程与客户端交互
         */
        Thread t = new Thread(() -> {
          String host = socket.getRemoteSocketAddress().toString();
          String message;
          try {
            // in
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // out
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
            // 将新的printWrite存入集合中
            allOut.put(host, printWriter);
            System.out.println(allOut);
            for (Iterator<ConcurrentHashMap.Entry<String, PrintWriter>> iterator = allOut.entrySet()
                .iterator();
                iterator.hasNext(); ) {
              ConcurrentHashMap.Entry<String, PrintWriter> entry = iterator.next();
              if (host.equals(entry.getKey())) {
                continue;
              }
              entry.getValue().println(host + "已加入聊天室,当前在线:" + allOut.size());
            }
            while ((message = bufferedReader.readLine()) != null) {
              System.out.println(host + ":" + message);
              for (Iterator<ConcurrentHashMap.Entry<String, PrintWriter>> iterator = allOut
                  .entrySet()
                  .iterator();
                  iterator.hasNext(); ) {
                ConcurrentHashMap.Entry<String, PrintWriter> entry = iterator.next();
                if (host.equals(entry.getKey())) {
                  continue;
                }
                entry.getValue().println(message);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            allOut.remove(host);
            for (Iterator<ConcurrentHashMap.Entry<String, PrintWriter>> iterator = allOut.entrySet()
                .iterator();
                iterator.hasNext(); ) {
              ConcurrentHashMap.Entry<String, PrintWriter> entry = iterator.next();
              entry.getValue().println(host + "已下线,当前在线:" + allOut.size());
            }
//        System.out.println(allOut);
            try {
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        t.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
