import SwiftUI
import shared

struct BoardView: View {

    @State private var vm: BoardVM

    init(
        boardDb: BoardDb
    ) {
        _vm = State(initialValue: BoardVM(board: boardDb))
    }

    var body: some View {

        VMView(vm: vm) { state in

            ZStack {

                if state.isFormBoardVisible {
                    BoardForm(vm: vm, state: state)
                } else {
                    BoardLists(vm: vm, state: state)
                }
            }
                    .navigationTitle(state.board.name)
        }
    }
}

private struct BoardLists: View {

    let vm: BoardVM
    let state: BoardVM.State

    var body: some View {

        VStack {

            ScrollView(.horizontal) {

                LazyHStack(spacing: 0) {

                    ForEach(state.lists, id: \.id) { listDb in
                        ListView(listDb: listDb)
                    }

                    VStack {

                        if state.isFormAddListVisible {
                            ListFormView(
                                listDb: nil,
                                boardDb: state.board,
                                onCancel: {
                                    vm.setIsFormAddListVisible(isVisible: false)
                                },
                                onSave: {
                                    vm.setIsFormAddListVisible(isVisible: false)
                                }
                            )
                        } else {
                            Button(
                                action: {
                                    vm.setIsFormAddListVisible(isVisible: true)
                                },
                                label: {
                                    Image(systemName: "plus")
                                }
                            )
                                    .buttonStyle(.borderless)
                        }

                        Spacer()
                    }
                            .padding(.top, 8)
                            .padding(.leading, 10)
                }
                        .padding(.vertical, 12)
            }
        }
                .toolbar {

                    HStack {

                        Button(
                            action: {
                                vm.setIsFormBoardVisible(isVisible: true)
                            },
                            label: {
                                Label("Edit Board", systemImage: "pencil")
                            }
                        )
                    }
                }
    }
}

private struct BoardForm: View {

    let vm: BoardVM
    let state: BoardVM.State

    @FocusState private var isFormFocused: Bool

    var body: some View {

        VStack {

            TextField__VMState(
                text: state.formBoardText,
                placeholder: "Board Name",
                isFocused: $isFormFocused,
                onValueChanged: { newValue in
                    vm.setFormBoardText(text: newValue)
                }
            )

            Button("Save") {
                vm.submitFormBoard()
            }

            Button("Delete") {
                vm.deleteBoard()
            }

            Spacer()
        }
                .toolbar {

                    HStack {

                        Button(
                            action: {
                                vm.setIsFormBoardVisible(isVisible: false)
                            },
                            label: {
                                Text("Back")
                            }
                        )
                    }
                }
    }
}
