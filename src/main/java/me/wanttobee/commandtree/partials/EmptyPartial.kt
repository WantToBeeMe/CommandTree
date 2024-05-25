package me.wanttobee.commandtree.partials

import org.bukkit.entity.Player

class EmptyPartial(argName: String ) : ICommandPartial(argName) {
    // just an alias since all other partials have this method as-well
    // for them the effect and empty effect are different, but for this special case they would do the same anyway
    fun setEffect(effect: (Player) -> Unit): EmptyPartial {
        this.emptyEffect = effect
        return this
    }

    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        emptyEffect?.invoke(commander)
    }
    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
       return emptyList()
    }
}
