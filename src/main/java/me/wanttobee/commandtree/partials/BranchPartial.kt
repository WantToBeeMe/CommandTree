package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class BranchPartial(argName: String ) : ICommandPartial(argName) {
    private var staticBranches : Array<ICommandPartial> = emptyArray()
    private var dynamicBranches: ( (Player) -> Array<ICommandPartial> )? = null

    fun getBranches(commander : Player) : Array<ICommandPartial> {
        return  dynamicBranches?.invoke(commander) ?: staticBranches
    }

    fun setStaticPartials(vararg branches: ICommandPartial) : BranchPartial{
        staticBranches = branches.toList().toTypedArray()
        return this
    }

    fun setDynamicPartials(branches: ( (Player) -> Array<ICommandPartial>)) : BranchPartial{
        dynamicBranches = branches
        return this
    }

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
       super.onCommand(commander, tailArgs)
        if (tailArgs.isEmpty()) return

        for(branch in getBranches(commander)) {
            if (branch.argName.lowercase() == tailArgs.first().lowercase()){
                branch.onCommand(commander, tailArgs.copyOfRange(1, tailArgs.size) )
                return
            }
        }
        CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs.first()} is not a valid argument")
    }

    override fun nextTabComplete(commander: Player, thisArg : String, followingArgs: Array<String>): List<String> {
        for(branch in getBranches(commander)){
            if(branch.argName.lowercase() == thisArg.lowercase())
                return branch.getTabComplete(commander, followingArgs)
        }
        return emptyList()
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String) : List<String> {
        val list = mutableListOf<String>()
        for(branch in getBranches(commander))
            if(branch.argName.lowercase().contains(currentlyTyping.lowercase())) list.add(branch.argName)
        return list
    }
}
