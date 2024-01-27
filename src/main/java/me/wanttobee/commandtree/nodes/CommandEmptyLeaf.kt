package me.wanttobee.commandtree.nodes

import org.bukkit.entity.Player

// this is probably the simplest leaf
// its only has its own argument name and no other parameters
// in other words, this leave is  finished when you enter the argument name without anything else
class CommandEmptyLeaf(argName : String, emptyEffect : ((Player) -> Unit) ) : ICommandLeaf<Unit>(argName,{ _, _->}, emptyEffect) {
    override val commandParam: String = ""
    override val argumentsNeeded: Int = 0

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        emptyEffect!!.invoke(commander)
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>) {}

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        return emptyList()
    }
}
