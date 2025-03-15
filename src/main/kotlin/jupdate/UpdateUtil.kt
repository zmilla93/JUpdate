package io.github.zmilla93.jupdate

import org.slf4j.LoggerFactory
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

class UpdateUtil {

    companion object {

        var cachedProgramPath = ""
        var hasCachedProgramPath = false
        val logger = LoggerFactory.getLogger(UpdateUtil::class.java.simpleName)

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

        // FIXME : Make this work with nested resources, and with/without starting slash
        fun copyResourceToDisk(sourceStr: String, destination: Path): Path {
            val prefix = if (sourceStr.startsWith("/")) "" else "/"
            val stream: InputStream =
                UpdateUtil::class.java.getResourceAsStream(prefix + sourceStr)
                    ?: throw java.lang.RuntimeException("Resource not found: $sourceStr")
            try {
                Files.createDirectories(destination.parent)
                val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                val writer =
                    Files.newOutputStream(destination.resolve(Paths.get(sourceStr)))
                        .bufferedWriter(StandardCharsets.UTF_8)
                while (reader.ready()) writer.write(reader.readLine())
                reader.close()
                writer.close()
            } catch (e: IOException) {
                throw java.lang.RuntimeException(e)
            }
            return destination
        }
    }

}