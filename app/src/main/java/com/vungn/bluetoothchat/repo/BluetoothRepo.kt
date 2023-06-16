package com.vungn.bluetoothchat.repo

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.vungn.bluetoothchat.data.ConnectionResult
import com.vungn.bluetoothchat.receiver.BluetoothReceiver
import com.vungn.bluetoothchat.util.BluetoothHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothRepo constructor(private val context: Context) : BluetoothHelper {
    private val bluetoothReceiverListener = object : BluetoothReceiver.BluetoothReceiverListener {
        override fun onStateChange(state: Int) {
            _state.value = state
        }

        override fun onFoundDevice(device: BluetoothDevice) {
            _scannedDevices.update { devices ->
                devices + device
            }
        }
    }

    private val bluetoothReceiver = BluetoothReceiver().also {
        it.bluetoothReceiverListener = bluetoothReceiverListener
    }
    private val _bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val _bluetoothAdapter by lazy {
        _bluetoothManager.adapter
    }
    private val _state =
        MutableStateFlow(if (isEnable()) BluetoothAdapter.STATE_ON else BluetoothAdapter.STATE_OFF)
    private val _scannedDevices = MutableStateFlow(setOf<BluetoothDevice>())
    private val _pairedDevices = MutableStateFlow(setOf<BluetoothDevice>())
    private var _currentServerSocket: BluetoothServerSocket? = null
    private var _currentClientSocket: BluetoothSocket? = null

    init {
        val intent = IntentFilter().also {
            it.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            it.addAction(BluetoothDevice.ACTION_FOUND)
        }
        context.registerReceiver(bluetoothReceiver, intent)
    }

    override val bluetoothManager: BluetoothManager
        get() = _bluetoothManager

    override val bluetoothAdapter: BluetoothAdapter
        get() = _bluetoothAdapter

    override val state: StateFlow<Int>
        get() = _state

    override val scannedDevices: StateFlow<Set<BluetoothDevice>>
        get() = _scannedDevices

    override val pairedDevices: StateFlow<Set<BluetoothDevice>>
        get() = _pairedDevices

    override fun isEnable(): Boolean = _bluetoothAdapter.isEnabled

    override fun startDiscovery() {
        _scannedDevices.update { setOf() }
        updatePairDevices()
        _bluetoothAdapter.startDiscovery()
    }

    override fun cancelDiscovery() {
        _bluetoothAdapter.cancelDiscovery()
    }

    override fun refreshDiscovery() {
        cancelDiscovery()
        startDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(bluetoothReceiver)
    }

    override fun startServer(): Flow<ConnectionResult> = flow {
        _bluetoothAdapter.cancelDiscovery()
        _currentServerSocket = _bluetoothAdapter.listenUsingRfcommWithServiceRecord(
            NAME, UUID_STRING
        )

        var shouldLoop = true
        while (shouldLoop) {
            _currentClientSocket = try {
                _currentServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                emit(ConnectionResult.Error(e.message.toString()))
                shouldLoop = false
                null
            }
            emit(ConnectionResult.ConnectionEstablished)
            _currentServerSocket?.also {
                _currentServerSocket?.close()
                shouldLoop = false
            }
        }
    }.onCompletion { closeConnections() }.flowOn(Dispatchers.IO)

    override fun connectToServer(device: BluetoothDevice): Flow<ConnectionResult> = flow {
        _bluetoothAdapter.cancelDiscovery()
        _currentClientSocket = _bluetoothAdapter?.getRemoteDevice(device.address)
            ?.createRfcommSocketToServiceRecord(UUID_STRING)

        _currentClientSocket?.let { socket ->
            try {
                socket.connect()
                emit(ConnectionResult.ConnectionEstablished)
            } catch (e: IOException) {
                Log.e(TAG, "connectToServer", e)
                socket.close()
                _currentClientSocket = null
                emit(ConnectionResult.Error(e.message.toString()))
            }
        }
    }.onCompletion { closeConnections() }.flowOn(Dispatchers.IO)

    private fun updatePairDevices() {
        _pairedDevices.update { _bluetoothAdapter.bondedDevices }
    }

    override fun closeConnections() {
        _currentServerSocket?.close()
        _currentClientSocket?.close()
        _currentServerSocket = null
        _currentClientSocket = null
    }

    companion object {
        private val TAG = BluetoothRepo::class.simpleName
        private const val NAME = "com.vungn.bluetoothchat.Name"
        private val UUID_STRING = UUID.fromString("27b7d1da-08c7-4505-a6d1-2459987e5e2d")
    }
}
