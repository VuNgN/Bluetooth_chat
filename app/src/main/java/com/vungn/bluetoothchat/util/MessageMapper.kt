package com.vungn.bluetoothchat.util

import com.vungn.bluetoothchat.data.BluetoothMessage

fun String.toBlueToothMessage(isFromLocal: Boolean): BluetoothMessage {
    val name = substringBeforeLast("#")
    val message = substringAfter("#")
    return BluetoothMessage(message = message, sender = name, isFromLocal = isFromLocal)
}

fun BluetoothMessage.toByteArray(): ByteArray = "$sender#$message".encodeToByteArray()