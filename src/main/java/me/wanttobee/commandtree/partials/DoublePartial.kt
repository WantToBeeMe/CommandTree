package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class DoublePartial(argName : String) : IReturnablePartial<Double>(argName) {
    private var staticOptions : Array<Double>? = null
    private var dynamicOptions : ((Player) -> Array<Double>)? = null
    private var staticRange : Pair<Double?,Double?>? = null
    private var dynamicRange : ((Player) -> Pair<Double?,Double?>)? = null

    fun setStaticOptions(possibilities : Array<Double>) : DoublePartial {
        this.staticOptions = possibilities
        return this
    }
    fun setDynamicOptions(possibilities : (Player) -> Array<Double>) : DoublePartial {
        this.dynamicOptions = possibilities
        return this
    }

    fun setStaticRange(range : IntRange) : DoublePartial {
        this.staticRange = range.first.toDouble() to range.last.toDouble()
        return this
    }
    fun setStaticRange(min : Double?, max : Double?) : DoublePartial {
        if (min == null && max == null)  this.staticRange = null
        else this.staticRange = min to max
        return this
    }
    fun setDynamicRange(range : (Player) -> IntRange) : DoublePartial {
        this.dynamicRange = {commander ->
            val r = range.invoke(commander)
            r.first.toDouble() to r.last.toDouble()
        }
        return this
    }
    fun setDynamicRange(min : ((Player) -> Double?)?, max : ((Player) -> Double?)?) : DoublePartial {
        if (min == null && max == null) this.dynamicRange = null
        else this.dynamicRange = {commander ->
            min?.invoke(commander) to max?.invoke(commander)
        }
        return this
    }

    fun getOptions(commander: Player) : Array<Double>? {
        return dynamicOptions?.invoke(commander) ?: staticOptions
    }
    fun getRange(commander: Player) : Pair<Double?,Double?>? {
        return dynamicRange?.invoke(commander) ?: staticRange
    }
    private fun inRange(number: Double, range: Pair<Double?,Double?>) : Boolean {
        if(range.first == null && range.second == null) return true
        if(range.first == null) return number <= range.second!!
        if(range.second == null) return range.first!! <= number
        return range.first!! <= number && number <= range.second!!
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Double? {
        if(tailArgs.isEmpty()) return null
        //  .. can also be considered as no parameter
        if(tailArgs.first().contains("..")){
            if(emptyEffect != null) emptyEffect!!.invoke(commander)
            else CommandTreeSystem.sendErrorToCommander(commander,
                "${ChatColor.RED}these ${ChatColor.GRAY}..${ChatColor.RED} are there to convey that you could type any number ${ChatColor.DARK_RED}(Double/Float)${ChatColor.RED}, but not literally ${ChatColor.GRAY}${tailArgs.first()}" )
            return null
        }

        val number = tailArgs.first().toDoubleOrNull() ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "should be a Double/Float")
            return null
        }

        val possibilities = getOptions(commander)
        if(possibilities != null){
            if(possibilities.contains(number)) return number
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "you must chose from one of the suggested once")
            return null
        }
        val range = getRange(commander) ?: return number
        if( inRange(number, range) ) return number

        if(range.first == null){
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number has to be lower than ${range.second} (or equal)")
        } else if(range.second == null) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number has to be higher than ${range.first} (or equal)")
        } else {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number can only be in range of ${range.first} to ${range.second}")
        }
        return null
    }


    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val possibilities = getOptions(commander)

        val list = mutableListOf<String>()
        if(possibilities != null){
            for(p in possibilities)
                if (p.toString().startsWith(currentlyTyping)) list.add(p.toString())
            return list
        }

        if ("" != currentlyTyping) return emptyList()
        val range = getRange(commander) ?: return listOf("..")
        val min = range.first
        val max = range.second
        // we are not going to put hundreds or even thousands of numbers in the tab complete.
        // so instead we use the .. to indicate that you can put any number in
        // and if its between numbers, it means it's in that range
        return if (min == null && max == null) listOf("..")
        else if (min ==null) listOf("..<=${max}")
        else if (max == null) listOf("${min}<=..")
        else listOf("$min<=..<=$max")
    }
}
