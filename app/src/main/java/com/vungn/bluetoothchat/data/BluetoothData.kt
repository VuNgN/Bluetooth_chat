package com.vungn.bluetoothchat.data

import android.bluetooth.BluetoothDevice

data class BluetoothData(
    val scannedDevices: Set<BluetoothDevice> = setOf(),
    val pairedDevices: Set<BluetoothDevice> = setOf(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false
)
