package io.neverdo.shared.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.neverdo.appdbsq.NeverdoDB

const val DB_NAME = "neverdo.db"
lateinit var db: NeverdoDB

suspend fun <T> dbIO(
    block: suspend CoroutineScope.() -> T
): T = withContext(Dispatchers.Default, block)
