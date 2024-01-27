package me.wanttobee.commandtree

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object CommandTreeSystem {
    private lateinit var minecraftPlugin : JavaPlugin
    var title : String? = null
        private set

    fun initialize(plugin: JavaPlugin, title: String?){
        minecraftPlugin = plugin
        CommandTreeSystem.title = title
    }

    fun createCommand(command : String,  commandObject : IPlayerCommands){
        minecraftPlugin.getCommand(command)?.setExecutor(commandObject)
        minecraftPlugin.getCommand(command)?.tabCompleter = commandObject
    }

    fun createCommand(commandObject : ICommandNamespace){
        minecraftPlugin.getCommand(commandObject.commandName)?.setExecutor(commandObject)
        minecraftPlugin.getCommand(commandObject.commandName)?.tabCompleter = commandObject
    }

    fun sendErrorToCommander(commander: Player, errorMessage: String, extraInfo : String = ""){
        val titleBit = title ?: ""
        commander.sendMessage("$titleBit ${ChatColor.RED}$errorMessage ${ChatColor.GRAY}$extraInfo")
    }
}
