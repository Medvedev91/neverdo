package io.neverdo.jvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.vm.ListFormVM

@Composable
fun ListFormView(
    list: ListDb?,
    board: BoardDb,
    modifier: Modifier,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {

    val (vm, state) = rememberVM(list) { ListFormVM(list, board) }

    VStack(
        modifier = modifier,
    ) {

        MyTextField(
            value = state.inputText,
            onValueChange = {
                vm.setInputText(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = "List Name",
        )

        HStack(
            modifier = Modifier
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = "Save",
                modifier = Modifier
                    .clip(squircleShape)
                    .background(blueColor)
                    .clickable {
                        vm.submitForm {
                            onSave()
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 13.sp,
            )

            Text(
                text = "Cancel",
                modifier = Modifier
                    .clickable {
                        onCancel()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 13.sp,
                color = Color.Gray,
            )

            if (list != null)
                Text(
                    text = "Delete List",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(squircleShape)
                        .background(Color.Red)
                        .clickable {
                            vm.deleteList()
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 13.sp,
                )
        }
    }
}
