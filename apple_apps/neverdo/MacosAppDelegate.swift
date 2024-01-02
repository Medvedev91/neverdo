import SwiftUI
import shared

class MacosAppDelegate: NSObject, NSApplicationDelegate {

    func applicationWillFinishLaunching(
        _ notification: Notification
    ) {
        Utils_kmp_macosKt.doInitKmpMacos()
    }

    func applicationShouldTerminateAfterLastWindowClosed(
        _ sender: NSApplication
    ) -> Bool {
        true
    }
}
