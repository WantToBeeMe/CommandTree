package me.wanttobee.commandtree.partials

import me.wanttobee.commandtree.CommandTreeSystem
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class IntPartial(argName : String, private val showRangeOptions : Int = 10) : IReturnablePartial<Int>(argName) {
    private var staticOptions : Array<Int>? = null
    private var dynamicOptions : ((Player) -> Array<Int>)? = null
    private var staticRange : IntRange? = null
    private var dynamicRange : ((Player) -> IntRange)? = null

    fun setStaticOptions(possibilities : Array<Int>) : IntPartial {
        this.staticOptions = possibilities
        return this
    }
    fun setDynamicOptions(possibilities : (Player) -> Array<Int>) : IntPartial {
        this.dynamicOptions = possibilities
        return this
    }

    fun setStaticRange(range : IntRange) : IntPartial {
        this.staticRange = range
        return this
    }
    fun setStaticRange(min : Int?, max : Int?) : IntPartial {
        if (min == null && max == null)  this.staticRange = null
        else this.staticRange = (min ?: Int.MIN_VALUE)..(max ?: Int.MAX_VALUE)
        return this
    }
    fun setDynamicRange(range : (Player) -> IntRange) : IntPartial {
        this.dynamicRange = range
        return this
    }
    fun setDynamicRange(min : ((Player) -> Int?)?, max : ((Player) -> Int?)?) : IntPartial {
        if (min == null && max == null) this.dynamicRange = null
        else this.dynamicRange = {commander ->
            (min?.invoke(commander) ?: Int.MIN_VALUE)..(max?.invoke(commander) ?: Int.MAX_VALUE)
        }
        return this
    }

    fun getOptions(commander: Player) : Array<Int>? {
        return dynamicOptions?.invoke(commander) ?: staticOptions
    }
    fun getRange(commander: Player) : IntRange? {
        return dynamicRange?.invoke(commander) ?: staticRange
    }

    override fun validateValue(commander: Player, tailArgs: Array<String>): Int? {
        if(tailArgs.isEmpty()) return null
        //  .. can also be considered as no parameter
        if(tailArgs.first().contains("..")){
            if(emptyEffect != null) emptyEffect!!.invoke(commander)
            else CommandTreeSystem.sendErrorToCommander(commander,
                "${ChatColor.RED}these ${ChatColor.GRAY}..${ChatColor.RED} are there to convey that you could type any number ${ChatColor.DARK_RED}(Int)${ChatColor.RED}, but not literally ${ChatColor.GRAY}${tailArgs.first()}" )
            return null
        }

        val number = tailArgs.first().toIntOrNull() ?: run {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "should be an Integer (Int)")
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
        if(number in range) return number

        if(range.first == Int.MIN_VALUE){
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number has to be lower than ${range.last} (or equal)")
        } else if(range.last == Int.MAX_VALUE) {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number has to be higher than ${range.first} (or equal)")
        } else {
            CommandTreeSystem.sendErrorToCommander(commander,
                "${tailArgs.first()} is not a valid number.",
                "number can only be in range of ${range.first} to ${range.last}")
        }
        return null
    }

    override fun thisTabComplete(commander: Player, currentlyTyping: String): List<String> {
        val list = mutableListOf<String>()

        val possibilities = getOptions(commander)
        if(possibilities != null){
            for(p in possibilities)
                if (p.toString().startsWith(currentlyTyping)) list.add(p.toString())
            return list
        }
        val range = getRange(commander) ?: return if (currentlyTyping == "") listOf("..") else emptyList()

        val min = range.first
        val max = range.last

        // we are not going to put hundreds or even thousands of numbers in the tab complete.
        // so instead we use the .. to indicate that you can put any number in
        // and if its between numbers, it means it's in that range
        if ("" == currentlyTyping) {
            if (min == Int.MIN_VALUE && max == Int.MAX_VALUE) list.add("..")
            else if (min == Int.MIN_VALUE) list.add("..<=${max}")
            else if (max == Int.MAX_VALUE) list.add("${min}<=..")
            else{
                if (max - min <= showRangeOptions){
                    for (i in min  .. max)
                        list.add(i.toString())
                } else list.add("$min<=..<=$max")
            }
            return list
        }
        if (max - min <= showRangeOptions){
            for (i in min  .. max)
                if (i.toString().startsWith(currentlyTyping)) list.add(i.toString())
        }
        return list
    }
}
