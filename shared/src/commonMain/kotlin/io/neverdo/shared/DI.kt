package io.neverdo.shared

import io.neverdo.shared.db.BoardDb
import io.neverdo.shared.db.CardDb
import io.neverdo.shared.db.KvDb
import io.neverdo.shared.db.ListDb

object DI {

    var kv = listOf<KvDb>()
    var boards = listOf<BoardDb>()
    var lists = listOf<ListDb>()
    var cards = listOf<CardDb>()

    suspend fun init() {

        val scope = defaultScope()

        kv = KvDb.select()
        KvDb.selectFlow().onEachExIn(scope) { kv = it }

        boards = BoardDb.selectBySortAsc()
        BoardDb.selectBySortAscFlow().onEachExIn(scope) { boards = it }

        lists = ListDb.selectBySortAsc()
        ListDb.selectBySortAscFlow().onEachExIn(scope) { lists = it }

        cards = CardDb.selectBySortAsc()
        CardDb.selectBySortAscFlow().onEachExIn(scope) { cards = it }
    }
}
