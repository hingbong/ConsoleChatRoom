package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {

  private static final UDPClient CLIENT = new UDPClient();


  private UDPClient() {
  }

  public static void main(String[] args) {
    CLIENT.receive();
    CLIENT.send();
  }

  private void send() {
    Thread thread = new Thread(() -> {
      Scanner sc = new Scanner(System.in);
      try {
        DatagramSocket socket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("192.168.42.255");
        String sendMessage;
        while (!(sendMessage = sc.nextLine()).equals("exit")) {
          DatagramPacket send = new DatagramPacket(sendMessage.getBytes(StandardCharsets.UTF_8),
              sendMessage.length(), inetAddress, 8888);
          socket.send(send);
        }
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }

  private void receive() {

    Thread thread = new Thread(() -> {
      try {
        DatagramSocket socket = new DatagramSocket(8888);
        byte[] buffer = new byte[1024];
        DatagramPacket receive = new DatagramPacket(buffer, buffer.length);
        while (true) {
          socket.receive(receive);
          byte[] get = receive.getData();
          String getString = new String(get, 0, get.length).trim();
          SocketAddress client = receive.getSocketAddress();
          System.out.println(client + "--------------" + getString);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }
}