package io.neverdo.jvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.vm.BoardVM

private val horizontalPadding = 8.dp

@Composable
fun BoardView(
    board: BoardDb,
) {

    val (vm, state) = rememberVM(board) { BoardVM(board) }

    VStack {

        ZStack(
            modifier = Modifier
                .padding(top = 12.dp, start = horizontalPadding),
        ) {

            if (state.isFormBoardVisible) {

                HStack(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    MyTextField(
                        value = state.formBoardText,
                        onValueChange = {
                            vm.setFormBoardText(it)
                        },
                        modifier = Modifier
                            .width(150.dp),
                        placeholder = "Board Name",
                    )

                    Text(
                        text = "Save",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(squircleShape)
                            .background(blueColor)
                            .clickable {
                                vm.submitFormBoard()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                    )

                    Text(
                        text = "Cancel",
                        modifier = Modifier
                            .clickable {
                                vm.setIsFormBoardVisible(false)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 13.sp,
                        color = Color.Gray,
                    )

                    Text(
                        text = "Delete Board",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(squircleShape)
                            .background(Color.Red)
                            .clickable {
                                vm.deleteBoard()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                    )
                }
            } else {
                Text(
                    text = state.board.name,
                    modifier = Modifier
                        .clip(squircleShape)
                        .clickable {
                            vm.setIsFormBoardVisible(true)
                        }
                        .padding(horizontal = horizontalPadding, vertical = 4.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        HStack(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(top = 8.dp, end = 500.dp)
        ) {

            state.lists.forEach { list ->
                ListView(list)
            }

            ZStack(
                modifier = Modifier
                    .padding(start = 12.dp)
            ) {
                if (state.isFormAddListVisible) {
                    ListFormView(
                        list = null,
                        board = board,
                        modifier = Modifier
                            .width(ListView__width)
                            .padding(top = 8.dp),
                        onCancel = { vm.setIsFormAddListVisible(false) },
                        onSave = { vm.setIsFormAddListVisible(false) },
                    )
                } else {
                    Icon(
                        Icons.Rounded.AddCircleOutline,
                        contentDescription = "Add List",
                        tint = Color.Gray,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(22.dp)
                            .clip(roundedShape)
                            .clickable {
                                vm.setIsFormAddListVisible(true)
                            },
                    )
                }
            }
        }
    }
}
