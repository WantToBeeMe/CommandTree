package me.wanttobee.commandtree

class Description(val summary: String, val usage: String? = null) {
    val subDescriptions = mutableListOf<Triple<String,String,String?>>()

    fun addSubDescription(
        name: String,
        description: String,
        usage: String? = null
    ) : Description {
        subDescriptions.add(Triple(name,description,usage))
        return this
    }
}