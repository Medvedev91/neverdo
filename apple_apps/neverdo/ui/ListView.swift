import SwiftUI
import shared

struct ListView: View {

    @State private var vm: ListVM

    init(
        listDb: ListDb
    ) {
        _vm = State(initialValue: ListVM(list: listDb))
    }

    var body: some View {

        VMView(vm: vm, stack: .HStack()) { state in

            VStack {

                HStack {

                    if state.isEditFormPresented {
                        ListFormView(
                            listDb: state.list,
                            boardDb: state.board,
                            onCancel: {
                                vm.setIsEditFormPresented(isPresented: false)
                            },
                            onSave: {
                                vm.setIsEditFormPresented(isPresented: false)
                            }
                        )
                        .padding(.all, 16)
                    } else {

                        Button(state.list.name) {
                            vm.setIsEditFormPresented(isPresented: true)
                        }
                        .foregroundColor(.primary)
                        .buttonStyle(.borderless)
                        .padding(.vertical, 14)
                        .padding(.bottom, 2)
                        .padding(.horizontal, 16)
                        .font(.system(size: 18, weight: .bold))

                        Spacer()
                    }
                }
                .padding(.top, 8)

                ScrollView(.vertical, showsIndicators: false) {

                    ForEach(state.cardsUi, id: \.card.id) { cardUi in

                        if cardUi.isEditable {
                            CardFormView(
                                cardUi: cardUi,
                                onClose: {
                                    vm.setEditableCard(cardUi: nil)
                                }
                            )
                            .padding(.top, 4)
                            .padding(.bottom, 4)
                        } else {
                            Button(
                                action: {
                                    vm.setEditableCard(cardUi: cardUi)
                                },
                                label: {
                                    Text(cardUi.card.text)
                                        .padding(.all, 4)
                                        .padding(.horizontal, 12)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                        .background(.background) // trick to full width clickable
                                }
                            )
                            .buttonStyle(.plain)
                        }

                        Divider()
                            .padding(.leading, 16)
                    }
                }

                NewCardForm(listVM: vm)
            }
            .frame(width: 280)

            VStack {
            }
            .frame(width: 1)
            .frame(maxHeight: .infinity)
            .background(.quaternary)
        }
    }
}

private struct CardFormView: View {

    let cardUi: ListVM.CardUi
    let onClose: () -> Void

    @FocusState private var isFormFocused: Bool

    var body: some View {

        VStack {

            TextArea(
                setupTextView: { nsTextView in
                    nsTextView.string = cardUi.initEditText
                    prepTextAreaTextStyle(nsTextView: nsTextView)
                    isFormFocused = true
                },
                onSubmit: { textField in
                    cardUi.updateText(
                        text: textField.string,
                        onSuccess: {
                            onClose()
                        }
                    )
                }
            )
            .focused($isFormFocused)

            HStack(spacing: 10) {

                Button("Cancel") {
                    onClose()
                }
                .buttonStyle(.link)
                .foregroundColor(.secondary)

                Button("Delete Card") {
                    cardUi.delete()
                }

                Spacer()
            }
            .padding(.top, 12)
            .padding(.horizontal, 5)
        }
        .padding(.horizontal, 11)
    }
}

private struct NewCardForm: View {

    let listVM: ListVM

    @FocusState private var isFocused: Bool

    var body: some View {

        VStack {

            Divider()

            TextArea(
                setupTextView: { nsTextView in
                    prepTextAreaTextStyle(nsTextView: nsTextView)
                },
                onSubmit: { textField in
                    listVM.addCard(
                        text: textField.string,
                        onSuccess: {
                            DispatchQueue.main.async {
                                textField.string = ""
                            }
                        }
                    )
                }
            )
            .padding(.top, 12)
            .padding(.bottom, 12)
            .padding(.horizontal, 12)
            .background(.background) // trick for onTapGesture()
            .onTapGesture {
                isFocused = true
            }
            .focused($isFocused)
        }
    }
}

private func prepTextAreaTextStyle(
    nsTextView: NSTextView
) {
    nsTextView.font = NSFont.systemFont(ofSize: 13)
}
