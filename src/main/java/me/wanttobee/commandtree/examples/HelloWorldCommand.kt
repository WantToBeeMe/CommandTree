package me.wanttobee.commandtree.examples

import me.wanttobee.commandtree.ICommandNamespace
import me.wanttobee.commandtree.ICommandObject
import me.wanttobee.commandtree.nodes.CommandEmptyLeaf
import me.wanttobee.commandtree.nodes.ICommandNode
import org.bukkit.ChatColor

// this example is one of the most simple classes
// if you want to have a command that just does 1 word. `/reload` for example, also only is one word
// normally these commands start from that one word and go from there,
// so in this case we would get `/helloWorld say` because we specified that the commandObject is `say`
//  (or in another command this could be `/A b c d e`)
// however, because there is only one possibility `say`, we can set hasOnlyOneGroupMember to true
// this to tell the system that we can skip selecting that bit because it will always be that anyway.
// so you are left with `/helloWorld` (in that other command this would be `/A c d e`)

// by default doing only the command name (with 0 other params) will automatically go to the help command
// however, we can turn that off if we don't want that by setting isZeroParameterCommand to true

// to make sure it all works you simply have to do `CommandTreeSystem.createCommand(HelloWorldCommand)`
//  (and make sure the CommandTreeSystem has been initialized )
object HelloWorldCommand : ICommandNamespace {
    override val commandName: String = "helloWorld"
    override val commandSummary: String = "to say hello world"
    override val systemCommands: Array<ICommandObject> = arrayOf(HelloWorld)
    override val hasOnlyOneGroupMember: Boolean = true
    override val isZeroParameterCommand: Boolean = true

    object HelloWorld : ICommandObject{
        override val helpText: String = "says hello world"
        override val baseTree: ICommandNode = CommandEmptyLeaf("say") { commander ->
            commander.sendMessage("${ChatColor.YELLOW} Hello World")
        }
    }
}
