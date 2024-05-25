package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.Location
import org.bukkit.entity.Player

class LocationPartial(argName : String) : IReturnablePartial<Location>(argName) {
    override val argumentsNeeded: Int = 3

    override fun validateValue(commander: Player, tailArgs: Array<String>): Location? {
        if(tailArgs.size < 3) return null

        val x = toAxisPoint(commander, "x", tailArgs[0]) ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs[0]} is not a valid number","(x)" )
            return null
        }
        val y = toAxisPoint(commander, "y", tailArgs[1]) ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs[1]} is not a valid number","(y)" )
            return null
        }
        val z = toAxisPoint(commander, "z", tailArgs[2]) ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,"${tailArgs[2]} is not a valid number","(z)" )
            return null
        }
        return Location(commander.world,x,y,z)
    }

    private fun toAxisPoint(sender: Player, direction: String, numberCord: String) : Double?{
        if(numberCord ==  "~") return getCommandersAxisPoint(sender, direction)

        var numberToTranslate = numberCord
        if(numberCord.startsWith('~'))
            numberToTranslate = numberCord.drop(1)
        var cord = numberToTranslate.toDoubleOrNull() ?: return null
        if(numberCord.startsWith('~'))
            cord += getCommandersAxisPoint(sender, direction)
        return cord
    }
    private fun getCommandersAxisPoint(commander : Player, direction: String) : Double{
        return when (direction) {
            "x" -> commander.location.x
            "y" -> commander.location.y
            "z" -> commander.location.z
            else -> -1.0
        }
    }


    override fun nextTabComplete(commander: Player, thisArg: String, followingArgs: Array<String>): List<String> {
        val targetBlock = commander.getTargetBlock(null, 6)
        val blockLocation = if(targetBlock.type.isAir) null else targetBlock.location

        if(followingArgs.size == 1){
            if(followingArgs[0] != ""){
                if(toAxisPoint(commander, "y", followingArgs[0]) == null) return emptyList()
                return listOf(
                    "${followingArgs[0]} ${blockLocation?.blockZ ?: "~"}",
                )
            }
            return listOf(
                "${blockLocation?.blockY ?: "~"}",
                "${blockLocation?.blockY ?: "~"} ${blockLocation?.blockZ ?: "~"}",
            )
        }
        if(followingArgs.size == 2 && followingArgs[1] == "")
            return listOf("${blockLocation?.blockZ ?: "~"}")
        return emptyList()
    }
    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val targetBlock = commander.getTargetBlock(null, 6)
        val blockLocation = if(targetBlock.type.isAir) null else targetBlock.location

        if(currentlyTyping != ""){
            if(toAxisPoint(commander, "x", currentlyTyping) == null) return emptyList()
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
