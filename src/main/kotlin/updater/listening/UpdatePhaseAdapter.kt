package io.github.zmilla93.updater.listening

import io.github.zmilla93.updater.data.UpdatePhase

/** Adapter class for [UpdatePhaseListener]. */
class UpdatePhaseAdapter : UpdatePhaseListener {

    override fun onPhaseStart(updatePhase: UpdatePhase) {}

    override fun onPhaseComplete(updatePhase: UpdatePhase) {}

    override fun onProgramClose() {}

}