package io.github.zkit4j.updater.listening

import io.github.zkit4j.updater.data.UpdatePhase

/** Adapter class for [UpdatePhaseListener]. */
class UpdatePhaseAdapter : UpdatePhaseListener {

    override fun onPhaseStart(updatePhase: UpdatePhase) {}

    override fun onPhaseComplete(updatePhase: UpdatePhase) {}

    override fun onProgramClose() {}

}