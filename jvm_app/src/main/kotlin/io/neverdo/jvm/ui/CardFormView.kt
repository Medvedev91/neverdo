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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.neverdo.shared.db.CardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.vm.CardFormVM

@Composable
fun CardFormView(
    card: CardDb?,
    listToAdd: ListDb,
    modifier: Modifier,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {

    val (vm, state) = rememberVM(card, listToAdd) {
        CardFormVM(card, listToAdd = listToAdd)
    }

    fun submitForm() {
        vm.submitForm {
            onSave()
        }
    }

    VStack(
        modifier = modifier,
    ) {

        MyTextField(
            value = state.inputText,
            onValueChange = {
                vm.setInputText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    if (
                        (it.key == Key.Enter) &&
                        (it.type == KeyEventType.KeyDown)
                    ) {
                        if (it.isShiftPressed)
                            vm.appendInputTextEnter()
                        else
                            submitForm()
                        true
                    } else {
                        false
                    }
                },
            placeholder = "Text",
        )

        if (card != null) {

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
                            submitForm()
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

                Text(
                    text = "Delete",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(squircleShape)
                        .background(Color.Red)
                        .clickable {
                            vm.deleteCard()
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 13.sp,
                )
            }
        }
    }
}
