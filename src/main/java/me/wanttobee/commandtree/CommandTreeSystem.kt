package me.wanttobee.commandtree

import me.wanttobee.commandtree.partials.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object CommandTreeSystem {
    private lateinit var minecraftPlugin : JavaPlugin
    var title : String? = null
        private set

    val allPartials = arrayOf(
        BranchPartial::class,
        EmptyPartial::class,
        BooleanPartial::class,
        StringPartial::class,
        PlayerPartial::class,
        IntPartial::class,
        DoublePartial::class,
        LocationPartial::class,
        VarargPartial::class,
        PairPartial::class,
        TriplePartial::class
    )

    fun initialize(plugin: JavaPlugin, title: String?){
        minecraftPlugin = plugin
        this.title = title
    }

    fun createCommand(commandObject : ITreeCommand){
        createCommand(commandObject.command.argName, commandObject)
    }
    fun createCommand(command : String,  commandObject : IPlayerCommandExecutor){
        minecraftPlugin.getCommand(command)?.setExecutor(commandObject)
        minecraftPlugin.getCommand(command)?.tabCompleter = commandObject
    }

    fun sendErrorToCommander(commander: Player, errorMessage: String, extraInfo : String = ""){
        val titleBit = title ?: ""
        commander.sendMessage("$titleBit ${ChatColor.RED}$errorMessage ${ChatColor.GRAY}$extraInfo")
    }
}
