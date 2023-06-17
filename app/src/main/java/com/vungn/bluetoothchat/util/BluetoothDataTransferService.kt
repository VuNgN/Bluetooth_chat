package com.vungn.bluetoothchat.util

import android.bluetooth.BluetoothSocket
import com.vungn.bluetoothchat.data.BluetoothMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService constructor(private val socket: BluetoothSocket) {
    fun listenForIncomingMessage(): Flow<BluetoothMessage> = flow {
        if (!socket.isConnected) {
            return@flow
        }
        val buffer = ByteArray(1024)
        while (true) {
            val byteCount = try {
                socket.inputStream.read(buffer)
            } catch (e: IOException) {
                throw TransferFailedException()
            }
            emit(
                buffer.decodeToString(endIndex = byteCount).toBlueToothMessage(isFromLocal = false)
            )
        }
    }.flowOn(Dispatchers.IO)

    suspend fun sendMessage(byteArray: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(byteArray)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }

    class TransferFailedException : IOException()
}