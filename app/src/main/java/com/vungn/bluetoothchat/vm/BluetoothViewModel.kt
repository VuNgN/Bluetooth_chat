package com.vungn.bluetoothchat.vm

import android.bluetooth.BluetoothDevice
import com.vungn.bluetoothchat.data.BluetoothData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothViewModel {
    val state: StateFlow<Int>
    val isEnable: StateFlow<Boolean>
    val errorMessage: StateFlow<String?>
    val data: Flow<BluetoothData>
    fun scanBluetooth()
    fun refresh()
    fun launchServer()
    fun connectToServer(device: BluetoothDevice)
    fun disconnect()
}