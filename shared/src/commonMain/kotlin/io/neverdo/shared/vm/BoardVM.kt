package io.neverdo.shared.vm

import io.neverdo.shared.DI
import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.db.ListDb
import io.neverdo.shared.launchExDefault
import io.neverdo.shared.onEachExIn
import io.neverdo.shared.uiConfirmation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class BoardVM(
    private val board: BoardDb,
) : __VM<BoardVM.State>() {

    data class State(
        val board: BoardDb,
        val lists: List<ListDb>,
        val isFormBoardVisible: Boolean,
        val formBoardText: String,
        val isFormAddListVisible: Boolean,
    )

    override val state = MutableStateFlow(
        State(
            board = board,
            lists = DI.lists.filterByBoard(board),
            isFormBoardVisible = false,
            formBoardText = "",
            isFormAddListVisible = false,
        )
    )

    override fun onAppear() {
        val scope = scopeVM()
        ListDb.selectBySortAscFlow().onEachExIn(scope) { lists ->
            state.update { it.copy(lists = lists.filterByBoard(board)) }
        }
    }

    //
    // Board's Form

    fun setIsFormBoardVisible(isVisible: Boolean) {
        state.update {
            it.copy(
                isFormBoardVisible = isVisible,
                formBoardText = board.name,
            )
        }
    }

    fun setFormBoardText(text: String) {
        state.update { it.copy(formBoardText = text) }
    }

    fun submitFormBoard() {
        launchExDefault {
            board.updateByIdWithValidation(state.value.formBoardText)
            setIsFormBoardVisible(false)
        }
    }

    fun deleteBoard() {
        uiConfirmation(
            title = "Warning",
            message = "Are you sure?",
            onConfirm = {
                launchExDefault {
                    board.deleteWithDependencies()
                }
            },
        )
    }

    //
    // Add List Form

    fun setIsFormAddListVisible(isVisible: Boolean) {
        state.update { it.copy(isFormAddListVisible = isVisible) }
    }

    ////
}

private fun List<ListDb>.filterByBoard(board: BoardDb) =
    this.filter { it.board_id == board.id }
