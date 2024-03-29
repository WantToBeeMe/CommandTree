package me.wanttobee.commandtree.nodes

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class CommandBoolLeaf(argName : String, effect : (Player, Boolean) -> Unit, emptyEffect : ((Player) -> Unit)? = null ) : ICommandLeaf<Boolean>(argName,effect, emptyEffect) {
    override val commandParam: String = "(true/false)"

    override fun validateValue(commander: Player, tailArgs: Array<String>): Boolean? {
        if(tailArgs.isEmpty()) return null
        val bool = tailArgs.first().toBooleanStrictOrNull() ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs.first()} is not a valid boolean", "(true/false)" )
            return null
        }
        return bool
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val list = mutableListOf<String>()
        if("true".startsWith(currentlyTyping.lowercase())) list.add("true")
        if("false".startsWith(currentlyTyping.lowercase())) list.add("false")
        return list
    }
}
