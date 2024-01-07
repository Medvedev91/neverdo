import SwiftUI

struct TextFieldCustom: NSViewRepresentable {

    @Binding var text: String
    let setupTextField: (NSTextField) -> Void
    let onSubmit: () -> Void

    func makeNSView(
        context: NSViewRepresentableContext<TextFieldCustom>
    ) -> NSTextField {
        let textField = NSTextField(frame: .zero)
        setupTextField(textField)
        textField.stringValue = text
        textField.delegate = context.coordinator
        return textField
    }

    func updateNSView(
        _ textField: NSTextField,
        context: NSViewRepresentableContext<TextFieldCustom>
    ) {
        textField.stringValue = text
        textField.sizeToFit()
    }

    func makeCoordinator() -> TextFieldCustom.Coordinator {
        Coordinator(parent: self)
    }

    class Coordinator: NSObject, NSTextFieldDelegate {

        var parent: TextFieldCustom

        init(parent: TextFieldCustom) {
            self.parent = parent
        }

        func controlTextDidChange(_ obj: Notification) {
            let textField = obj.object as! NSTextField
            parent.text = textField.stringValue
        }

        func control(
            _ control: NSControl,
            textView: NSTextView,
            doCommandBy commandSelector: Selector
        ) -> Bool {
            if let event: NSEvent = NSApp.currentEvent, event.keyCode == 36 {
                let modifiers = event.modifierFlags.intersection(.deviceIndependentFlagsMask)
                if (modifiers.isEmpty) {
                    parent.onSubmit()
                    return true
                } else if modifiers == [.command] || modifiers == [.shift] {
                    // todo report if null
                    if let pointerIndex = textView.selectedRanges.first?.rangeValue.location {
                        let oldText = textView.string
                        var newText = oldText
                        newText.insert("\n", at: newText.index(newText.startIndex, offsetBy: pointerIndex))
                        parent.text = newText
                        textView.setSelectedRange(NSMakeRange(pointerIndex + 1, 0))
                        return true
                    }
                }
            }
            return false
        }
    }
}
