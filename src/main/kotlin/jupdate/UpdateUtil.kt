package io.github.zmilla93.jupdate

import java.io.File
import java.net.URISyntaxException
import java.util.regex.Matcher

class UpdateUtil {

    companion object {

        var cachedProgramPath = ""
        var hasCachedProgramPath = false

        /**
         *  Returns the full path of the currently running program.
         *  Used at the start of the updating process to save the original launch path.
         */
        fun getCurrentProgramPath(): String {
            if (!hasCachedProgramPath) {
                try {
                    var path = AbstractUpdater::class.java.protectionDomain.codeSource.location.toURI().path
                    if (path.startsWith("/")) path = path.replaceFirst("/".toRegex(), "")
                    // FIXME : Is cleaning file separators required?
                    cachedProgramPath = path.replace("[/\\\\]".toRegex(), Matcher.quoteReplacement(File.separator))
                    hasCachedProgramPath = true
                } catch (e: URISyntaxException) {
                    throw RuntimeException("Failed to get program path!")
                }
            }
            return cachedProgramPath
        }
    }

}