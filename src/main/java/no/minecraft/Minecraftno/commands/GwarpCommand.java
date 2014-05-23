package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import no.minecraft.Minecraftno.handlers.data.WarpData;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GwarpCommand extends MinecraftnoCommand {

    private final WarpHandler wh;

    public GwarpCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        this.wh = wh;
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            WarpData wd = this.wh.getGwarp(args[0]);
            if (this.userHandler.getFreeze(player)) {
                player.sendMessage(getErrorChatColor() + "Du kan ikke bruke denne kommandoen når du er fryst fast.");
                return true;
            } else {
                if (wd != null && wd.getWorld() != null) {
                    Logger.getLogger("Minecraft").log(Level.INFO, "[Minecraftno] Graveområde som ble skrevet var: " + args[0]);
                    player.teleport(wd.getLocation());
                    player.sendMessage(getOkChatColor() + "Woosh! Du ble sendt til graveområdet: " + getVarChatColor() + wd.getName());
                } else {
                    player.sendMessage(getErrorChatColor() + "Fant ikke graveområde med navnet: " + getVarChatColor() + args[0]);
                }
            }
        } else {
            return false;
        }
        return true;
    }
}