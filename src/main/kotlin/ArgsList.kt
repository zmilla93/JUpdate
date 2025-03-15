package io.github.zmilla93

class ArgsList(args: Array<String>) {

    val list = args.toMutableList()

    fun containsArg(arg: String): Boolean {
        return list.contains(arg)
    }

    fun containsArgPrefix(prefix: String): Boolean {
        return list.find { it.startsWith(prefix) } != null
    }

    fun getCleanArg(prefix: String): String? {
        val result = list.find { it.startsWith(prefix) }
        if (result == null) return null
        return result.replaceFirst(prefix, "")
    }

    fun addArg(arg: String) {
        list.add(arg)
    }

    fun removeArg(arg: String) {
        list.removeIf { it == arg }
    }

    // FIXME : Remove static?
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

    override fun toString(): String {
        return list.toString()
    }

}