import SwiftUI
import shared

struct ListFormView: View {

    @State private var vm: ListFormVM

    private let onCancel: () -> Void
    private let onSave: () -> Void

    ///

    @FocusState private var isFormFocused: Bool

    init(
        listDb: ListDb?,
        boardDb: BoardDb,
        onCancel: @escaping () -> Void,
        onSave: @escaping () -> Void
    ) {
        _vm = State(initialValue: ListFormVM(list: listDb, boardToAdd: boardDb))
        self.onCancel = onCancel
        self.onSave = onSave
    }

    var body: some View {

        VMView(vm: vm, stack: .VStack()) { state in

            ZStack {
                TextField__VMState(
                    text: state.inputText,
                    placeholder: "Board Name",
                    isFocused: $isFormFocused,
                    onValueChanged: { newValue in
                        vm.setInputText(text: newValue)
                    }
                )
            }
                    .background(squircleShape.fill(.tertiary))

            HStack(spacing: 10) {

                Button("Save") {
                    vm.submitForm {
                        onSave()
                    }
                }

                Button("Cancel") {
                    onCancel()
                }
                        .buttonStyle(.link)
                        .foregroundColor(.secondary)

                if state.list != nil {
                    Button("Delete List") {
                        vm.deleteList()
                    }
                }

                Spacer()
            }
                    .padding(.top, 16)
        }
                .frame(width: 250)
    }
}
