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
                        .onAppear() {
                            // trick preventing escape to close full screen overrides everywhere
                            NSEvent.addLocalMonitorForEvents(matching: .keyDown) { (aEvent) -> NSEvent? in
                                if aEvent.keyCode == 53 {
                                    return nil
                                }
                                return aEvent
                            }
                        }
                }
            }
        }
    }
}
