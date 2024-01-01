package io.neverdo.jvm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.neverdo.jvm.ui.MainView
import io.neverdo.jvm.ui.VStack
import io.neverdo.jvm.ui.rememberVM
import io.neverdo.shared.initKmpJvm
import io.neverdo.shared.vm.AppVM

fun main() = application {

    initKmpJvm()

    Window(
        onCloseRequest = ::exitApplication,
        title = "NeverDo",
    ) {

        val (_, state) = rememberVM { AppVM() }

        MaterialTheme {

            VStack(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
            ) {

                if (state.isAppReady)
                    MainView()
            }
        }
    }
}
