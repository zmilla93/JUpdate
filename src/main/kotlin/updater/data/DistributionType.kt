package io.github.zmilla93.updater.data

import java.nio.file.Paths
import kotlin.io.path.exists

enum class DistributionType {

    NONE,
    WINDOWS_MSI,
    WINDOWS_PORTABLE,
    JAR,
    ;


    companion object {
        var hasCheckedType = false
        private var current: DistributionType = NONE
        fun checkCurrent(launcherPath: String): DistributionType {
            if (launcherPath.endsWith(".exe")) {
                val launcher = Paths.get(launcherPath)
                val appFolder = launcher.parent.resolve("app")
                if (appFolder.resolve(".package").exists()) current = WINDOWS_MSI
                else if (appFolder.resolve(".jpackage.xml").exists()) current = WINDOWS_PORTABLE
            }
            return current
        }
    }

}