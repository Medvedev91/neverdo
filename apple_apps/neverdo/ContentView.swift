import SwiftUI
import shared

struct ContentView: View {

    var body: some View {

        VStack {

            Image(systemName: "globe")
                    .imageScale(.large)
                    .foregroundStyle(.tint)

            Text("\(Atm_kmp_appleKt.timeMls()) \(Utils_kmp_macosKt.isDevEnvironment ? 1 : 0)--\(Atm_kmp_appleKt.getLocalUtcOffset())")

            //
            // todo remove

            Button("Load File") {
                Utils_kmp_macosKt.uiReadFilePicker(
                        windowTitle: "title my",
                        onFileRead: { fileContent in
                            print(fileContent)
                        }
                )
            }

            Button("Save File") {
                Utils_kmp_macosKt.uiSaveFilePicker(
                        windowTitle: "title my",
                        defFileName: "ok.json",
                        fileContent: "kokoeeee"
                )
            }

            Button("Open Dialog") {
                Utils_kmp_macosKt.uiAlert(message: "sdf;afeeeeee")
            }

            ////
        }
                .padding()
    }
}
