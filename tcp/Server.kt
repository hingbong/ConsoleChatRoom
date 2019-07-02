package tcp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

class Server private constructor() {

    private val allOut = ConcurrentHashMap<String, PrintStream>()

    fun start() {
        try {
            /*
       * accept()服务器会持续运行监听,当客户端连接会返回Socket实例,多次调用accept()可以与多个客户端进行连接
       */

            while (true) {
                val socket = serverSocket!!.accept()
                /*
         * 启动一个线程与客户端交互
         */
                val t = Thread {
                    val host = socket.remoteSocketAddress.toString()
                    var message: String?
                    try {
                        // in
                        val inputStream = socket.getInputStream()
                        val inputStreamReader = InputStreamReader(inputStream,
                                StandardCharsets.UTF_8)
                        val bufferedReader = BufferedReader(inputStreamReader)

                        // out
                        val outputStream = socket.getOutputStream()
                        val printStream = PrintStream(outputStream, true, StandardCharsets.UTF_8)
                        // 将新的printWrite存入集合中
                        allOut[host] = printStream
                        println(allOut)
                        allOut.entries.parallelStream().filter { e -> host != e.key }.forEach { e -> e.value.println(host + "已加入聊天室,当前在线:" + allOut.size) }
                        while (bufferedReader.readLine().also { message = it } != null) {
                            println("$host:$message")
                            allOut.entries.parallelStream().filter { e -> host != e.key }.forEach { e -> e.value.println(message) }
                        }
                    } catch (e: Exception) {
                        if (!e.message.equals("Connection reset")) {
                            e.fillInStackTrace()
                        }
                    } finally {
                        allOut.remove(host)
                        allOut.entries.parallelStream().filter { e -> host != e.key }.forEach { e -> e.value.println(host + "已下线,当前在线:" + allOut.size) }
                        //        System.out.println(allOut);
                        try {
                            socket.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }
                t.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val SERVER = Server()
        /*
   *  运行在服务端的ServerSocket主要有两个作用:
   * 1,向系统申请端口
   * 2,监听端口,当客户端由该端口连接,服务端会自动实例化一个Socket.
   */
        private var serverSocket: ServerSocket? = null

        init {
            try {
                serverSocket = ServerSocket(8089)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        @JvmStatic
        fun main(args: Array<String>) {
            SERVER.start()
        }
    }
}
