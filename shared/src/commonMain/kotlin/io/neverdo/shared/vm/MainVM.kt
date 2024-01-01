package io.neverdo.shared.vm

import io.neverdo.shared.*
import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.lib.Backup
import io.neverdo.shared.lib.UnixTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainVM : __VM<MainVM.State>() {

    data class State(
        val boards: List<BoardDb>,
        val selectedBoard: BoardDb?,
    )

    override val state = MutableStateFlow(
        State(
            boards = DI.boards,
            selectedBoard = null,
        )
    )

    override fun onAppear() {
        val scope = scopeVM()
        BoardDb.selectBySortAscFlow().onEachExIn(scope) { boards ->
            state.update { state ->
                // Update selected board on change
                val selectedBoard = state.selectedBoard
                val newSelectedBoard =
                    if (selectedBoard == null) null
                    else boards.firstOrNull { selectedBoard.id == it.id }
                state.copy(
                    boards = boards,
                    selectedBoard = newSelectedBoard,
                )
            }
        }
    }

    fun selectBoard(board: BoardDb) {
        state.update { it.copy(selectedBoard = board) }
    }

    //
    // Form

    fun newBoard() {
        launchExDefault {
            val defaultName = "New Board"
            val lastNum: Int? = BoardDb
                .selectBySortAsc()
                .mapNotNull { board ->
                    if (board.name == defaultName)
                        1
                    else {
                        "^$defaultName\\s(\\d+)$"
                            .toRegex(RegexOption.IGNORE_CASE)
                            .findAll(board.name)
                            .map { it.groupValues[1] }
                            .toList()
                            .firstOrNull()
                            ?.toInt()
                    }
                }
                .maxOfOrNull { it }
            val newName = if (lastNum == null)
                defaultName
            else
                "$defaultName ${lastNum + 1}"
            BoardDb.insertWithValidation(newName)
        }
    }

    ////

    fun backup() {
        launchExDefault {
            try {
                val fileName = Backup.prepFileName(UnixTime(), prefix = "neverdo_")
                val fileContent = Backup.create("manual")
                uiSaveFilePicker(
                    windowTitle = "Backup",
                    defFileName = fileName,
                    fileContent = fileContent,
                )
            } catch (e: Throwable) {
                // todo report
                uiAlert("Backup error: $e")
            }
        }
    }

    fun restore() {
        try {
            uiReadFilePicker(
                windowTitle = "Restore",
                onFileRead = { fileContent ->
                    launchExDefault {
                        Backup.restore(fileContent)
                    }
                }
            )
        } catch (e: Throwable) {
            // todo report
            uiAlert("Restore error: $e")
        }
    }
}
