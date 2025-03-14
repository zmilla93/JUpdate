package io.github.zmilla93.updater.data

/**
 * The updater is a simple state machine.
 */
enum class UpdateStep {

    NONE, DOWNLOAD, UNPACK, PATCH, CLEAN,

}