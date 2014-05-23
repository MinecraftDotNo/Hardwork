package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class DelCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public DelCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
        	String victim = args[0];
        	int access = 0;
        	
        	/* If online, then get full nick and get access from player cache. */
            Player victimPlayer = this.plugin.playerMatch(args[0]);
            if (victimPlayer != null) {
            	victim = victimPlayer.getName();
            	access = this.userHandler.getAccess(victimPlayer);
            } else {
                access = this.userHandler.getAccess(victim);
            }
            
            if (access != 0) {
            	if (this.userHandler.delUser(victim)) {
            		player.sendMessage(getOkChatColor() + "Spilleren " + getVarChatColor() + victim + getOkChatColor() + " ble satt til gjest.");
            	} else {
            		player.sendMessage(getErrorChatColor() + "En feil oppstod under fjerning av byggetillatelsen.");
            	}
            } else {
            	player.sendMessage(getErrorChatColor() + "Fant ikke spilleren.");
            }
            
            return true;
        } else {
            return false;
        }
    }
}