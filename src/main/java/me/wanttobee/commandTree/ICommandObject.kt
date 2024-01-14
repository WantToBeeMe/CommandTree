package me.wanttobee.commandTree

import me.wanttobee.commandTree.commandTree.ICommandNode

//an interface to build your command tree from
interface ICommandObject {

    val helpText : String

    val baseTree : ICommandNode
}



