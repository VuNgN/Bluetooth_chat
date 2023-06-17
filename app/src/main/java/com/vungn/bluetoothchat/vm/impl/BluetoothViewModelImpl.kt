package com.vungn.bluetoothchat.vm.impl

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vungn.bluetoothchat.data.BluetoothData
import com.vungn.bluetoothchat.data.ConnectionResult
import com.vungn.bluetoothchat.util.BluetoothHelper
import com.vungn.bluetoothchat.vm.BluetoothViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModelImpl @Inject constructor(private val bluetoothHelper: BluetoothHelper) :
    ViewModel(), BluetoothViewModel {
    private val _state = bluetoothHelper.state
    private val _isEnable = MutableStateFlow(bluetoothHelper.isEnable())
    private val _data = MutableStateFlow(BluetoothData())
    private val _errorMessage = MutableStateFlow<String?>(null)
    private var deviceConnectionJob: Job? = null

    override val state: StateFlow<Int>
        get() = _state

    override val isEnable: StateFlow<Boolean>
        get() = _isEnable

    override val data = combine(
        bluetoothHelper.scannedDevices, bluetoothHelper.pairedDevices, _data
    ) { scannedDevices, pairedDevices, data ->
        data.copy(
            scannedDevices = scannedDevices, pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _data.value)

    override val errorMessage: StateFlow<String?>
        get() = _errorMessage

    override fun scanBluetooth() {
        bluetoothHelper.startDiscovery()
    }

    override fun refresh() {
        bluetoothHelper.refreshDiscovery()
    }

    override fun launchServer() {
        _data.update { bluetoothData -> bluetoothData.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothHelper.startServer().listen()
    }

    override fun connectToServer(device: BluetoothDevice) {
        deviceConnectionJob = bluetoothHelper.connectToServer(device).listen()
    }

    override fun disconnect() {
        deviceConnectionJob?.cancel()
        bluetoothHelper.closeConnections()
        _data.update { bluetoothData ->
            bluetoothData.copy(isConnecting = false, isConnected = false)
        }
    }

    override fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val bluetoothMessage = bluetoothHelper.tryToSendMessage(message)
            if (bluetoothMessage != null) {
                _data.update { bluetoothData ->
                    bluetoothData.copy(messages = bluetoothData.messages + bluetoothMessage)
                }
            } else {
                _errorMessage.emit("Message sending failed")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothHelper.release()
        Log.d(TAG, "onCleared: BluetoothHelper is released")
    }

    private fun Flow<ConnectionResult>.listen(): Job = onEach { result ->
        when (result) {
            is ConnectionResult.ConnectionEstablished -> {
                Log.d(TAG, "Server connect success")
                _data.update { bluetoothData ->
                    bluetoothData.copy(isConnected = true, isConnecting = false)
                }
            }

            is ConnectionResult.TransferSuccess -> {
                Log.d(TAG, "Message transferring success")
                _data.update { bluetoothData ->
                    bluetoothData.copy(messages = bluetoothData.messages + result.message)
                }
            }

            is ConnectionResult.Error -> {
                Log.e(TAG, "Server connect failure")
                _data.update { bluetoothData ->
                    bluetoothData.copy(isConnected = false, isConnecting = false)
                }
                _errorMessage.emit(result.message)
            }
        }
    }.catch { e ->
        Log.e(TAG, "Job flow ConnectionResult failure", e)
        _errorMessage.emit(e.message)
        bluetoothHelper.closeConnections()
        _data.update { bluetoothData ->
            bluetoothData.copy(isConnected = false, isConnecting = false)
        }
    }.launchIn(viewModelScope)

    companion object {
        private val TAG = BluetoothViewModelImpl::class.simpleName
    }
}