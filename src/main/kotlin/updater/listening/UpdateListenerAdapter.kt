package io.github.zmilla93.updater.listening

import io.github.zmilla93.updater.data.UpdatePhase
import updater.data.AppVersion

/** Adapter class for [UpdateListener]. */
class UpdateListenerAdapter : UpdateListener {

    override fun onPhaseStart(updatePhase: UpdatePhase) {}

    override fun onPhaseComplete(updatePhase: UpdatePhase) {}

    override fun onNewUpdateAvailable(newVersion: AppVersion) {}

    override fun onProgramClose() {}

}