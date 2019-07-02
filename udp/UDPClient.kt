package udp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.StandardCharsets

class UDPClient private constructor() {

    private fun send() {
        val thread = Thread {
            val bis = BufferedReader(InputStreamReader(System.`in`))
            try {
                val socket = DatagramSocket()
                val inetAddress = InetAddress.getByName("192.168.43.255")
                var sendMessage: String
                while ("exit" != bis.readLine().also { sendMessage = it }) {
                    val send = DatagramPacket(sendMessage.toByteArray(StandardCharsets.UTF_8),
                            sendMessage.length, inetAddress, 8888)
                    socket.send(send)
                }
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun receive() {

        val thread = Thread {
            try {
                val socket = DatagramSocket(8888)
                var buffer: ByteArray
                while (true) {
                    buffer = ByteArray(1024)
                    val receive = DatagramPacket(buffer, buffer.size)
                    socket.receive(receive)
                    val get = receive.data
                    val getString = String(get, 0, get.size).trim { it <= ' ' }
                    val client = receive.socketAddress
                    println("$client--------------$getString")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.isDaemon = true
        thread.start()
    }

    companion object {

        private val CLIENT = UDPClient()

        @JvmStatic
        fun main(args: Array<String>) {
            CLIENT.receive()
            CLIENT.send()
        }
    }
}

