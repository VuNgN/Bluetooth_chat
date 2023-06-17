package com.vungn.bluetoothchat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.vungn.bluetoothchat.data.BluetoothData
import com.vungn.bluetoothchat.vm.BluetoothViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Chat(
    modifier: Modifier = Modifier, viewModel: BluetoothViewModel, navigateBack: () -> Boolean
) {
    val data by viewModel.data.collectAsState(initial = BluetoothData())
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lazyListState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(key1 = data.messages, block = {
        if (data.messages.isNotEmpty()) {
            lazyListState.scrollToItem(data.messages.size - 1)
        }
    })
    LaunchedEffect(key1 = errorMessage, block = {
        if (errorMessage != null) {
            snackBarHostState.showSnackbar(errorMessage.toString())
        }
    })

    Scaffold(modifier = modifier
        .fillMaxSize()
        .clickable(indication = null, interactionSource = interactionSource) { kc?.hide() },
        topBar = {
            TopAppBar(title = { Text(text = "Chat") }, navigationIcon = {
                IconButton(onClick = {
                    viewModel.disconnect()
                    navigateBack()
                }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Go back")
                }
            })
        },
        bottomBar = {
            TextField(modifier = Modifier.fillMaxWidth(),
                value = message,
                onValueChange = { message = it },
                placeholder = { Text(text = "Message") },
                leadingIcon = {
                    Row {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Rounded.Image,
                                contentDescription = "Choose images"
                            )
                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.sendMessage(message = message)
                        message = ""
                        kc?.hide()
                        focusManager.clearFocus()
                    }) {
                        Icon(imageVector = Icons.Rounded.Send, contentDescription = "Send message")
                    }
                })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(modifier = Modifier.padding(12.dp), action = {
                    TextButton(onClick = {
                        viewModel.disconnect()
                        navigateBack()
                    }) {
                        Text(text = "Disconnect")
                    }
                }) {
                    Text(text = data.visuals.message)
                }
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = lazyListState
            ) {
                items(data.messages) { message ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(
                                    shape = if (message.isFromLocal) RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = 0.dp
                                    )
                                    else RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 20.dp
                                    )
                                )
                                .background(
                                    if (message.isFromLocal) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                                .padding(10.dp)
                                .align(if (message.isFromLocal) Alignment.End else Alignment.Start),
                            horizontalAlignment = if (message.isFromLocal) Alignment.End else Alignment.Start
                        ) {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                text = message.sender,
                                color = if (message.isFromLocal) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = message.message,
                                color = if (message.isFromLocal) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}