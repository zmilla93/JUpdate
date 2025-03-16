package io.github.zmilla93.updater.data

import org.slf4j.LoggerFactory

/**
 * Represents currently supported distribution types.
 * Distribution type is set via the program argument --distribution:TYPE
 * TYPE is the enum name, to lower case, and "_" replaced with "-".
 * IE WIN_MSI uses "--distribution:win-msi".
 * This value must be passed to jpackage using "--arguments" when building for a specific platform.
 */
enum class DistributionType {

    NONE,
    DEBUG,
    JAR,
    WIN_MSI,
    WIN_PORTABLE,
    // TODO : Mac & Linux
    ;

    companion object {

        const val ARG_PREFIX = "--distribution:"
        val logger = LoggerFactory.getLogger(DistributionType::class.simpleName)

        fun getType(value: String?): DistributionType {
            if (value == null) {
                logger.error("No distribution type argument found. This program cannot be updated.")
                return NONE
            }
            val type = entries.find { it.name.lowercase().replace("_", "-") == value.lowercase() }
            if (type == null) {
                logger.error("Unknown distribution type '$value'. This program cannot be updated.")
                return NONE
            }
            return type
        }

        fun getTypeFromArgs(args: Array<String>): DistributionType {
            val arg = ArgsList.getCleanArg(args, ARG_PREFIX)
            return getType(arg)
        }

    }

}