package io.neverdo.shared

import io.neverdo.appdbsq.NeverdoDB
import io.neverdo.shared.db.DB_NAME
import kotlinx.cinterop.*
import kotlinx.coroutines.launch
import platform.AppKit.*
import platform.Foundation.*
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual val isDevEnvironment: Boolean = Platform.isDebugBinary

fun initKmpMacos() {
    // ~/Library/Containers/io.neverdo.app/Data/Library/Application\ Support/databases
    val dbName = if (isDevEnvironment) "$DB_NAME.dev" else DB_NAME
    initKmp(createNativeDriver(dbName, NeverdoDB.Schema))
}

//
// UI Modals

actual fun uiAlert(
    message: String,
) {
    appleMainScope().launch {
        val alert = NSAlert()
        alert.alertStyle = NSAlertStyleWarning
        alert.messageText = "Warning"
        alert.informativeText = message
        alert.addButtonWithTitle("Ok")
        alert.runModal()
    }
}

actual fun uiConfirmation(
    title: String,
    message: String,
    onConfirm: () -> Unit,
) {
    appleMainScope().launch {
        val alert = NSAlert()
        alert.alertStyle = NSAlertStyleWarning
        alert.messageText = title
        alert.informativeText = message
        alert.addButtonWithTitle("Yes")
        alert.addButtonWithTitle("No")
        val response = alert.runModal()
        if (response == NSAlertFirstButtonReturn)
            onConfirm()
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun uiSaveFilePicker(
    windowTitle: String,
    defFileName: String,
    fileContent: String,
) {
    appleMainScope().launchEx {
        val savePanel = NSSavePanel()
        savePanel.title = windowTitle
        savePanel.nameFieldStringValue = defFileName
        savePanel.extensionHidden = false // Set extension on existing file click
        val response = savePanel.runModal()
        if (response == NSModalResponseOK) {
            val url = savePanel.URL!!
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                (fileContent as NSString).writeToFile(
                    path = url.path!!,
                    atomically = true,
                    encoding = NSUTF8StringEncoding,
                    error = errorPtr.ptr,
                )
                val error = errorPtr.value
                if (error != null)
                    throw Exception(error.toString())
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun uiReadFilePicker(
    windowTitle: String,
    onFileRead: (fileContent: String) -> Unit,
) {
    val openPanel = NSOpenPanel()
    openPanel.title = windowTitle
    openPanel.showsHiddenFiles = false
    openPanel.canChooseDirectories = false
    openPanel.allowsMultipleSelection = false
    openPanel.setAllowedFileTypes(listOf("json"))
    val response = openPanel.runModal()
    if (response == NSModalResponseOK) {
        val path = openPanel.URL!!.path!!
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            val fileContent = NSString.stringWithContentsOfFile(
                path = path,
                encoding = NSUTF8StringEncoding,
                error = errorPtr.ptr,
            )!!
            val error = errorPtr.value
            if (error != null)
                throw Exception(error.toString())
            onFileRead(fileContent)
        }
    }
}
