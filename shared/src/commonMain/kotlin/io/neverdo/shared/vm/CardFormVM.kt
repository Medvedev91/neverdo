package io.neverdo.shared.vm

import io.neverdo.shared.db.CardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.launchExDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CardFormVM(
    private val card: CardDb?,
    private val listToAdd: ListDb,
) : __VM<CardFormVM.State>() {

    data class State(
        val card: CardDb?,
        val inputText: String,
    )

    override val state = MutableStateFlow(
        State(
            card = card,
            inputText = card?.text ?: "",
        )
    )

    fun setInputText(text: String) {
        state.update { it.copy(inputText = text) }
    }

    fun appendInputTextEnter() {
        state.update {
            val newText = it.inputText + "\n"
            it.copy(inputText = newText)
        }
    }

    fun submitForm(
        onSuccess: () -> Unit,
    ) {
        launchExDefault {
            val card = card
            if (card != null) {
                card.updateByIdWithValidation(state.value.inputText)
            } else {
                CardDb.insertWithValidation(
                    text = state.value.inputText,
                    list = listToAdd,
                )
                state.update { it.copy(inputText = "") }
            }
            onSuccess()
        }
    }

    fun deleteCard() {
        launchExDefault {
            if (card == null)
                throw Exception("Error")
            card.backupable__delete()
        }
    }
}
