package me.wanttobee.commandtree.nodes

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

// We are using a composite design
// that means we have branches and leaves
// branches may have new nodes hanging on it
// leaves are normally the end
// in our case however, there can be leaves to contain other leaves lol
// branched: a way of sorting the leaves in groups
// leaves: the parameters which are eventually going to be used for the thing you are trying to do
class CommandBranch(argName: String, private val branches : Array<ICommandNode> ) : ICommandNode(argName) {
    override val commandParam: String = "..."

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        if(tailArgs.isEmpty()){
            CommandTreeSystem.sendErrorToCommander(commander,"not enough arguments found")
            return
        }
        for(branch in branches) {
            if (branch.argName.lowercase() == tailArgs.first().lowercase()){
                branch.onCommand(commander, tailArgs.copyOfRange(1, tailArgs.size) )
                return
            }
        }
        CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs.first()} is not a valid argument")
    }

    override fun nextTabComplete(commander: Player, fromArg:String, tailArgs: Array<String>): List<String> {
        for(branch in branches){
            if(branch.argName.lowercase() == fromArg.lowercase())
                return branch.getTabComplete(commander, tailArgs)
        }
        return emptyList()
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String) : List<String> {
        val list = mutableListOf<String>()
        for(branch in branches)
            if(branch.argName.lowercase().contains(currentlyTyping.lowercase())) list.add(branch.argName)
        return list
    }
}
