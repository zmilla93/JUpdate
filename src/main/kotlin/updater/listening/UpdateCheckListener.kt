package io.github.zmilla93.updater.listening

import io.github.zmilla93.updater.core.Updater
import updater.data.AppVersion

/**
 * Listens for new updates events when using [Updater.runPeriodicUpdateCheck].
 */
interface UpdateCheckListener {

    /**
     * Called on the EDT when a new update is detected from a delayed/async update check.
     * For immediate/blocking checks, call Updater.isUpdateAvailable() directly.
     */
    fun onNewUpdateAvailable(newVersion: AppVersion)

}