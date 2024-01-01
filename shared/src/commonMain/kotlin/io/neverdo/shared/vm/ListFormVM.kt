package io.neverdo.shared.vm

import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.launchExDefault
import io.neverdo.shared.uiConfirmation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ListFormVM(
    private val list: ListDb?,
    private val boardToAdd: BoardDb,
) : __VM<ListFormVM.State>() {

    data class State(
        val inputText: String,
        val list: ListDb?
    )

    override val state = MutableStateFlow(
        State(
            inputText = list?.name ?: "",
            list = list,
        )
    )

    fun setInputText(text: String) {
        state.update { it.copy(inputText = text) }
    }

    fun submitForm(
        onSuccess: () -> Unit,
    ) {
        launchExDefault {
            val list = list
            if (list != null) {
                list.updateByIdWithValidation(state.value.inputText)
            } else {
                ListDb.insertWithValidation(
                    name = state.value.inputText,
                    board = boardToAdd,
                )
                setInputText("")
            }
            onSuccess()
        }
    }

    fun deleteList() {
        uiConfirmation(
            title = "Warning",
            message = "Are you sure?",
            onConfirm = {
                launchExDefault {
                    if (list == null)
                        throw Exception("Error")
                    list.deleteWithDependencies()
                }
            },
        )
    }
}
