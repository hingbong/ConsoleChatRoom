package tcp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*

class Client private constructor() {
    /*
   * java.net.Socket 套接字
   * Socket封装了底层TCP协议的通讯细节,使用它可以通过TCP协议与服务端建立连接,
   * 并以两条流进行IO操作完成与服务端的数据交换.
   */
    private var socket: Socket? = null

    init {
        try {
            /*
       * 实例化过程就是连接服务端的过程,若指定的地址和端口的服务端没有响应会抛出异常
       */
            println("connecting to the server")
            socket = Socket("localhost", 8089)
            println("connected")
        } catch (e: IOException) {
            e.printStackTrace()
        }

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

    fun start() {
        // in
        val get = Thread {
            try {
                val inputStream = socket!!.getInputStream()
                val inputStreamReader = InputStreamReader(inputStream,
                        StandardCharsets.UTF_8)
                val bufferedReader = BufferedReader(inputStreamReader)
                var message: String?
                while (bufferedReader.readLine().also { message = it } != null) {
                    println(message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        get.isDaemon = true
        get.start()

        //out
        try {
            val outputStream = socket!!.getOutputStream()
            val printStream = PrintStream(outputStream, true, StandardCharsets.UTF_8)
            val scanner = Scanner(System.`in`)
            var Message: String
            // send message
            while (true) {
                Message = scanner.nextLine()
                if ("exit" == Message) {
                    break
                }
                printStream.println(Message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        private val client = Client()

        @JvmStatic
        fun main(args: Array<String>) {
            client.start()
        }
    }
}
