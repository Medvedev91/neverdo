package io.neverdo.jvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.neverdo.shared.vm.MainVM

private val menuItemInnerPadding = 8.dp

@Composable
fun MainView() {

    val (vm, state) = rememberVM { MainVM() }

    HStack(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        VStack(
            modifier = Modifier
                .width(200.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .padding(horizontal = 8.dp),
        ) {

            state.boards.forEach { board ->
                MenuItemView(board.name) {
                    vm.selectBoard(board)
                }
            }

            Icon(
                Icons.Rounded.AddCircleOutline,
                contentDescription = "Add Board",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(start = 4.dp, top = 8.dp)
                    .size(22.dp)
                    .clip(roundedShape)
                    .clickable {
                        vm.newBoard()
                    },
            )

            ZStack(Modifier.weight(1f))

            MenuItemView("Backup") {
                vm.backup()
            }

            MenuItemView("Restore") {
                vm.restore()
            }
        }

        ZStack(
            modifier = Modifier
                .fillMaxHeight()
                .width(0.5.dp)
                .background(Color.LightGray),
        )

        val selectedBoard = state.selectedBoard
        if (selectedBoard != null)
            BoardView(selectedBoard)
    }
}

@Composable
private fun MenuItemView(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(squircleShape)
            .clickable {
                onClick()
            }
            .padding(menuItemInnerPadding),
    )
}
