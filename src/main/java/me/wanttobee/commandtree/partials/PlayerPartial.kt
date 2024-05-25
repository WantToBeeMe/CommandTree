package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerPartial(argName: String) : IReturnablePartial<Player>(argName){
    private var playerMustBeOnline = true
    private var removeCommanderFromOptions = false
    private var allowRSelector = false
    private var allowASelector = false
    private var allowSSelector = false
    // @p is not an option since these commands can only be run from a player
    //  and so @p will always just be the caller
    private var staticOptions : Array<Player>? = null
    private var dynamicOptions : ((Player) -> Array<Player>)? = null

    // note that this flag only applies to players when it was not given in the options list
    fun setPlayerMustBeOnline(mustBeOnline : Boolean) : PlayerPartial {
        this.playerMustBeOnline = mustBeOnline
        return this
    }

    fun setStaticOptions(possibilities : Array<Player>) : PlayerPartial {
        this.staticOptions = possibilities
        return this
    }
    fun setDynamicOptions(possibilities : (Player) -> Array<Player>) : PlayerPartial {
        this.dynamicOptions = possibilities
        return this
    }

    fun setAllowTargetSelectors(atS: Boolean, atR: Boolean, atA : Boolean) : PlayerPartial {
        this.allowRSelector = atR
        this.allowASelector = atA
        this.allowSSelector = atS
        return this
    }
    fun exceptCommander(exceptCommander : Boolean) : PlayerPartial {
        this.removeCommanderFromOptions = exceptCommander
        return this
    }

    private fun getOptions(commander: Player) : Array<Player>? {
        return (dynamicOptions?.invoke(commander) ?: staticOptions)?.let {
            if(removeCommanderFromOptions) it.filter { it != commander }.toTypedArray()
            else it
        }
    }
    private fun getOnlinePlayers(commander: Player) : Collection<Player> {
        return Bukkit.getOnlinePlayers().let {
            if(removeCommanderFromOptions) it.filter { it != commander }
            else it
        }
    }

    private var didAtA = false
    override fun onCommand(commander: Player, tailArgs: Array<String>) {
        if(tailArgs.isEmpty()) {
            if(emptyEffect != null) emptyEffect!!.invoke(commander)
            else  CommandTreeSystem.sendErrorToCommander(commander,"not enough argument found")
            return
        }


        val value = validateValue(commander, tailArgs)
        if( effect != null) {
            if (value != null)
                effect!!.invoke(commander, value)
            else if(didAtA){
                getOnlinePlayers(commander).forEach {
                    if(!removeCommanderFromOptions || it != commander)
                        effect!!.invoke(commander, it)
                }
            }
            didAtA = false
        }
        else  CommandTreeSystem.sendErrorToCommander(commander,"no effect has been provided")
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Player? {
        if(tailArgs.isEmpty()) return null
        if(tailArgs.first().startsWith("@")){
            if(allowASelector && tailArgs.first() == "@a"){
                didAtA = true
                return null
            }
            if(allowASelector &&  tailArgs.first() == "@r")
                return getOnlinePlayers(commander).random()
            if(allowASelector &&  tailArgs.first() == "@s")
                return commander
            CommandTreeSystem.sendErrorToCommander(commander,tailArgs.first(),"is not a allowed" )
            return null
        }

        val possiblePlayers : Array<Player>? = getOptions(commander)
        if(possiblePlayers != null){
            for(pos in possiblePlayers){
                if(pos.name.lowercase() == tailArgs.first().lowercase())
                    return pos
            }
            CommandTreeSystem.sendErrorToCommander(commander,tailArgs.first(),"was not one of the options" )
            return null
        }

        for (player in getOnlinePlayers(commander)){
            if(player.name.lowercase() == tailArgs.first().lowercase())
                return player
        }
        if(removeCommanderFromOptions){
            for (player in Bukkit.getOnlinePlayers()){
                if(player.name.lowercase() == tailArgs.first().lowercase())
                    CommandTreeSystem.sendErrorToCommander(commander, "you cant enter yourself" )
                    return null
            }
        }
        val offlinePlayer = Bukkit.getOfflinePlayer(tailArgs.first()).player
        if(offlinePlayer == null){
            CommandTreeSystem.sendErrorToCommander(commander,tailArgs.first(),"has never been seen on this server" )
            return  null
        }
        if(playerMustBeOnline){
            CommandTreeSystem.sendErrorToCommander(commander,tailArgs.first(),"is not online" )
            return null
        }
        return offlinePlayer
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val playerList = getOptions(commander)?.toList() ?: getOnlinePlayers(commander)
        val list = mutableListOf<String>()
        if (currentlyTyping.isEmpty() || currentlyTyping[0] == '@') {
            if(allowASelector) list.add("@a")
            if(allowRSelector) list.add("@r")
            if(allowSSelector) list.add("@s")
        }

        if( playerList.isEmpty() && currentlyTyping.isEmpty() && list.isEmpty()){
            return listOf("-")
        }

        for (pos in playerList) {
            if (pos.name.lowercase().contains(currentlyTyping.lowercase()))
                list.add(pos.name)
        }
        return list
    }
}
