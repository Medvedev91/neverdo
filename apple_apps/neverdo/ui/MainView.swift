import SwiftUI
import shared

private let menuIconsColor: Color = .secondary

struct MainView: View {

    @State private var vm = MainVM()

    @State private var selectedBoard: BoardDb? = nil

    var body: some View {

        VMView(vm: vm) { state in

            NavigationSplitView(

                sidebar: {

                    List(selection: $selectedBoard) {

                        ForEach(state.boards, id: \.id) { boardDb in

                            Text(boardDb.name)
                                    .tag(boardDb)
                                    .font(.system(size: 14))
                        }
                    }
                            .safeAreaInset(edge: .bottom) {

                                VStack {

                                    Divider()

                                    HStack {

                                        Button(
                                            action: {
                                                vm.doNewBoard()
                                            },
                                            label: {
                                                Image(systemName: "plus")
                                                        .renderingMode(.template)
                                                        .foregroundColor(menuIconsColor)
                                            }
                                        )
                                                .buttonStyle(.borderless)

                                        Spacer()

                                        Menu(
                                            content: {
                                                Button("Backup") {
                                                    vm.backup()
                                                }
                                                Button("Restore") {
                                                    vm.restore()
                                                }
                                            },
                                            label: {
                                                Image(systemName: "ellipsis.circle")
                                                        .foregroundStyle(menuIconsColor, menuIconsColor)
                                            }
                                        )
                                                .menuStyle(.borderlessButton)
                                                .menuIndicator(.hidden)
                                                .fixedSize()
                                    }
                                            .padding(.all, 8)
                                }
                            }
                },

                detail: {

                    ZStack {

                        if let selectedBoard = selectedBoard {
                            BoardView(boardDb: selectedBoard)
                                    .id("board_view_\(selectedBoard.id)")
                        } else {
                            ZStack {
                            }
                                    .navigationTitle("")
                        }
                    }
                            .background(.background)
                }
            )
        }
    }
}
