package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

class PairPartial<T,U>(argName: String) : IReturnablePartial<Pair<T,U>>(argName) {
    override var argumentsNeeded: Int = 0
    private var firstPartial: IReturnablePartial<T>? = null
    private var secondPartial: IReturnablePartial<U>? = null

    private var invalidVarargUsage = false

    fun setPartials(partialOne: IReturnablePartial<T>, partialTwo: IReturnablePartial<U>) : PairPartial<T,U> {
        this.firstPartial = partialOne
        this.secondPartial = partialTwo
        argumentsNeeded =  partialOne.argumentsNeeded + partialTwo.argumentsNeeded
        if(firstPartial is VarargPartial<*>)
            invalidVarargUsage = true
        return this
    }

    private fun correctlyInitializedCheck(commander: Player) : Boolean {
        if (invalidVarargUsage) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "PairPartial(${argName}) has a VarargPartial as the first argument, which is not allowed")
            return false
        }
        if (firstPartial == null || secondPartial == null) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "PairPartial(${argName}) has not been initialized with a type reference")
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
        val pair = validateValue(commander, tailArgs) ?: return
        if( effect != null) effect!!.invoke(commander, pair)
        else CommandTreeSystem.sendErrorToCommander(commander,"no effect has been provided")
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Pair<T,U>? {
        if (!correctlyInitializedCheck(commander)) return null
        if(tailArgs.size < argumentsNeeded) {
            CommandTreeSystem.sendErrorToCommander(commander, "not enough arguments found")
            return null
        }

        val firstValue : T = firstPartial!!.validateValue(commander, tailArgs) ?: return null
        val secondValue : U = secondPartial!!.validateValue(commander, tailArgs.copyOfRange(firstPartial!!.argumentsNeeded, tailArgs.size)) ?: return null
        return Pair(firstValue,secondValue)
    }

    // let's say we have a Pair<Pair<A,B>, Pair<C,D> >
    // if the arguments are ["pair","A","B","C"]
    // then nextTabComplete will be called with  Pair.nextTabComplete(player, "A", ["B","C"])
    // in other words, Pair is the object, and his tab complete job is "A", however as you can se we are already done with that,
    // so now it's the next parameters job to do the tab complete. in particular, it's the job of "A"  to do it
    // That's why it is given as the fromArg
    override fun nextTabComplete(commander: Player, thisArg: String, followingArgs: Array<String>): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        return if(followingArgs.size < firstPartial!!.argumentsNeeded){
            // T tab complete
            // as long as the tailArgs list is smaller, we know it belongs to firstLeaf.
            // that's because fromArg is taken from the list, and thus if size is 2, it will always belong to tabComplete if the list is 0 or 1
            firstPartial!!.getTabComplete(commander,arrayOf(thisArg) + followingArgs)
        } else{
            // U tab complete
            // however, when the list becomes the same size (so in other words, list+fromArg becomes bigger)
            // then it is overflowing the first Leaf, and  thus going to the second Leaf
            secondPartial!!.getTabComplete(commander,followingArgs.copyOfRange(firstPartial!!.argumentsNeeded-1, followingArgs.size))
        }

    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        if (!correctlyInitializedCheck(commander)) return emptyList()
        //T tabComplete
        // we don't have to check, the first parameter always belongs to the first leaf
        return firstPartial!!.getTabComplete(commander, arrayOf(currentlyTyping))
    }
}
