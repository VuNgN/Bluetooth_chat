package com.vungn.bluetoothchat.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import com.vungn.bluetoothchat.data.ConnectionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothHelper {
    val bluetoothAdapter: BluetoothAdapter
    val bluetoothManager: BluetoothManager
    val state: StateFlow<Int>
    val scannedDevices: StateFlow<Set<BluetoothDevice>>
    val pairedDevices: StateFlow<Set<BluetoothDevice>>
    fun isEnable(): Boolean
    fun startDiscovery()
    fun cancelDiscovery()
    fun refreshDiscovery()
    fun startServer(): Flow<ConnectionResult>
    fun connectToServer(device: BluetoothDevice): Flow<ConnectionResult>
    fun closeConnections()
    fun release()
}