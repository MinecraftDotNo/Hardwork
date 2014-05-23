package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class XPCommand extends MinecraftnoCommand {

    public XPCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(getOkChatColor() + "Level: " + player.getLevel());
            player.sendMessage(getOkChatColor() + "XP: " + player.getTotalExperience());
            return true;
        } else {
            return false;
        }
    }
}

