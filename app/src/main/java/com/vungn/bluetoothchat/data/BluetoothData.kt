package com.vungn.bluetoothchat.data

import android.bluetooth.BluetoothDevice

data class BluetoothData(
    val scannedDevices: Set<BluetoothDevice> = emptySet(),
    val pairedDevices: Set<BluetoothDevice> = emptySet(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val messages: List<BluetoothMessage> = emptyList()
)
