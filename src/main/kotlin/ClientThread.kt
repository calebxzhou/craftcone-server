import java.io.BufferedInputStream
import java.io.DataInputStream
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.Executors

/**
 * Created  on 2023-06-28,10:46.
 */
class ClientThread(private val clientSocket: Socket) : Thread() {
    val thpool = Executors.newCachedThreadPool()
    val buffer = ByteArray(16*1024*1024)
    val clientAddr = clientSocket.remoteSocketAddress
    override fun run() {
        while (true){
            try {
                val input = clientSocket.getInputStream()
                var bytesRead = 0
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val data = buffer.copyOf(bytesRead)
                    println("${clientAddr}传入${data.size}")
                    clientSocketList.forEach { client ->
                        //包转发给除了自己以外的玩家
                        if(client != clientSocket){
                            client.getOutputStream().write(data)
                        }
                    }
                }
            } catch (e: SocketException) {
                println("${clientAddr}退出，原因：${e.localizedMessage}")
                clientSocketList.remove(clientSocket)
                clientThreadList.remove(this)
                this.stop()
                break
            }
        }
    }
}