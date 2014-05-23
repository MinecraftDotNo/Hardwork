package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetGwarpCommand extends MinecraftnoCommand {

    private final WarpHandler wh;
    private final LogHandler logHandler;

    public SetGwarpCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        setAccessLevel(3);
        this.wh = wh;
        this.logHandler = instance.getLogHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            try {
                boolean success = this.wh.setGwarp(args[0], player.getLocation());

                if (success) {
                    player.sendMessage(getOkChatColor() + "Graveområde satt: " + getVarChatColor() + args[0]);
                    this.logHandler.log(this.userHandler.getUserId(player), 0, 0, 0, args[0], MinecraftnoLog.SETWARP);
                } else {
                    player.sendMessage(getErrorChatColor() + "Navnet kan bare inneholde bokstaver (" + getVarChatColor() + args[0] + getErrorChatColor() + ")");
                }
            } catch (Exception e) {
                player.sendMessage(getErrorChatColor() + "Graveområde ble ikke lagret (Lagringsfeil) (" + getVarChatColor() + args[0] + getErrorChatColor() + ")");
            }
            return true;
        } else {
            return false;
        }
    }
}