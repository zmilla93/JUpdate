package io.github.zmilla93.updater.listening

import io.github.zmilla93.updater.core.Updater
import io.github.zmilla93.updater.data.UpdatePhase
import updater.data.AppVersion

/**
 * Listens for update events emitted by an [Updater].
 * Use [UpdateListenerAdapter] if you only want specific overrides.
 */
interface UpdateListener {

    /** Called when a new [UpdatePhase] begins. */
    fun onPhaseStart(updatePhase: UpdatePhase)

    /** Called when an [UpdatePhase] completes. */
    fun onPhaseComplete(updatePhase: UpdatePhase)

    /**
     * Called when a new update is detected from a delayed/async update check.
     * For immediate/blocking checks, call Updater.isUpdateAvailable() directly.
     */
    fun onNewUpdateAvailable(newVersion: AppVersion)

    /**
     * Called when the program is about to close to start a new process, but before starting the new process.
     * Use this to clean up anything that might interfere with running the new process, such as file locking.
     */
    fun onProgramClose()

}