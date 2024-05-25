package me.wanttobee.commandtree.examples

import me.wanttobee.commandtree.Description
import me.wanttobee.commandtree.ITreeCommand
import me.wanttobee.commandtree.partials.*
import org.bukkit.ChatColor

object GroupCommand : ITreeCommand {
    override val description = Description("to show you some more complex commands you can make",)
        .addSubDescription(name="pair",        description="This is my cool pair tree",        usage= "/mygroup pair <bool> <int>")
        .addSubDescription(name="message", description="This is my cool message tree", usage= "/mygroup message <message>")
        .addSubDescription(name= "more",     description="moreee!!",                              usage= "/mygroup more")

    override val command = BranchPartial("mygroup").setStaticPartials(
        PairPartial<Boolean,Int>("pair").setPartials(
            BooleanPartial("bool"),
            IntPartial("int").setStaticRange(1..10)
        ).setEffect {commander, value -> commander.sendMessage("${ChatColor.LIGHT_PURPLE}$value")},

        StringPartial("message").setStaticOptions(arrayOf("first_option", "option_2", "other_option"))
            .setEffect {commander, message -> commander.sendMessage("${ChatColor.LIGHT_PURPLE}$message")},

        BranchPartial("more").setStaticPartials(
            EmptyPartial("even_more")
                .setEffect {commander -> commander.sendMessage("${ChatColor.GOLD}even more")}
        )
    )
}