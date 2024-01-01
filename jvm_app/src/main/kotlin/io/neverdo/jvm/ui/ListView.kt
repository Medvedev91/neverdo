package io.neverdo.jvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.vm.ListVM

val ListView__width = 250.dp

@Composable
fun ListView(
    list: ListDb,
) {

    val (vm, state) = rememberVM(list) { ListVM(list) }

    VStack(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 200.dp)
            .width(ListView__width),
    ) {

        ZStack(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {

            if (state.isEditFormPresented) {
                ListFormView(
                    list = state.list,
                    board = state.board,
                    modifier = Modifier
                        .width(ListView__width)
                        .padding(top = 8.dp, bottom = 8.dp),
                    onCancel = { vm.setIsEditFormPresented(false) },
                    onSave = { vm.setIsEditFormPresented(false) },
                )
            } else {
                Text(
                    text = state.list.name,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(squircleShape)
                        .clickable {
                            vm.setIsEditFormPresented(true)
                        }
                        .padding(8.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        state.cardsUI.forEach { cardUI ->

            VStack {

                if (cardUI.isEditable) {
                    CardFormView(
                        card = cardUI.card,
                        listToAdd = state.list,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        onCancel = { vm.setEditableCard(null) },
                        onSave = { vm.setEditableCard(null) },
                    )
                } else {
                    Text(
                        text = cardUI.card.text,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .fillMaxWidth()
                            .clip(squircleShape)
                            .clickable {
                                vm.setEditableCard(cardUI)
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        fontSize = 14.sp,
                    )
                }

                if (state.cardsUI.last() != cardUI)
                    ZStack(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(0.5.dp)
                            .background(Color.LightGray)
                    )
            }
        }

        CardFormView(
            card = null,
            listToAdd = state.list,
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 12.dp),
            onCancel = {},
            onSave = {},
        )
    }
}
