package com.vungn.bluetoothchat.di

import android.content.Context
import com.vungn.bluetoothchat.repo.BluetoothRepo
import com.vungn.bluetoothchat.util.BluetoothHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun bluetoothRepoProvide(@ApplicationContext context: Context): BluetoothHelper =
        BluetoothRepo(context)
}