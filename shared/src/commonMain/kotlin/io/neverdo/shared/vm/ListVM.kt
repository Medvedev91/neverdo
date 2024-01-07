package io.neverdo.shared.vm

import io.neverdo.shared.db.CardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.launchExDefault
import io.neverdo.shared.onEachExIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ListVM(
    private val list: ListDb,
) : __VM<ListVM.State>() {

    data class CardUI(
        val card: CardDb,
        val isEditable: Boolean,
    )

    data class State(
        val list: ListDb,
        private val cards: List<CardDb>,
        val editableCardId: Int?,
        val isEditFormPresented: Boolean,
    ) {
        val board = list.getBoardDI()
        val cardsUI = cards.map { card -> CardUI(card, card.id == editableCardId) }
    }

    override val state = MutableStateFlow(
        State(
            list = list,
            cards = listOf(), // todo init data
            editableCardId = null,
            isEditFormPresented = false,
        )
    )

    override fun onAppear() {
        val scope = scopeVM()
        CardDb.selectBySortAscFlow().onEachExIn(scope) { cards ->
            // todo no filter
            state.update { it.copy(cards = cards.filter { it.list_id == list.id }) }
        }
    }

    fun setIsEditFormPresented(isPresented: Boolean) {
        state.update { it.copy(isEditFormPresented = isPresented) }
    }

    fun setEditableCard(cardUI: CardUI?) {
        state.update { it.copy(editableCardId = cardUI?.card?.id) }
    }

    fun addCard(
        text: String,
        onSuccess: () -> Unit,
    ) {
        launchExDefault {
            CardDb.insertWithValidation(
                text = text,
                list = state.value.list,
            )
            onSuccess()
        }
    }
}
