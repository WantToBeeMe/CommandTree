package me.wanttobee.commandTree.commandTree

import me.wanttobee.commandTree.WTBMCommands
import org.bukkit.entity.Player


// We are using a composite design
// that means we have branches and leaves
// branches may have new nodes hanging on it
// leaves are normally the end
// in our case however, there can be leaves to contain other leaves lol
// branched: a way of sorting the leaves in groups
// leaves: the parameters which are eventually going to be used for the thing you are trying to do
abstract class ICommandLeaf<T>(argName : String, protected val effect : (Player, T) -> Unit, protected val emptyEffect : ((Player) -> Unit)? = null) : ICommandNode(argName) {
    // some leaves have more arguments, in order to find when this leaf is finished, we need to say how many
    // for example, a position is 3 arguments
    // or varargs has infinite
    // this is needed because in our case we can stack leaves under each other, and we want to know when it ends
    open val argumentsNeeded = 1

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        // by default, if we don't have any arguments we try to invoke the empty effect
        // otherwise we will return an error to the user
        if(tailArgs.isEmpty()) {
            if(emptyEffect != null) emptyEffect.invoke(commander)
            else  WTBMCommands.sendErrorToCommander(commander,"no argument found")
            return
        }
        val value = validateValue(commander, tailArgs) ?: return
        effect.invoke(commander,value)
    }

    // branches are just other grouping up the different leaves
    // but leaves are really values which are eventually used (like booleans or something)
    abstract fun validateValue(commander : Player, tailArgs: Array<String>) : T?
}
