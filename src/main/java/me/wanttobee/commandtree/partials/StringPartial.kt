package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class StringPartial(argName: String) : IReturnablePartial<String>(argName) {
    private var staticOptions : Array<String>? = null
    private var dynamicOptions : ((Player) -> Array<String>)? = null

    fun setStaticOptions(possibilities : Array<String>) : StringPartial {
        this.staticOptions = possibilities
        return this
    }

    fun setDynamicOptions(possibilities : (Player) -> Array<String>) : StringPartial {
        this.dynamicOptions = possibilities
        return this
    }

    fun getOptions(commander: Player) : Array<String>? {
        return dynamicOptions?.invoke(commander) ?: staticOptions
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): String? {
         if(tailArgs.isEmpty()) return null
         val currentPossibilities : Array<String> = getOptions(commander) ?: return tailArgs.first()
         for(pos in currentPossibilities){
             if(pos == tailArgs.first())
                 return tailArgs.first()
         }
        CommandTreeSystem.sendErrorToCommander(commander,tailArgs.first(),"is not a valid argument" )
        return null
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val optionsList = getOptions(commander)?.toList() ?: return if (currentlyTyping == "") listOf("...") else emptyList()

        val list = mutableListOf<String>()
        for (pos in optionsList) {
            if (pos.lowercase().contains(currentlyTyping.lowercase()))
                list.add(pos)
        }
        return list
    }
}
