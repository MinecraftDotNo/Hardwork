package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class AddCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public AddCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player victim = this.plugin.playerMatch(args[0]);
            if (victim != null) {
                if (this.userHandler.getAccess(victim) == 0) { // Brukeren er gjest
                    if (this.userHandler.changeAccessLevel(victim, 1)) {
                        player.sendMessage(getDefaultChatColor() + "Spilleren " + getVarChatColor() + victim.getName() + getDefaultChatColor() + " ble lagt til.");
                        victim.sendMessage(getOkChatColor() + "Du har nå byggerettigheter på serveren.");
                        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                            if (entry.getValue().getAnnonseringer()) {
                                entry.getKey().sendMessage("Velkommen til Hardwork, " + getDefaultChatColor() + victim.getName() + "!");
                            }
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "En feil oppstod under adding av spilleren.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Brukeren har allerede byggetillatelse.");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Fant ikke spiller. Sjekke offline playerlist");
                if (this.userHandler.getAccess(args[0]) == 0) { // Brukeren er gjest
                    if (this.userHandler.changeAccessLevel(args[0], 1)) {
                        player.sendMessage(getDefaultChatColor() + "Spilleren " + getVarChatColor() + args[0] + getDefaultChatColor() + " ble lagt til.");
                    } else {
                        player.sendMessage(getErrorChatColor() + "En feil oppstod under adding av spilleren.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Brukeren har allerede byggetillatelse.");
                }
            }
            return true;
        } else {
            return false;
        }
    }
}