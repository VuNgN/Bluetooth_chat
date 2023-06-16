package com.vungn.bluetoothchat.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vungn.bluetoothchat.vm.BluetoothViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    modifier: Modifier = Modifier, viewModel: BluetoothViewModel, navigateBack: () -> Boolean
) {
    var message by remember { mutableStateOf("") }
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Chat") }, navigationIcon = {
            IconButton(onClick = {
                viewModel.disconnect()
                navigateBack()
            }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Go back")
            }
        })
    }, bottomBar = {
        TextField(modifier = Modifier.fillMaxWidth(),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text(text = "Message") },
            leadingIcon = {
                Row {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.Image, contentDescription = "Choose images"
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Rounded.Mood, contentDescription = "Choose emojis")
                    }
                }
            },
            trailingIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Rounded.Send, contentDescription = "Send message")
                }
            })

    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

        }
    }
}