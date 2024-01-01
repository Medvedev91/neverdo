package io.neverdo.shared.vm

import io.neverdo.shared.initKmpDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import io.neverdo.shared.launchEx

class AppVM : __VM<AppVM.State>() {

    data class State(
        val isAppReady: Boolean,
    )

    override val state = MutableStateFlow(
        State(
            isAppReady = false,
        )
    )

    override fun onAppear() {
        val scope = scopeVM()
        scope.launchEx {
            initKmpDeferred.join()
            state.update { it.copy(isAppReady = true) }
        }
    }
}
