package com.vungn.bluetoothchat.ui.screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vungn.bluetoothchat.ui.MainActivity.Companion.TAG
import com.vungn.bluetoothchat.vm.BluetoothViewModel

@Composable
fun RequirementChecklist(
    modifier: Modifier = Modifier,
    viewModel: BluetoothViewModel,
    navigateToHome: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var areGranted by remember { mutableStateOf(false) }
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    val permissionsRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            areGranted = result.values.all { true }
            Log.d(TAG, "Are all permissions granted: $areGranted")
        })
    val bluetoothRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {})

    LaunchedEffect(key1 = true, block = {
        permissionsRequest.launch(permissions)
        val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothRequest.launch(bluetoothIntent)
    })

    LaunchedEffect(key1 = state, key2 = areGranted,
        block = {
            if (state == BluetoothAdapter.STATE_ON && areGranted) {
                navigateToHome()
            }
        })

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp), verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You need to",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight(500)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Accept all permissions", style = MaterialTheme.typography.bodyMedium)
            if (areGranted) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(onClick = { permissionsRequest.launch(permissions) }) {
                    Text(text = "Request")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Turn on bluetooth", style = MaterialTheme.typography.bodyMedium)
            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                BluetoothAdapter.STATE_OFF -> {
                    Button(onClick = {
                        val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothRequest.launch(bluetoothIntent)
                    }) {
                        Text(text = "Turn on")
                    }
                }
            }
        }
    }
}
