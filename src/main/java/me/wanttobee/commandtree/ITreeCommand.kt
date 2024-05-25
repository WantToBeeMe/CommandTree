package me.wanttobee.commandtree

import me.wanttobee.commandtree.partials.BranchPartial
import me.wanttobee.commandtree.partials.ICommandPartial
import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface ITreeCommand : IPlayerCommandExecutor{
    val description : Description
    val command : ICommandPartial

    override fun skipHelpForZeroParameterCommand(): Boolean {
       return command.emptyEffect != null
    }
    override fun canAddHelpToTabComplete(commander: Player): Boolean {
        if(command !is BranchPartial) return false

        for(branch in (command as BranchPartial).getBranches(commander)){
            if(branch.argName.lowercase() == "help") return false
        }
        return true
    }

    override fun onCommand(commander: Player, args: Array<String>): Boolean {
        command.onCommand(commander,args)
        return true
    }

    override fun onTabComplete(commander: Player, args: Array<String>): List<String> {
        return command.getTabComplete(commander, args)
    }

    override fun help(commander: Player, page : Int){
        val amountPerPage = 8
        val totalPages = (description.subDescriptions.size/amountPerPage)+1
        val page = Math.min(page,totalPages)
        val helperTab : (String)-> String = { h -> "${ChatColor.YELLOW}$h${ChatColor.WHITE}"}
        commander.sendMessage("${ChatColor.GRAY}-========= ${ChatColor.WHITE}$page/$totalPages ${ChatColor.GRAY}=========-")
        if(page == 1) {
            var baseText = "${ChatColor.YELLOW}/${command.argName}${ChatColor.WHITE} ${description.summary}"
            if(CommandTreeSystem.title != null)
                baseText =  "${CommandTreeSystem.title} "+ baseText
            if (description.usage != null)
                baseText += " ${ChatColor.GRAY}${description.usage}"
            commander.sendMessage(baseText)
        }

        for(sysCom in 0 until amountPerPage){
            val index = sysCom + (page-1)*amountPerPage
            if(description.subDescriptions.size <= index) break
            val command = description.subDescriptions[sysCom]
            var baseText = "- ${helperTab(command.first+ ":")} ${command.second}"
            if (command.third != null)
                baseText += " ${ChatColor.GRAY}${command.third}"
            commander.sendMessage(baseText)
        }
        commander.sendMessage("${ChatColor.GRAY}-========= ${ChatColor.WHITE}$page/$totalPages ${ChatColor.GRAY}=========-")
    }
}
