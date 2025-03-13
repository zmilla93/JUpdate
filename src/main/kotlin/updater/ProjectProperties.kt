package io.github.zmilla93.updater

import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.util.*

class ProjectProperties {

    var version: String = ""
    val logger = LoggerFactory.getLogger(javaClass)
    val fileName = "project.properties"

    init {
        try {
            /** Read the project.properties file into a [Properties] object. */
            val stream = ProjectProperties::class.java.getClassLoader().getResourceAsStream(fileName)
            if (stream == null) throw RuntimeException("Failed to create an input stream for $fileName")
            val properties = Properties()
            properties.load(BufferedInputStream(stream))
            stream.close()
            /** Read key, value pairs */
            version = properties.getProperty("version")
        } catch (e: IOException) {
            logger.error("Properties not found! Create a 'project.properties' file in the resources folder, then add the lines 'version=\${project.version}' and 'artifactId=\${project.artifactId}'.")
        }
    }

}