import SwiftUI

struct TextField__VMState: View {

    /// It's not VMState-like, but it's useful for UI logic.
    @FocusState.Binding private var isFocused: Bool

    /// TRICK
    /// Otherwise on init() with new text @State text would not updated.
    /// It is needed for view model if input hints exists.
    @State private var text: String
    private let stateText: String

    private let placeholder: String
    private let onValueChanged: (String) -> Void

    init(
        text: String,
        placeholder: String,
        isFocused: FocusState<Bool>.Binding,
        onValueChanged: @escaping (String) -> Void
    ) {
        _isFocused = isFocused
        _text = State(initialValue: text)
        stateText = text
        self.placeholder = placeholder
        self.onValueChanged = onValueChanged
    }

    var body: some View {

        ZStack(alignment: .trailing) {

            ZStack {
                TextField(
                    text: $text,
                    prompt: Text(placeholder),
                    axis: .vertical
                ) {
                    // todo what is it?
                }
                        .padding(.vertical, 8)
            }
                ///
                    .onChange(of: text) { newValue in
                        onValueChanged(newValue)
                    }
                    .onChange(of: stateText) { newValue in
                        text = newValue
                    }
                    ///
                    .focused($isFocused)
                    .textFieldStyle(.plain)
                    .padding(.leading, 12)
                    .padding(.trailing, 12)
        }
                .onTapGesture {
                    isFocused = true
                }
    }
}
