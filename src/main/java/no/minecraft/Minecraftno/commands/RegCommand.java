package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class RegCommand extends MinecraftnoCommand {
    public RegCommand(Minecraftno instance) {
        super(instance);

        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        for (String msg : this.plugin.getGlobalConfiguration().srvReg) {
            player.sendMessage(ChatColor.GOLD + msg);
        }
        return true;
    }
}