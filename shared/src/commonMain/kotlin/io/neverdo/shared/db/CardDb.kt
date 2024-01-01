package io.neverdo.shared.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dbsq.CardSQ
import io.neverdo.shared.*
import io.neverdo.shared.lib.Backupable__Holder
import io.neverdo.shared.lib.Backupable__Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

data class CardDb(
    val id: Int,
    val time: Int,
    val sort: Int,
    val list_id: Int,
    val text: String,
) : Backupable__Item {

    companion object Companion : Backupable__Holder {

        suspend fun selectBySortAsc(): List<CardDb> = dbIO {
            db.cardQueries.selectBySortAsc().toModels()
        }

        fun selectBySortAscFlow(): Flow<List<CardDb>> = db.cardQueries.selectBySortAsc()
            .asFlow().mapToList(Dispatchers.IO).map { list -> list.map { it.toModel() } }

        suspend fun insertWithValidation(
            text: String,
            list: ListDb,
        ): CardDb = dbIO {
            db.transactionWithResult {

                val textValidated = textValidate(text)

                // todo by sql query
                val maxId = db.cardQueries.selectBySortAsc().toModels().maxOfOrNull { it.id } ?: 0
                val cardSq = CardSQ(
                    id = maxId + 1,
                    time = time(),
                    sort = 0,
                    list_id = list.id,
                    text = textValidated,
                )
                db.cardQueries.insert(cardSq)

                cardSq.toModel()
            }
        }

        //
        // Backupable Holder

        override fun backupable__getAll(): List<Backupable__Item> =
            db.cardQueries.selectBySortAsc().toModels()

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.cardQueries.insert(
                CardSQ(
                    id = j.getInt(0),
                    time = j.getInt(1),
                    sort = j.getInt(2),
                    list_id = j.getInt(3),
                    text = j.getString(4),
                )
            )
        }
    }

    suspend fun updateByIdWithValidation(text: String) {
        dbIO {
            db.cardQueries.updateById(
                id = id,
                time = time,
                sort = sort,
                list_id = list_id,
                text = textValidate(text),
            )
        }
    }

    //
    // Backupable Item

    override fun backupable__getId(): String = id.toString()

    override fun backupable__backup(): JsonElement = listOf(
        id, time, sort, list_id, text,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        val j = json.jsonArray
        db.cardQueries.updateById(
            id = j.getInt(0),
            time = j.getInt(1),
            sort = j.getInt(2),
            list_id = j.getInt(3),
            text = j.getString(4),
        )
    }

    override fun backupable__delete() {
        db.cardQueries.deleteById(id)
    }
}

private fun textValidate(text: String): String {
    val textValidated = text.trim()
    if (textValidated.isBlank())
        throw UIException("Empty card text")
    return textValidated
}

private fun CardSQ.toModel() = CardDb(
    id = id, time = time, sort = sort, list_id = list_id, text = text
)

private fun Query<CardSQ>.toModels(): List<CardDb> =
    executeAsList().map { it.toModel() }
