package io.github.zmilla93.jupdate

import updater.data.AppVersion
import java.nio.file.Path

/** The minimal required configuration when implementing an [AbstractUpdater]. */
open class UpdaterConfig(
    /** The [AppVersion] of the currently running program. */
    val currentVersion: AppVersion,
    /** One or more file names that will be downloaded into the temp directory and then unpacked if needed. */
    val assetNames: Array<String>,
    /** The name of the patcher file inside the temp directory that will run after unpacking. */
    val patcherFileName: String,
    /** Where temporary files & patcher are stored during update. Creating at update start, deleted during clean. */
    val tempDirectory: Path,
)