package me.wanttobee.commandtree.nodes

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

// The pair is if you want multiple parameters under one arg name
// just like how vararg is infinite parameters of the same type, this is 2 parameters of 2 types of your choice,
// so it can be /HelloWorld say WantToNeeMe true
// where say is this and command object, and the pair is <Player, Boolean>
class CommandTripleLeaf<T,U, V>(argName : String, private val firstLeaf : ICommandLeaf<T>, private val secondLeaf : ICommandLeaf<U>, private val thirdLeaf : ICommandLeaf<V>, effect : (Player, Triple<T,U,V>) -> Unit, emptyEffect : ((Player) -> Unit)? = null )
    : ICommandLeaf<Triple<T, U, V>>(argName,effect, emptyEffect) where T : Any, U : Any, V : Any {
    constructor(argName: String, firstLeaf: ICommandLeaf<T>, secondLeaf: ICommandLeaf<U>, thirdLeaf : ICommandLeaf<V>) : this(argName,firstLeaf,secondLeaf,thirdLeaf,{ _, _ -> })

    override val commandParam: String = "${firstLeaf.commandParam} ${secondLeaf.commandParam} ${thirdLeaf.commandParam}"
    override val argumentsNeeded: Int = firstLeaf.argumentsNeeded + secondLeaf.argumentsNeeded + thirdLeaf.argumentsNeeded

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        if(tailArgs.isEmpty()) {
            if(emptyEffect != null) emptyEffect.invoke(commander)
            else CommandTreeSystem.sendErrorToCommander(commander, "not enough arguments found")
            return
        }
        val triple = validateValue(commander, tailArgs) ?: run{
            CommandTreeSystem.sendErrorToCommander(commander, "not enough arguments found")
            return
        }
        effect.invoke(commander,triple)
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Triple<T,U ,V>? {
        if(tailArgs.isEmpty()) return null
        val firstValue : T = firstLeaf.validateValue(commander, tailArgs) ?: return null
        val secondValue : U = secondLeaf.validateValue(commander, tailArgs.copyOfRange(firstLeaf.argumentsNeeded, tailArgs.size)) ?: return null
        val thirdValue : V =  thirdLeaf.validateValue(commander, tailArgs.copyOfRange(firstLeaf.argumentsNeeded + secondLeaf.argumentsNeeded, tailArgs.size)) ?: return null
        return Triple(firstValue,secondValue,thirdValue)
    }

    // read the comments in the CommandPairLeaf variant, same principle, just 2 instead of 3
    override fun nextTabComplete(commander: Player, fromArg: String, tailArgs: Array<String>): List<String> {
        if(tailArgs.size < firstLeaf.argumentsNeeded){
            // T tab complete
            return firstLeaf.getTabComplete(commander,arrayOf(fromArg) + tailArgs)
        } else{
            // U tab complete
            if(tailArgs.size < firstLeaf.argumentsNeeded + secondLeaf.argumentsNeeded)
                return secondLeaf.getTabComplete(commander,tailArgs.copyOfRange(firstLeaf.argumentsNeeded-1, tailArgs.size))

            // V tab complete
            else
                return thirdLeaf.getTabComplete(commander,tailArgs.copyOfRange(firstLeaf.argumentsNeeded + secondLeaf.argumentsNeeded -1, tailArgs.size))
        }

    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        //T tabComplete
        // we don't have to check, the first parameter always belongs to the first leaf
        return firstLeaf.getTabComplete(commander, arrayOf(currentlyTyping))
    }
}
