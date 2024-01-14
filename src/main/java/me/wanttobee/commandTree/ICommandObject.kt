package me.wanttobee.commandTree

import me.wanttobee.commandTree.commandTree.ICommandNode

//an interface to build your command tree from
interface ICommandObject {
    // the text that you will see when you enter the 'help' command in minecraft
    val helpText : String
    val baseTree : ICommandNode
}



