package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class DelGwarpCommand extends MinecraftnoCommand {

    private final WarpHandler wh;

    public DelGwarpCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        setAccessLevel(4);
        this.wh = wh;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            String warpName = wh.getGwarp(args[0]).getName();
            Location warp = wh.getGwarp(args[0]).getLocation();
            if (warp != null) {
                if (wh.delGwarp(warpName)) {
                    player.sendMessage(getDefaultChatColor() + "Graveområdet " + getVarChatColor() + warpName + getDefaultChatColor() + " ble fjernet.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Graveområdet eksisterer ikke.");
            }
            return true;
        } else {
            return false;
        }
    }
}