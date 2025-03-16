package io.github.zmilla93.updater.listening

import io.github.zmilla93.updater.core.Updater
import io.github.zmilla93.updater.data.UpdatePhase

/**
 * Listens for [UpdatePhase] events emitted by an [Updater].
 * Use [UpdatePhaseAdapter] if you only want specific overrides.
 */
interface UpdatePhaseListener {

    /** Called on the EDT when a new [UpdatePhase] begins. */
    fun onPhaseStart(updatePhase: UpdatePhase)

    /** Called on the EDT when an [UpdatePhase] completes. */
    fun onPhaseComplete(updatePhase: UpdatePhase)

    /**
     * Called when the program is about to close to start a new process, but before starting the new process.
     * Use this to clean up anything that might interfere with running the new process, such as file locking.
     * This function is NOT called on the EDT, and is NOT intended as a general purpose shutdown hook. It is
     * for ensuring a clean launch of the new program.
     */
    fun onProgramClose()

}