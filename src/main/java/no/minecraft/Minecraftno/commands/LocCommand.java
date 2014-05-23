package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LocCommand extends MinecraftnoCommand {

    public LocCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        Location loc = player.getLocation();
        if (args.length == 0) {
            player.sendMessage(getOkChatColor() + "Din blokkposisjon:" + "X: " + getVarChatColor() + loc.getBlockX() + getDefaultChatColor() + " Y: " + getVarChatColor() + loc.getBlockY() + getDefaultChatColor() + " Z: " + getVarChatColor() + loc.getBlockZ() + getDefaultChatColor() + " Verden: " + getVarChatColor() + loc.getWorld().getName());
        } else if (args.length == 1) {
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                victim.sendMessage(getOkChatColor() + player.getName() + " sendte sin blokkposisjon: " + getOkChatColor() + "X: " + getVarChatColor() + loc.getBlockX() + getDefaultChatColor() + " Y: " + getVarChatColor() + loc.getBlockY() + getDefaultChatColor() + " Z: " + getVarChatColor() + loc.getBlockZ() + getDefaultChatColor() + " Verden: " + getVarChatColor() + loc.getWorld().getName());
                player.sendMessage(getOkChatColor() + "Du sendte din blokkposisjon til: " + victim.getName());
            } else {
                player.sendMessage(getOkChatColor() + "Ingen spiller med det navne.");
            }
        } else {
            return false;
        }
        return true;
    }
}
