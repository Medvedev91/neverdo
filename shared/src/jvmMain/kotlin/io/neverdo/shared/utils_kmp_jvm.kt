package io.neverdo.shared

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.neverdo.appdbsq.NeverdoDB
import io.neverdo.shared.db.DB_NAME
import java.awt.FileDialog
import java.awt.Frame
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.swing.JOptionPane

actual val isDevEnvironment: Boolean = System.getProperty("jpackage.app-version") == null

fun initKmpJvm() {
    val userName: String = System.getProperty("user.name")!!

    val dbUri: String = if (isDevEnvironment) DB_NAME
    else {
        // todo check OS
        val dir = "/Users/$userName/Library/Application Support/neverdo"
        File(dir).mkdirs()
        File(dir, DB_NAME).absolutePath
    }

    val sqlDriver = JdbcSqliteDriver(
        url = "jdbc:sqlite:$dbUri",
        schema = NeverdoDB.Schema,
    )

    initKmp(sqlDriver = sqlDriver)
}

//
// UI Modals

actual fun uiAlert(
    message: String,
) {
    JOptionPane.showMessageDialog(
        null,
        message,
        "Warning",
        JOptionPane.ERROR_MESSAGE,
    )
}

actual fun uiConfirmation(
    title: String,
    message: String,
    onConfirm: () -> Unit,
) {
    val dialogResult = JOptionPane.showConfirmDialog(
        null,
        "Are you sure?",
        "Warning",
        JOptionPane.YES_NO_OPTION,
    )
    if (dialogResult == JOptionPane.YES_OPTION)
        onConfirm()
}

actual fun uiSaveFilePicker(
    windowTitle: String,
    defFileName: String,
    fileContent: String,
) {
    val parent: Frame? = null
    val dialog = object : FileDialog(parent, windowTitle, SAVE) {
        override fun setVisible(value: Boolean) {
            super.setVisible(value)
            val directory = directory
            val file = file
            if (value && directory != null && file != null) {
                val writer = BufferedWriter(FileWriter(directory + file))
                writer.write(fileContent)
                writer.close()
            }
        }
    }
    dialog.file = defFileName
    dialog.isVisible = true
}

actual fun uiReadFilePicker(
    windowTitle: String,
    onFileRead: (fileContent: String) -> Unit,
) {
    val parent: Frame? = null
    val dialog = object : FileDialog(parent, windowTitle, LOAD) {
        override fun setVisible(value: Boolean) {
            super.setVisible(value)
            val directory = directory
            val file = file
            if (value && directory != null && file != null)
                onFileRead(File(directory + file).readText())
        }
    }
    dialog.isVisible = true
}
