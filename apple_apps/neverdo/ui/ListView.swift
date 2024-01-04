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

                    ForEach(state.cardsUI, id: \.card.id) { card in

                        if card.isEditable {
                            CardFormView(
                                card: card.card,
                                listToAdd: state.list,
                                onCancel: {
                                    vm.setEditableCard(cardUI: nil)
                                },
                                onSave: {
                                    vm.setEditableCard(cardUI: nil)
                                }
                            )
                                    .padding(.bottom, 12)
                                    .padding(.horizontal, 16)
                        } else {
                            Button(
                                action: {
                                    vm.setEditableCard(cardUI: card)
                                },
                                label: {
                                    Text(card.card.text)
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

                CardFormView(
                    card: nil,
                    listToAdd: state.list,
                    onCancel: {},
                    onSave: {}
                )
                        .padding(.bottom, 12)
                        .padding(.horizontal, 12)
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
