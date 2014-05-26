package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import no.minecraft.Minecraftno.handlers.data.WarpData;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class WarpCommand extends MinecraftnoCommand {

    private final WarpHandler wh;

    public WarpCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        this.wh = wh;
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (!this.userHandler.isRegPlayer(player)) {
                WarpData wd = this.wh.getWarp(args[0]);
                if (this.userHandler.getFreeze(player)) {
                    player.sendMessage(getErrorChatColor() + "Du kan ikke bruke denne kommandoen når du er fryst fast.");
                    return true;
                } else {
                    if (wd != null && wd.getWorld() != null) {
                        if (player.isInsideVehicle() && player.getVehicle() instanceof Horse) {
                            Entity veh = player.getVehicle();

                            // Separate player and "vehicle".
                            veh.eject();

                            // Teleport player.
                            player.teleport(wd.getLocation());

                            // Teleport "vehicle".
                            veh.teleport(wd.getLocation());

                            // Place player "in vehicle" again.
                            veh.setPassenger(player);
                        } else {
                            player.teleport(wd.getLocation());
                        }

                        player.sendMessage(getOkChatColor() + "Woosh! Du ble sendt til: " + getVarChatColor() + wd.getName());
                    } else {
                        player.sendMessage(getErrorChatColor() + "Fant ikke warp med navnet: " + getVarChatColor() + args[0]);
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}