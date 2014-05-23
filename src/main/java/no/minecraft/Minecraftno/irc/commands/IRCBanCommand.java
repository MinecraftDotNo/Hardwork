package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class IRCBanCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final LogHandler logHandler;

    public IRCBanCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.logHandler = instance.getLogHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "ban";
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        for (User user : bot.getUsers(channel)) {
            if (user.getNick().equals(sender)) {
                if ((user.getPrefix().equalsIgnoreCase("%")) || (user.getPrefix().equalsIgnoreCase("@"))) {
                    String[] args = message.split(" ");
                    StringBuilder build = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        build.append(args[i] + " ");
                    }
                    Player victim = this.plugin.playerMatch(args[0]);
                    if (victim != null) {
                        if (!build.toString().isEmpty()) {
                            if (this.userHandler.banUser(victim, build.toString(), user.getNick())) {
                                this.userHandler.updatePlayer(victim, 5);
                                victim.kickPlayer(build.toString());
                                this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(victim), 0, 0, build.toString(), MinecraftnoLog.BAN);
                                this.plugin.getServer().broadcastMessage(victim.getDisplayName() +
                                    ChatColor.DARK_GREEN + " ble bannet: " +
                                    ChatColor.WHITE + build.toString());
                                this.plugin.getServer().broadcastMessage("Bannet av : " +
                                    this.userHandler.getIrcToGamePrefix(user) + user.getNick());
                            } else {
                                bot.sendMessage(sender, Colors.RED + "Fikk ikke satt ban, MySQL-feil.");
                            }
                        } else {
                            bot.sendMessage(sender, Colors.RED + "Du må spesifisere en grunn for å banne spilleren.");
                        }
                    } else {
                        // Her er victim == null, men objektet brukes. MÅ FIKSES!
                        String victimName = this.userHandler.getUsernameFromDB(args[0]);
                        if (!build.toString().isEmpty()) {
                            if (!this.userHandler.isBanned(victimName)) {
                                if (this.userHandler.banUser(victimName, build.toString(), user.getNick())) {
                                    this.userHandler.updatePlayer(victim, 5);
                                    this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(victim), 0, 0, build.toString(), MinecraftnoLog.BAN);
                                    this.plugin.getServer().broadcastMessage(UserHandler.getPrefix(this.userHandler.getAccess(victim)) + victimName + ChatColor.DARK_GREEN + " ble bannet: " + ChatColor.WHITE + build.toString());
                                    this.plugin.getServer().broadcastMessage("Bannet av : " + this.userHandler.getIrcToGamePrefix(user) + user.getNick());
                                } else {
                                    bot.sendMessage(sender, Colors.RED + "Fikk ikke satt ban, MySQL-feil.");
                                }
                            } else {
                                bot.sendMessage(sender, Colors.RED + "Brukeren er allerede bannet!");
                            }
                        } else {
                            bot.sendMessage(sender, Colors.RED + "Du må spesifisere en grunn for å banne spilleren.");
                        }
                    }
                }
            }
        }
    }
}
