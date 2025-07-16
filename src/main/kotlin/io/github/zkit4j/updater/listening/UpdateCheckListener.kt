package io.github.zkit4j.updater.listening

import io.github.zkit4j.updater.data.AppVersion
import io.github.zkit4j.updater.core.Updater

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