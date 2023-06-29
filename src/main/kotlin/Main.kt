import java.net.ServerSocket
import java.net.Socket

val clientSocketList = hashSetOf<Socket>()
val clientThreadList = hashSetOf<ClientThread>()
fun main(args: Array<String>) {
    val serverSocket = ServerSocket(19198)
    while (true) {
        val clientSocket = serverSocket.accept()
        println("新连接：$clientSocket.remoteSocketAddress")
        clientSocketList += clientSocket
        val clientThread = ClientThread(clientSocket)
        clientThreadList += clientThread
        clientThread.start()
    }
}
/*

fun processSocket(clientSocket: Socket){
    if(!clientSocketList.contains(clientSocket)){
        clientSocketList += clientSocket
    }
   // val buffer = ByteArray(8*1024*1024)

    val data = clientSocket.getInputStream().use {  it.readAllBytes()}
    println("接收${data.size}")
    clientSocketList.forEach { client ->
        client.getOutputStream().write(data)
    }
    clientSocket.getInputStream().use { inp->
        clientSocket.getOutputStream().use { out->

            var bytesRead: Int
            while (inp.read(buffer).also { bytesRead = it } != -1) {

                val data = buffer.copyOf(bytesRead)
                clients.forEach{ client ->
                    //if(client.remoteSocketAddress != clientSocket.remoteSocketAddress){
                        client.getOutputStream().use { out->
                            out.write(data)
                        }
                    //}
                }
                println(data.size)

            }
        }
    }

}*/
