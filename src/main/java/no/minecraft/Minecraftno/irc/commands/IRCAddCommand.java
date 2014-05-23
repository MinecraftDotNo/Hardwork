package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.util.Map.Entry;

public class IRCAddCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public IRCAddCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "add";
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        for (User user : bot.getUsers(channel)) {
            if (user.getNick().equals(sender)) {
                if ((user.getPrefix().equalsIgnoreCase("%")) || (user.getPrefix().equalsIgnoreCase("@"))) {
                    String[] args = message.split(" ");
                    if (args.length == 1) {
                        Player victim = this.plugin.playerMatch(args[0]);
                        if (victim != null) {
                            if (this.userHandler.getAccess(victim) == 0) {
                                if (this.userHandler.changeAccessLevel(victim, 1)) {
                                    bot.sendNotice(sender, ("Spilleren " + victim.getName() + " ble lagt til."));
                                    victim.sendMessage("Du har nå byggerettigheter på serveren.");
                                    for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                                        if (entry.getValue().getAnnonseringer()) {
                                            entry.getKey().sendMessage("Velkommen til Hardwork, " + ChatColor.DARK_GREEN + victim.getName() + "!");
                                        }
                                    }
                                } else {
                                    bot.sendNotice(sender, ("En feil oppstod under adding av spilleren."));
                                }
                            } else {
                                bot.sendNotice(sender, ("Brukeren har allerede byggetillatelse."));
                            }
                        } else {
                            bot.sendNotice(sender, "Fant ikke spiller. Sjekke offline playerlist");
                            if (this.userHandler.getAccess(args[0]) == 0) { // Brukeren er gjest
                                if (this.userHandler.changeAccessLevel(args[0], 1)) {
                                    bot.sendNotice(sender, "Spilleren " + args[0] + " ble lagt til.");
                                } else {
                                    bot.sendNotice(sender, "En feil oppstod under adding av spilleren.");
                                }
                            } else {
                                bot.sendNotice(sender, "Brukeren har allerede byggetillatelse.");
                            }
                        }
                    }
                }
            }
        }
    }
}
