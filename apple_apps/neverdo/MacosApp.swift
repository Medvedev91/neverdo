import SwiftUI
import shared

@main
struct MacosApp: App {
    
    @State private var vm = AppVM()

    @NSApplicationDelegateAdaptor(MacosAppDelegate.self) private var appDelegate

    var body: some Scene {

        WindowGroup {

            VMView(vm: vm) { state in

                if state.isAppReady {

                    MainView()
                }
            }
        }
    }
}
