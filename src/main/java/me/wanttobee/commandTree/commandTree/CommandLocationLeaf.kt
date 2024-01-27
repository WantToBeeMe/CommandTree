package me.wanttobee.commandTree.commandTree

import me.wanttobee.commandTree.WTBMCommands
import org.bukkit.Location
import org.bukkit.entity.Player

class CommandLocationLeaf(argName : String, effect : (Player, Location) -> Unit, emptyEffect : ((Player) -> Unit)? = null ) : ICommandLeaf<Location>(argName,effect, emptyEffect) {
    override val commandParam: String = "(x:number) (y:number) (z:number)"
    override val argumentsNeeded: Int = 3

    override fun validateValue(commander: Player, tailArgs: Array<String>): Location? {
        if(tailArgs.size < 3) return null
        val x = toLocation(commander, "x", tailArgs[0]) ?: run {
            WTBMCommands.sendErrorToCommander(commander,"${tailArgs[0]} is not a valid number","(x)" )
            return null
        }
        val y = toLocation(commander, "y", tailArgs[1]) ?: run {
            WTBMCommands.sendErrorToCommander(commander,"${tailArgs[1]} is not a valid number","(y)" )
            return null
        }
        val z = toLocation(commander, "z", tailArgs[2]) ?: run {
            WTBMCommands.sendErrorToCommander(commander,"${tailArgs[2]} is not a valid number","(z)" )
            return null
        }
        return Location(commander.world,x,y,z)
    }
    private fun toLocation(sender: Player, direction: String, numberCord: String) : Double?{
        if(numberCord ==  "~") return getCommandersLocation(sender, direction)

        var numberToTranslate = numberCord
        if(numberCord.startsWith('~'))
            numberToTranslate = numberCord.drop(1)
        var cord = numberToTranslate.toDoubleOrNull() ?: return null
        if(numberCord.startsWith('~'))
            cord += getCommandersLocation(sender, direction)
        return cord
    }
    private fun getCommandersLocation(commander : Player, direction: String) : Double{
        return when (direction) {
            "x" -> commander.location.x
            "y" -> commander.location.y
            "z" -> commander.location.z
            else -> -1.0
        }
    }


    override fun nextTabComplete(commander: Player, fromArg: String, tailArgs: Array<String>): List<String> {
        val targetBlock = commander.getTargetBlock(null, 6)
        val blockLocation = if(targetBlock.type.isAir) null else targetBlock.location

        if(tailArgs.size == 1){
            if(tailArgs[0] != ""){
                if(toLocation(commander, "y", tailArgs[0]) == null) return emptyList()
                return listOf(
                   "${tailArgs[0]} ${blockLocation?.blockZ ?: "~"}",
                )
            }
            return listOf(
                "${blockLocation?.blockY ?: "~"}",
                "${blockLocation?.blockY ?: "~"} ${blockLocation?.blockZ ?: "~"}",
            )
        }
        if(tailArgs.size == 2 && tailArgs[1] == "")
            return listOf("${blockLocation?.blockZ ?: "~"}")
        return emptyList()
    }
    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val targetBlock = commander.getTargetBlock(null, 6)
        val blockLocation = if(targetBlock.type.isAir) null else targetBlock.location

        if(currentlyTyping != ""){
            if(toLocation(commander, "x", currentlyTyping) == null) return emptyList()
            return listOf(
                "$currentlyTyping ${blockLocation?.blockY ?: "~"}",
                "$currentlyTyping ${blockLocation?.blockY ?: "~"} ${blockLocation?.blockZ ?: "~"}",
            )
        }
        return listOf(
            "${blockLocation?.blockX ?: "~"}",
            "${blockLocation?.blockX ?: "~"} ${blockLocation?.blockY ?: "~"}",
            "${blockLocation?.blockX ?: "~"} ${blockLocation?.blockY ?: "~"} ${blockLocation?.blockZ ?: "~"}",
        )
    }
}
