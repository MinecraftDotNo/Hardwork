package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class IRCUnbanCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final LogHandler logHandler;

    public IRCUnbanCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.logHandler = instance.getLogHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "unban";
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        for (User user : bot.getUsers(channel)) {
            if (user.getNick().equals(sender)) {
                if ((user.getPrefix().equalsIgnoreCase("%")) || (user.getPrefix().equalsIgnoreCase("@"))) {
                    String[] args = message.split(" ");
                    if (args.length > 0) {
                        String victimName = this.userHandler.getUsernameFromDB(args[0]);
                        if (victimName != null) {
                            if (this.userHandler.isBanned(victimName)) {
                                this.userHandler.unBanUser(victimName, false);
                                this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(victimName), 0, 0, null, MinecraftnoLog.UNBAN);
                            } else if (this.userHandler.isWeekBanned(victimName)) {
                                this.userHandler.unBanUser(victimName, true);
                                this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(victimName), 0, 0, null, MinecraftnoLog.UNBAN);
                            } else {
                                bot.sendMessage(sender, ("Brukeren er ikke bannet. (" + victimName + ")"));
                                return;
                            }
                            this.plugin.getServer().broadcastMessage(ChatColor.GREEN + "Brukeren: " + ChatColor.WHITE + victimName + ChatColor.GREEN + " er ikke lengre bannet.");
                        } else {
                            bot.sendMessage(sender, ("Brukeren eksisterer ikke. (" + args[0] + ")"));
                        }
                    }
                }
            }
        }
    }
}
