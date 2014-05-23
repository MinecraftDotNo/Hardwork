package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.TreeSet;

public class WarpsCommand extends MinecraftnoCommand {

    private final WarpHandler wh;

    public WarpsCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        this.wh = wh;
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "Warps: " + ChatColor.RESET + StringUtils.join(new TreeSet(this.wh.getWarpList()), ChatColor.DARK_GREEN + ", " + ChatColor.RESET));

            return true;
        } else {
            return false;
        }
    }
}
