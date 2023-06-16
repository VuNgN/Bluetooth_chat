package com.vungn.bluetoothchat.ui.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vungn.bluetoothchat.ui.screen.Chat
import com.vungn.bluetoothchat.ui.screen.Home
import com.vungn.bluetoothchat.ui.screen.RequirementChecklist
import com.vungn.bluetoothchat.util.NavRoute
import com.vungn.bluetoothchat.vm.BluetoothViewModel
import com.vungn.bluetoothchat.vm.impl.BluetoothViewModelImpl

@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    val vm: BluetoothViewModel = viewModel<BluetoothViewModelImpl>()
    NavHost(
        navController = navController, startDestination = NavRoute.REQUIRE_CHECKLIST_ROUTE.name
    ) {
        composable(NavRoute.REQUIRE_CHECKLIST_ROUTE.name) {
            RequirementChecklist(viewModel = vm,
                navigateToHome = { navController.navigate(NavRoute.HOME_ROUTE.name) })
        }
        composable(NavRoute.HOME_ROUTE.name) {
            Home(viewModel = vm,
                navigateToChat = { navController.navigate(NavRoute.CHAT_ROUTE.name) })
        }
        composable(NavRoute.CHAT_ROUTE.name) {
            Chat(viewModel = vm, navigateBack = { navController.popBackStack() })
        }
    }
}
