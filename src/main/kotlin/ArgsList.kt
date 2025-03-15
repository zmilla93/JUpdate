package io.github.zmilla93

class ArgsList(args: Array<String>) {

    val list = args.toMutableList()


    companion object {
        fun getFullArg(args: Array<String>, argPrefix: String): String? {
            return args.find { it.startsWith(argPrefix) }
        }

        fun getCleanArg(args: Array<String>, argPrefix: String): String? {
            return cleanArg(getFullArg(args, argPrefix), argPrefix)
        }

        fun cleanArg(arg: String?, argPrefix: String): String? {
            if (arg == null) return null
            return arg.replaceFirst(argPrefix, "")
        }
    }

}