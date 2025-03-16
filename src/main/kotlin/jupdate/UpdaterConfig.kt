package io.github.zmilla93.jupdate

import updater.data.AppVersion
import java.nio.file.Path

/** The minimal required configuration when implementing an [AbstractUpdater]. */
open class UpdaterConfig(
    /** The name of the platform specific executable that runs the program. */
    val nativeExecutableName:String,
    /** The [AppVersion] of the currently running program. */
    val currentVersion: AppVersion,
    // FIXME : Switch to an abstract getDownloadTargets():DownloadTarget[] better flexability
    /** One or more files that will be downloaded into the temp directory and then unpacked if needed. */
    val assetNames: Array<String>,
    /** The name of the patcher file inside the temp directory that will run after unpacking. */
    val patcherFileName: String,
    /** Where temporary files & patcher are stored during update. Creating at update start, deleted during clean. */
    val tempDirectory: Path,
)