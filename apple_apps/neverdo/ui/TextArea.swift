import SwiftUI

struct TextArea: NSViewRepresentable {

    let setupTextView: (NSTextView) -> Void
    let onSubmit: (NSTextView) -> Void
    let onEscPressed: () -> Void

    func makeNSView(
        context: NSViewRepresentableContext<TextArea>
    ) -> NSTextView {
        let textView = MyTextView(frame: .zero)
        textView.parent = self
        setupTextView(textView)
        return textView
    }

    func updateNSView(
        _ textView: NSTextView,
        context: NSViewRepresentableContext<TextArea>
    ) {
    }
}

// https://stackoverflow.com/a/76278534/23126671
private class MyTextView: NSTextView {

    var parent: TextArea! // todo Set by constructor

    //

    private var heightConstraint: NSLayoutConstraint?

    private var contentSize: CGSize {
        get {
            guard let layoutManager = layoutManager,
                  let textContainer = textContainer
            else {
                return .zero
            }
            layoutManager.ensureLayout(for: textContainer)
            return layoutManager.usedRect(for: textContainer).size
        }
    }

    override init(frame frameRect: NSRect, textContainer container: NSTextContainer?) {
        super.init(frame: frameRect, textContainer: container)
    }

    override init(frame frameRect: NSRect) {
        super.init(frame: frameRect)
        self.translatesAutoresizingMaskIntoConstraints = false
        heightConstraint = self.heightAnchor.constraint(equalToConstant: 0)
        heightConstraint?.isActive = true
    }

    override func paste(_ sender: Any?) {
        pasteAsPlainText(sender)
    }

    // trick not keyDown() because of NSEvent.addLocalMonitorForEvent(.keyDown)
    override func keyUp(with event: NSEvent) {

        // Esc
        if event.keyCode == 53 {
            parent.onEscPressed()
            return
        }

        // Return
        if event.keyCode == 36 {
            let modifiers = event.modifierFlags.intersection(.deviceIndependentFlagsMask)
            if (modifiers.isEmpty) {
                parent.onSubmit(self)
                return
            } else if modifiers == [.command] || modifiers == [.shift] || modifiers == [.option] || modifiers == [.control] {
                if let pointerIndex = self.selectedRanges.first?.rangeValue.location {
                    var newText = self.string
                    newText.insert("\n", at: newText.index(newText.startIndex, offsetBy: pointerIndex))
                    self.string = newText
                    self.setSelectedRange(NSMakeRange(pointerIndex + 1, 0))
                    return
                } else {
                    // todo report if nil
                    zlog("nil pointerIndex")
                }
            }
        }

        super.keyDown(with: event)
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    override var string: String {
        didSet {
            didChangeText()
        }
    }

    override func layout() {
        updateHeight()
        super.layout()
    }

    override func didChangeText() {
        updateHeight()
        super.didChangeText()
    }

    private func updateHeight() {
        heightConstraint?.constant = self.contentSize.height + textContainerInset.height * 2
    }
}
