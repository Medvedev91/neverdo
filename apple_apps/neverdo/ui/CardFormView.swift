import SwiftUI
import shared

struct CardFormView: View {

    @State private var vm: CardFormVM

    private let onCancel: () -> Void
    private let onSave: () -> Void

    @FocusState private var isFormFocused: Bool

    init(
        card: CardDb?,
        listToAdd: ListDb,
        onCancel: @escaping () -> Void,
        onSave: @escaping () -> Void
    ) {
        _vm = State(initialValue: CardFormVM(card: card, listToAdd: listToAdd))
        self.onCancel = onCancel
        self.onSave = onSave
    }

    var body: some View {

        VMView(vm: vm, stack: .VStack()) { state in

            VStack {

                TextField__VMState(
                    text: state.inputText,
                    placeholder: "Text",
                    isFocused: $isFormFocused,
                    onValueChanged: { newValue in
                        vm.setInputText(text: newValue)
                    }
                )
                        .background(squircleShape.fill(.tertiary))

                HStack(spacing: 10) {

                    Button("Save") {
                        vm.submitForm {
                            onSave()
                        }
                    }

                    if state.card != nil {

                        Button("Cancel") {
                            onCancel()
                        }
                                .buttonStyle(.link)
                                .foregroundColor(.secondary)

                        Button("Delete Card") {
                            vm.deleteCard()
                        }
                    }

                    Spacer()
                }
                        .padding(.top, 16)
            }
        }
    }
}
