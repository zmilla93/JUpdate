package io.github.zmilla93.jupdate

import java.nio.file.Path

class UpdaterConfig(
    /** One or more file names that will be downloaded into the temp directory and then unpacked if needed. */
    val assetNames: Array<String>,
    /** The name of the patcher file inside the temp directory after unpacking. */
    val patcherFileName: String,
    /** The location of files downloaded by the updater. */
    val tempDirectory: Path,
)