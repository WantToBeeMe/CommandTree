package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class TriplePartial<T,U,V>(argName: String) : IReturnablePartial<Triple<T,U,V>>(argName) {
    override var argumentsNeeded: Int = 0
    private var firstPartial: IReturnablePartial<T>? = null
    private var secondPartial: IReturnablePartial<U>? = null
    private var thirdPartial: IReturnablePartial<V>? = null

    private var invalidVarargUsage = false

    fun setPartials(partialOne: IReturnablePartial<T>, partialTwo: IReturnablePartial<U>, partialThree:IReturnablePartial<V> ) : TriplePartial<T,U,V> {
        this.firstPartial = partialOne
        this.secondPartial = partialTwo
        this.thirdPartial = partialThree
        argumentsNeeded = partialOne.argumentsNeeded + partialTwo.argumentsNeeded + partialThree.argumentsNeeded
        if (firstPartial is VarargPartial<*> || secondPartial is VarargPartial<*>)
            invalidVarargUsage = true
        return this
    }
    private fun correctlyInitializedCheck(commander: Player) : Boolean {
        if (invalidVarargUsage) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "TriplePartial(${argName}) has a VarargPartial as the first or second argument, which is not allowed")
            return false
        }
        if (firstPartial == null || secondPartial == null || thirdPartial == null) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "TriplePartial(${argName}) has not been initialized with a type reference")
            return false
        }
        return true
    }

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        if(tailArgs.isEmpty()) {
            if(emptyEffect != null) emptyEffect!!.invoke(commander)
            else CommandTreeSystem.sendErrorToCommander(commander, "not enough arguments found")
            return
        }
        val triple = validateValue(commander, tailArgs) ?: return
        if( effect != null) effect!!.invoke(commander, triple)
        else CommandTreeSystem.sendErrorToCommander(commander,"no effect has been provided")
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Triple<T,U ,V>? {
        if (!correctlyInitializedCheck(commander)) return null
        if(tailArgs.size < argumentsNeeded) {
            CommandTreeSystem.sendErrorToCommander(commander, "not enough arguments found")
            return null
        }
        val firstValue : T = firstPartial!!.validateValue(commander, tailArgs) ?: return null
        val secondValue : U = secondPartial!!.validateValue(commander, tailArgs.copyOfRange(firstPartial!!.argumentsNeeded, tailArgs.size)) ?: return null
        val thirdValue : V =  thirdPartial!!.validateValue(commander, tailArgs.copyOfRange(firstPartial!!.argumentsNeeded + secondPartial!!.argumentsNeeded, tailArgs.size)) ?: return null
        return Triple(firstValue,secondValue,thirdValue)
    }

    // read the comments in the CommandPairLeaf variant, same principle, just 2 instead of 3
    override fun nextTabComplete(commander: Player, thisArg: String, followingArgs: Array<String>): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        return if(followingArgs.size < firstPartial!!.argumentsNeeded){
            // T tab complete
            firstPartial!!.getTabComplete(commander,arrayOf(thisArg) + followingArgs)
        } else{
            // U tab complete
            if(followingArgs.size < firstPartial!!.argumentsNeeded + secondPartial!!.argumentsNeeded)
                secondPartial!!.getTabComplete(commander,followingArgs.copyOfRange(firstPartial!!.argumentsNeeded-1, followingArgs.size))

            // V tab complete
            else
                thirdPartial!!.getTabComplete(commander,followingArgs.copyOfRange(firstPartial!!.argumentsNeeded + secondPartial!!.argumentsNeeded -1, followingArgs.size))
        }
    }
    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        //T tabComplete
        // we don't have to check, the first parameter always belongs to the first leaf
        return firstPartial!!.getTabComplete(commander, arrayOf(currentlyTyping))
    }
}
