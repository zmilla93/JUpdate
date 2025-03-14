package io.github.zmilla93.updater.data

import java.nio.file.Paths
import kotlin.io.path.exists

enum class DistributionType {

    NONE,
    JAR,
    WINDOWS_MSI,
    WINDOWS_PORTABLE,
    // TODO : Mac & Linux
    ;

    companion object {

        var hasRunCheck = false
        private var current: DistributionType = NONE

        fun current(launcherPath: String): DistributionType {
            if (hasRunCheck) return current
            val launcher = Paths.get(launcherPath)
            val appFolder = launcher.parent
            if (appFolder.resolve(".package").exists()) current = WINDOWS_MSI
            else if (appFolder.resolve(".jpackage.xml").exists()) current = WINDOWS_PORTABLE
            return current
        }

    }

}