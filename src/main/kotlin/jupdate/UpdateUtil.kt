package io.github.zmilla93.jupdate

import org.slf4j.LoggerFactory
import java.io.*
import java.net.URISyntaxException
import java.net.http.HttpClient
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import java.util.regex.Matcher
import javax.net.ssl.SSLContext
import kotlin.system.exitProcess

class UpdateUtil {

    companion object {

        var cachedProgramPath = ""
        var hasCachedProgramPath = false
        val logger = LoggerFactory.getLogger(UpdateUtil::class.java.simpleName)

        /**
         *  Returns the full path of the currently running program.
         *  Used at the start of the updating process to save the original launch path.
         */
        // FIXME : This function is old, see if there is a better way to do things
        fun getCurrentProgramPath(): String {
            if (!hasCachedProgramPath) {
                try {
                    var path = AbstractUpdater::class.java.protectionDomain.codeSource.location.toURI().path
                    if (path.startsWith("/")) path = path.replaceFirst("/".toRegex(), "")
                    // FIXME : Is cleaning file separators required?
                    cachedProgramPath = path.replace("[/\\\\]".toRegex(), Matcher.quoteReplacement(File.separator))
                    hasCachedProgramPath = true
                } catch (e: URISyntaxException) {
                    // FIXME@important : Should handle this error better
                    throw RuntimeException("Failed to get program path!")
                }
            }
            return cachedProgramPath
        }

        // FIXME : Make this work with nested resources, and with/without starting slash
        fun copyResourceToDisk(sourceStr: String, destination: Path): Boolean {
            val prefix = if (sourceStr.startsWith("/")) "" else "/"
            val stream: InputStream =
                UpdateUtil::class.java.getResourceAsStream(prefix + sourceStr)
                    ?: throw java.lang.RuntimeException("Resource not found: $sourceStr")
            try {
                println("Resource: " + sourceStr)
                println("Creating Dir: " + destination.parent)
                val dir = Files.createDirectories(destination.parent)
                println("Result dir: " + dir)
                val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                val output = destination.resolve(sourceStr)
                println("dest: $destination")
                println("src: $sourceStr")
                println("out: $output")
                val writer = Files.newOutputStream(output).bufferedWriter(StandardCharsets.UTF_8)
                while (reader.ready()) writer.write(reader.readLine() + "\n")
                reader.close()
                writer.close()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                logger.error("IOException while copying resources to disk!")
            }
            return false
        }

//        fun runNewProcess(vararg args: String) {
////            val argsList = ArrayList<String>(*args)
//            val argsList = args.asList()
//            runNewProcess(argsList)
//        }

        /** Starts a new process while terminating the currently running program. */
        // FIXME : Switch to ArgsList?
        fun runNewProcess(args: ArrayList<String>) {
            // TODO @important: Unlock (or closeCurrentProcess?)
            val processBuilder = ProcessBuilder(args)
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
            processBuilder.start()
            exitProcess(0)
        }

        fun getHTTPClient(): HttpClient {
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, SecureRandom())
            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
        }

    }

}