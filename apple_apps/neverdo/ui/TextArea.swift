import SwiftUI

struct TextArea: NSViewRepresentable {

    @Binding var text: String
    let setupTextView: (NSTextView) -> Void
    let onSubmit: () -> Void

    func makeNSView(
        context: NSViewRepresentableContext<TextArea>
    ) -> NSTextView {
        let textView = MyTextView(frame: .zero)
        textView.parent = self
        setupTextView(textView)
        textView.string = text
        return textView
    }

    func updateNSView(
        _ textView: NSTextView,
        context: NSViewRepresentableContext<TextArea>
    ) {
        textView.string = text
    }
}

// https://stackoverflow.com/a/76278534/23126671
private class MyTextView: NSTextView {

    var parent: TextArea! // todo Set by constructor

    //

    private var heightConstraint: NSLayoutConstraint?

    private var contentSize: CGSize {
        get {
            guard let layoutManager = layoutManager, let textContainer = textContainer else {
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

    override func keyDown(with event: NSEvent) {
        if event.keyCode == 36 {
            let modifiers = event.modifierFlags.intersection(.deviceIndependentFlagsMask)
            if (modifiers.isEmpty) {
                parent.onSubmit()
                return
            } else if modifiers == [.command] || modifiers == [.shift] || modifiers == [.option] || modifiers == [.control] {
                if let pointerIndex = self.selectedRanges.first?.rangeValue.location {
                    let oldText = self.string
                    var newText = oldText
                    newText.insert("\n", at: newText.index(newText.startIndex, offsetBy: pointerIndex))
                    parent.text = newText
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
        // Async to fix "update while update" log
        DispatchQueue.main.async {
            self.parent.text = self.string
        }
        updateHeight()
        super.didChangeText()
    }

    private func updateHeight() {
        heightConstraint?.constant = self.contentSize.height + textContainerInset.height * 2
    }
}
