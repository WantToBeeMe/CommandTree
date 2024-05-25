package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class VarargPartial<T>(argName: String) : IReturnablePartial<List<T>>(argName) {
    override var argumentsNeeded = 1
    var canReturnEmpty = false
        private set
    private var typeReference : IReturnablePartial<T>? = null

    fun setCanReturnEmpty(canReturnEmpty: Boolean) : VarargPartial<T> {
        this.canReturnEmpty = canReturnEmpty
        argumentsNeeded = if(canReturnEmpty) 0 else 1
        return this
    }
    fun setPartial(typeReference: IReturnablePartial<T>) : VarargPartial<T> {
        this.typeReference = typeReference
        return this
    }

    private fun correctlyInitializedCheck(commander: Player) : Boolean {
        if (typeReference == null) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "VarargPartial(${argName}) has not been initialized with a type reference")
            return false
        }
        return true
    }

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        if (!correctlyInitializedCheck(commander)) return

        if(canReturnEmpty && tailArgs.isEmpty())
            effect?.invoke(commander, emptyList() )
        else super.onCommand(commander, tailArgs)
    }

    override fun validateValue(commander : Player, tailArgs: Array<String>) : List<T>? {
        if (!correctlyInitializedCheck(commander)) return null

        val listT : MutableList<T> =  mutableListOf()
        for(arg in tailArgs){
            val potentialT = typeReference!!.validateValue(commander, arrayOf(arg)) ?: return null
            listT.add(potentialT)
        }
        return listT
    }

    override fun nextTabComplete(commander: Player, thisArg: String, followingArgs: Array<String>): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        // we make sure that we only pass in an array of size 1, that way the tab complete gets handled
        // by the leaf we use as a type reference
        return typeReference!!.getTabComplete(commander, arrayOf(followingArgs.last()))
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        // we make sure that we only pass in an array of size 1, that way the tab complete gets handled
        // by the leaf we use as a type reference
        return typeReference!!.getTabComplete(commander, arrayOf(currentlyTyping))
    }
}