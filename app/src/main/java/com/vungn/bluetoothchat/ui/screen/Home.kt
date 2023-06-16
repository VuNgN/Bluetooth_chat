package com.vungn.bluetoothchat.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vungn.bluetoothchat.data.BluetoothData
import com.vungn.bluetoothchat.vm.BluetoothViewModel

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier, viewModel: BluetoothViewModel, navigateToChat: () -> Unit
) {
    val data by viewModel.data.collectAsState(initial = BluetoothData())
    val errorMessage by viewModel.errorMessage.collectAsState()
    val backgroundColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background
    else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    val listColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    else MaterialTheme.colorScheme.background
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true, block = {
        viewModel.scanBluetooth()
    })
    LaunchedEffect(key1 = errorMessage, block = {
        if (errorMessage != null) {
            snackBarHostState.showSnackbar(errorMessage.toString())
        }
    })
    LaunchedEffect(key1 = data.isConnected, block = {
        if (data.isConnected) {
            navigateToChat()
        }
    })

    if (data.isConnecting) {
        Scaffold(
            modifier = modifier.fillMaxSize(), floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.disconnect() },
                    shape = MaterialTheme.shapes.small.copy(all = CornerSize(50.dp)),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel, contentDescription = "Cancel connection"
                    )
                }
            }, floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(text = "Waiting for a client ...")
                    CircularProgressIndicator()
                }
            }

        }
    } else {
        Scaffold(modifier = modifier.fillMaxSize(), floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { viewModel.launchServer() },
                text = { Text(modifier = Modifier, text = "Launch server") },
                icon = { Icon(imageVector = Icons.Rounded.Public, contentDescription = null) })
        }, floatingActionButtonPosition = FabPosition.End, snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)
            ) {
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Paired devices",
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    itemsIndexed(data.pairedDevices.toList()) { index, pairedDevice ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(listColor)
                        ) {
                            ListItem(modifier = Modifier
                                .background(listColor)
                                .clickable { viewModel.connectToServer(pairedDevice) },
                                colors = ListItemDefaults.colors(containerColor = listColor),
                                headlineText = { Text(text = pairedDevice.name ?: "Unknown name") },
                                supportingText = { Text(text = pairedDevice.address) })
                            if (index < data.pairedDevices.size - 1) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Scanned devices",
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Refresh scanned devices"
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    itemsIndexed(data.scannedDevices.toList()) { index, scannedDevice ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(listColor)
                        ) {
                            ListItem(modifier = Modifier
                                .background(listColor)
                                .clickable { viewModel.connectToServer(scannedDevice) },
                                colors = ListItemDefaults.colors(containerColor = listColor),
                                headlineText = {
                                    Text(
                                        text = scannedDevice.name ?: "Unknown name"
                                    )
                                },
                                supportingText = { Text(text = scannedDevice.address) })
                            if (index < data.scannedDevices.size - 1) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}