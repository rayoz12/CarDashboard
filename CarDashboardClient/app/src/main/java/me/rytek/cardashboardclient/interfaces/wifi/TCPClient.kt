package me.rytek.cardashboardclient.interfaces.wifi

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.*
import java.net.Socket
import kotlin.concurrent.thread


class TCPClient(val address: String, val port: Int) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private var socket: Socket? = null

    private val _messageFlow = MutableSharedFlow<String>() // private mutable shared flow
    val onMessageEvent = _messageFlow.asSharedFlow() //publicly exposed as read-only shared flow


    // a reference to the thread
    private lateinit var recvThread: Thread;
    // used to send messages
    private lateinit var mBufferOut: PrintWriter
    // used to read messages from the server
    private lateinit var mBufferIn: BufferedReader

    fun connect() {
        recvThread = thread {
            socket = Socket(address, port)
            try {

                //sends the message to the server
                mBufferOut =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())), true)

                //receives the message which the server sends back
                mBufferIn = BufferedReader(InputStreamReader(socket!!.getInputStream()))


                //While the thread hasn't been interrupted (cancelled)
                while (!Thread.currentThread().isInterrupted) {
                    try {
                        val message = mBufferIn.readLine()
                        if (message != null) {
                            _messageFlow.tryEmit(message)
                            Log.d(
                                "TCP",
                                "RESPONSE FROM SERVER: Received Message: '$message'"
                            )
                        }
                    }
                    catch (e: InterruptedException) {
                        // Kill the loop
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e("TCP", "S: Error", e)
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                if (socket != null) {
                    socket!!.close()
                }
            }
        }

    }

    fun stop() {
        recvThread.interrupt()
    }

    fun send(data: String) {
        if (socket != null && socket!!.isConnected) {
            mBufferOut.print(data)
        }
    }

}












//inner class TCPReader {
//    lateinit var job: Job
//
//    var stopReading = false
//
//    lateinit var messageFlow: Flow<String>
//
//    fun start(): Flow<String> {
//        val readerIn = BufferedReader(InputStreamReader(socket!!.getInputStream()))
//        var outputFromServer: String = ""
//
//        job = scope.launch(Dispatchers.IO) {
//            messageFlow = flow {
//                while (readerIn.readLine().also { outputFromServer = it } != null) {
//                    emit(outputFromServer)
//                }
//            }.flowOn(scope.coroutineContext)
//        }
//        return messageFlow
//    }
//
//    fun stop() {
//        job.cancel()
//    }
//}



