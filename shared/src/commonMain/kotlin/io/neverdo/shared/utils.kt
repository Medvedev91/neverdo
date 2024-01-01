package io.neverdo.shared

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import dbsq.BoardSQ
import dbsq.CardSQ
import dbsq.ListSQ
import io.neverdo.appdbsq.NeverdoDB
import io.neverdo.shared.db.db
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

expect val isDevEnvironment: Boolean

//
// UI Modals

expect fun uiAlert(
    message: String,
)

expect fun uiConfirmation(
    title: String,
    message: String,
    onConfirm: () -> Unit,
)

@Throws(Throwable::class)
expect fun uiSaveFilePicker(
    windowTitle: String,
    defFileName: String,
    fileContent: String,
)

@Throws(Throwable::class)
expect fun uiReadFilePicker(
    windowTitle: String,
    onFileRead: (fileContent: String) -> Unit,
)

//
// KMP Init

lateinit var initKmpDeferred: Deferred<Unit>

internal fun initKmp(
    sqlDriver: SqlDriver,
) {
    db = NeverdoDB(
        driver = sqlDriver,
        BoardSQAdapter = BoardSQ.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
        ListSQAdapter = ListSQ.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
        CardSQAdapter = CardSQ.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
    )
    initKmpDeferred = defaultScope().async { DI.init() }
}

////

fun Int.toHms(
    roundToNextMinute: Boolean = false
): List<Int> {
    val time = if (!roundToNextMinute) this
    else {
        val rmd = this % 60
        if (rmd == 0) this else (this + (60 - rmd))
    }
    return listOf(time / 3600, (time % 3_600) / 60, time % 60)
}

fun reportApi(message: String) {
    // todo
    zlog("reportApi: $message")
}

fun CoroutineScope.launchEx(
    block: suspend CoroutineScope.() -> Unit,
) {
    launch {
        try {
            block()
        } catch (e: UIException) {
            uiAlert(e.uiMessage)
        } catch (e: Throwable) {
            uiAlert(e.stackTraceToString())
            reportApi("launchEx $e\n${e.stackTraceToString()}")
        }
    }
}

fun launchExDefault(
    block: suspend CoroutineScope.() -> Unit,
) = defaultScope().launchEx(block)

fun <T> Flow<T>.onEachExIn(
    scope: CoroutineScope,
    action: suspend (T) -> Unit,
) = onEach {
    try {
        action(it)
    } catch (e: Throwable) {
        reportApi("onEachEx $e")
    }
}.launchIn(scope)

class UIException(val uiMessage: String) : Exception(uiMessage)
