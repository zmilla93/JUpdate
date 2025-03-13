package io.github.zmilla93.jupdate

abstract class AbstractUpdater {

    abstract fun isUpdateAvailable(): Boolean

    abstract fun download(): Boolean

    abstract fun unpack(): Boolean

    abstract fun patch(): Boolean

    abstract fun clean(): Boolean

}