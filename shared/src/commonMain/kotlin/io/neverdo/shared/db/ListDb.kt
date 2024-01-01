package io.neverdo.shared.db

import io.neverdo.shared.lib.Backupable__Holder
import io.neverdo.shared.lib.Backupable__Item
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dbsq.ListSQ
import io.neverdo.shared.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.coroutines.IO

data class ListDb(
    val id: Int,
    val time: Int,
    val sort: Int,
    val board_id: Int,
    val name: String,
) : Backupable__Item {

    companion object Companion : Backupable__Holder {

        suspend fun selectBySortAsc(): List<ListDb> = dbIO {
            db.listQueries.selectBySortAsc().toModels()
        }

        fun selectBySortAscFlow(): Flow<List<ListDb>> = db.listQueries.selectBySortAsc()
            .asFlow().mapToList(Dispatchers.IO).map { list -> list.map { it.toModel() } }

        suspend fun insertWithValidation(
            name: String,
            board: BoardDb,
        ): ListDb = dbIO {
            db.transactionWithResult {

                val allLists = db.listQueries.selectBySortAsc().toModels()
                val nameValidated = nameValidation(
                    name = name,
                    boardId = board.id,
                    allLists = allLists,
                )

                val maxId = allLists.maxOfOrNull { it.id } ?: 0
                val listSq = ListSQ(
                    id = maxId + 1,
                    time = time(),
                    sort = 0,
                    board_id = board.id,
                    name = nameValidated,
                )
                db.listQueries.insert(listSq)

                listSq.toModel()
            }
        }

        //
        // Backupable Holder

        override fun backupable__getAll(): List<Backupable__Item> =
            db.listQueries.selectBySortAsc().toModels()

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.listQueries.insert(
                ListSQ(
                    id = j.getInt(0),
                    time = j.getInt(1),
                    sort = j.getInt(2),
                    board_id = j.getInt(3),
                    name = j.getString(4),
                )
            )
        }
    }

    fun getBoardDI(): BoardDb = DI.boards.first { it.id == board_id }

    suspend fun updateByIdWithValidation(name: String) {
        dbIO {
            db.transaction {
                val allLists = db.listQueries.selectBySortAsc().toModels().filter { it.id != id }
                val nameValidated = nameValidation(
                    name = name,
                    boardId = board_id,
                    allLists = allLists,
                )
                db.listQueries.updateById(
                    id = id,
                    time = time,
                    sort = sort,
                    board_id = board_id,
                    name = nameValidated,
                )
            }
        }
    }

    // todo remove
    suspend fun deleteWithDependencies() = dbIO {
        CardDb.selectBySortAsc()
            .filter { it.list_id == id }
            .forEach {
                it.backupable__delete()
            }
        backupable__delete()
    }

    //
    // Backupable Item

    override fun backupable__getId(): String = id.toString()

    override fun backupable__backup(): JsonElement = listOf(
        id, time, sort, board_id, name,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        val j = json.jsonArray
        db.listQueries.updateById(
            id = j.getInt(0),
            time = j.getInt(1),
            sort = j.getInt(2),
            board_id = j.getInt(3),
            name = j.getString(4),
        )
    }

    override fun backupable__delete() {
        db.listQueries.deleteById(id)
    }
}

private fun nameValidation(
    name: String,
    boardId: Int,
    allLists: List<ListDb>,
): String {

    val nameValidated = name.trim()
    if (nameValidated.isBlank())
        throw UIException("Empty list name")

    if (allLists
            .filter { it.board_id == boardId }
            .any { it.name.lowercase() == nameValidated.lowercase() }
    )
        throw UIException("$nameValidated already exists")

    return nameValidated
}

private fun ListSQ.toModel() = ListDb(
    id = id, time = time, sort = sort, board_id = board_id, name = name
)

private fun Query<ListSQ>.toModels(): List<ListDb> =
    executeAsList().map { it.toModel() }
