package io.neverdo.shared.lib

import io.neverdo.shared.db.*
import io.neverdo.shared.toHms
import kotlinx.serialization.json.*

object Backup {

    suspend fun create(
        type: String,
    ): String {

        val map: Map<String, JsonElement> = mapOf(
            // Meta
            "version" to JsonPrimitive(1),
            "type" to JsonPrimitive(type),
            // Database
            "boards" to BoardDb.selectBySortAsc().modelsToJsonArray(),
            "lists" to ListDb.selectBySortAsc().modelsToJsonArray(),
            "cards" to CardDb.selectBySortAsc().modelsToJsonArray(),
            "kv" to KvDb.select().modelsToJsonArray(),
        )

        return JsonObject(map).toString()
    }

    @Throws(Exception::class)
    suspend fun restore(jString: String) {
        db.transaction {

            val json = Json.parseToJsonElement(jString)

            db.cardQueries.truncate()
            db.listQueries.truncate()
            db.boardQueries.truncate()
            db.kVQueries.truncate()

            json.mapJsonArray("boards") { BoardDb.backupable__restore(it) }
            json.mapJsonArray("lists") { ListDb.backupable__restore(it) }
            json.mapJsonArray("cards") { CardDb.backupable__restore(it) }
            json.mapJsonArray("kv") { KvDb.backupable__restore(it) }
        }
    }

    fun prepFileName(unixTime: UnixTime, prefix: String): String {
        val year = unixTime.year().toString()
        val month = unixTime.month().toString().padStart(2, '0')
        val day = unixTime.dayOfMonth().toString().padStart(2, '0')
        val (h, m, s) = (unixTime.utcTime() % 86_400).toHms()
            .map { it.toString().padStart(2, '0') }
        return "${prefix}${year}_${month}_${day}_${h}_${m}_${s}.json"
    }
}

private inline fun JsonElement.mapJsonArray(
    key: String,
    block: (JsonArray) -> Unit,
) {
    this.jsonObject[key]!!.jsonArray.forEach { block(it.jsonArray) }
}

private fun List<Backupable__Item>.modelsToJsonArray() =
    JsonArray(this.map { it.backupable__backup() })
