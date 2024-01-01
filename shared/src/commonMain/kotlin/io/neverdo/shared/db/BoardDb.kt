package io.neverdo.shared.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dbsq.BoardSQ
import io.neverdo.shared.*
import io.neverdo.shared.lib.Backupable__Holder
import io.neverdo.shared.lib.Backupable__Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

data class BoardDb(
    val id: Int,
    val time: Int,
    val sort: Int,
    val name: String,
) : Backupable__Item {

    companion object Companion : Backupable__Holder {

        suspend fun selectBySortAsc(): List<BoardDb> = dbIO {
            db.boardQueries.selectBySortAsc().toModels()
        }

        fun selectBySortAscFlow(): Flow<List<BoardDb>> = db.boardQueries.selectBySortAsc()
            .asFlow().mapToList(Dispatchers.IO).map { list -> list.map { it.toModel() } }

        suspend fun insertWithValidation(
            name: String,
        ): BoardDb = dbIO {
            db.transactionWithResult {

                val allBoards = db.boardQueries.selectBySortAsc().toModels()
                val nameValidated = nameValidation(name, allBoards)

                val maxId = allBoards.maxOfOrNull { it.id } ?: 0
                val boardSq = BoardSQ(
                    id = maxId + 1,
                    time = time(),
                    sort = 0,
                    name = nameValidated,
                )
                db.boardQueries.insert(boardSq)

                boardSq.toModel()
            }
        }

        //
        // Backupable Holder

        override fun backupable__getAll(): List<Backupable__Item> =
            db.boardQueries.selectBySortAsc().toModels()

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.boardQueries.insert(
                BoardSQ(
                    id = j.getInt(0),
                    time = j.getInt(1),
                    sort = j.getInt(2),
                    name = j.getString(3),
                )
            )
        }
    }

    suspend fun updateByIdWithValidation(name: String) = dbIO {
        db.transaction {
            val allBoards = db.boardQueries.selectBySortAsc().toModels()
            val nameValidated = nameValidation(name, allBoards.filter { it.id != id })
            db.boardQueries.updateById(
                id = id,
                time = time,
                sort = sort,
                name = nameValidated,
            )
        }
    }

    // todo remove
    suspend fun deleteWithDependencies() = dbIO {
        ListDb.selectBySortAsc()
            .filter { it.board_id == id }
            .forEach { it.deleteWithDependencies() }
        backupable__delete()
    }

    //
    // Backupable Item

    override fun backupable__getId(): String = id.toString()

    override fun backupable__backup(): JsonElement = listOf(
        id, time, sort, name,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        val j = json.jsonArray
        db.boardQueries.updateById(
            id = j.getInt(0),
            time = j.getInt(1),
            sort = j.getInt(2),
            name = j.getString(3),
        )
    }

    override fun backupable__delete() {
        db.boardQueries.deleteById(id)
    }
}

private fun nameValidation(
    name: String,
    allBoards: List<BoardDb>,
): String {

    val nameValidated = name.trim()
    if (nameValidated.isBlank())
        throw UIException("Empty board name")

    if (allBoards.any { it.name.lowercase() == nameValidated.lowercase() })
        throw UIException("$nameValidated already exists")

    return nameValidated
}

private fun BoardSQ.toModel() = BoardDb(
    id = id, time = time, sort = sort, name = name,
)

private fun Query<BoardSQ>.toModels(): List<BoardDb> =
    executeAsList().map { it.toModel() }
