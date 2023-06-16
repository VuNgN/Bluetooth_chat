package com.vungn.bluetoothchat.receiver

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

@SuppressLint("MissingPermission")
class BluetoothReceiver : BroadcastReceiver() {
    private var _bluetoothReceiverListener: BluetoothReceiverListener? = null
    var bluetoothReceiverListener: BluetoothReceiverListener?
        get() = _bluetoothReceiverListener
        set(value) {
            _bluetoothReceiverListener = value
        }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state: Int = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR
                )
                _bluetoothReceiverListener?.onStateChange(state)
            }

            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java
                        )
                    } else {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                if (device != null) {
                    Log.d(TAG, "Scanned device: ${device.name}")
                    _bluetoothReceiverListener?.onFoundDevice(device)
                }
            }
        }
    }

    interface BluetoothReceiverListener {
        fun onStateChange(state: Int)
        fun onFoundDevice(device: BluetoothDevice)
    }

    companion object {
        private val TAG = BluetoothReceiver::class.simpleName
    }
}