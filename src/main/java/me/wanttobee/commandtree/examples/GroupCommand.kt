package me.wanttobee.commandtree.examples

import me.wanttobee.commandtree.ICommandNamespace
import me.wanttobee.commandtree.ICommandObject
import me.wanttobee.commandtree.nodes.*
import org.bukkit.ChatColor

// to make sure it all works you simply have to do `CommandTreeSystem.createCommand(GroupCommand)`
//  (and make sure the CommandTreeSystem has been initialized )
object GroupCommand  : ICommandNamespace {
    override val commandName: String = "group"
    override val commandSummary: String = "to show you some more complex commands you can make"
    override val systemCommands: Array<ICommandObject> = arrayOf(PairTree)
    override val hasOnlyOneGroupMember: Boolean = false
    override val isZeroParameterCommand: Boolean = false

    // each object start with its own name before you can enter the parameters that the command was based on
    // this is a string command, it would look something like this `/group message [first_option, option_2, other_option]`
    // where you start with `message` and ofter that you get the 3 options where you can choose between in your complete
    object SayTree : ICommandObject {
        override val helpText: String = "This is my cool pair tree"
        override val baseTree: ICommandNode = CommandStringLeaf("message", arrayOf("first_option", "option_2", "other_option"),
            {commander, message -> commander.sendMessage("${ChatColor.RED}$message")}
        )
    }

    // you can also make it so you have multiple options after the group-name
    // this one would look something like `/group pair [true/false] [1..10]`
    // where after pair you get the first option of the pair, which is a boolean,
    // and then you get the second option of the pair, which is a number between 1 and 10
    // you can stack pairs however you want. (make sure that if you do set a vararg in the pair, that it is the last argument of the pair stack, otherwise the pair will never finish)
    // There are some default multiple arguments already, for example the CommandLocationLeaf,
    // if you want 3 arguments, you could stack 2 pairs (so one slot of the first pair will be a new pair, totaling in 3)
    // byt you could also use the Triple instead
    object PairTree : ICommandObject {
        override val helpText: String = "This is my cool pair tree"
        override val baseTree: ICommandNode = CommandPairLeaf("pair",
            CommandBoolLeaf("bool", {_,_ -> } ),
            CommandIntLeaf("int",1,10, {_,_ -> } ),
            {commander, pair -> commander.sendMessage("${ChatColor.RED}${pair.first}${ChatColor.LIGHT_PURPLE}${pair.second}")}
        )
    }
}