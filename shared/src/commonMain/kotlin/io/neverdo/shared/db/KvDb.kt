package io.neverdo.shared.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dbsq.KVSQ
import io.neverdo.shared.getString
import io.neverdo.shared.lib.Backupable__Holder
import io.neverdo.shared.lib.Backupable__Item
import io.neverdo.shared.toJsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

data class KvDb(
    val k: String,
    val v: String,
) : Backupable__Item {

    companion object Companion : Backupable__Holder {

        suspend fun select(): List<KvDb> = dbIO {
            db.kVQueries.select().toModels()
        }

        fun selectFlow(): Flow<List<KvDb>> = db.kVQueries.select()
            .asFlow().mapToList(Dispatchers.IO).map { list -> list.map { it.toModel() } }

        suspend fun upsert(k: String, v: String) = dbIO {
            db.kVQueries.upsert(KVSQ(k = k, v = v))
        }

        //
        // Backupable Holder

        override fun backupable__getAll(): List<Backupable__Item> =
            db.kVQueries.select().toModels()

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.kVQueries.upsert(
                KVSQ(
                    k = j.getString(0),
                    v = j.getString(1),
                )
            )
        }
    }

    //
    // Backupable Item

    override fun backupable__getId(): String = k

    override fun backupable__backup(): JsonElement = listOf(
        k, v,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        val j = json.jsonArray
        db.kVQueries.upsert(
            KVSQ(
                k = j.getString(0),
                v = j.getString(1),
            )
        )
    }

    override fun backupable__delete() {
        db.kVQueries.deleteByKey(k)
    }
}

private fun KVSQ.toModel() = KvDb(
    k = k, v = v
)

private fun Query<KVSQ>.toModels(): List<KvDb> =
    executeAsList().map { it.toModel() }
