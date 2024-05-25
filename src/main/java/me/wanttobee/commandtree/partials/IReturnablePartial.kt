package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.entity.Player

abstract class IReturnablePartial<T>(argName : String) : ICommandPartial(argName) {
    var effect : ((Player, T) -> Unit)? = null

    open val argumentsNeeded = 1

    open fun setEffect(effect : (Player, T) -> Unit) : IReturnablePartial<T>{
        this.effect = effect
        return this
    }

    abstract fun validateValue(commander : Player, tailArgs: Array<String>) : T?

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        // by default, if we don't have any arguments we try to invoke the empty effect
        // otherwise we will return an error to the user
        if(tailArgs.size < argumentsNeeded) {
            if(emptyEffect != null) emptyEffect!!.invoke(commander)
            else  CommandTreeSystem.sendErrorToCommander(commander,"not enough argument found")
            return
        }
        val value = validateValue(commander, tailArgs) ?: return
        if( effect != null) effect!!.invoke(commander, value)
        else  CommandTreeSystem.sendErrorToCommander(commander,"no effect has been provided")
    }
}
