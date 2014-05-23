package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class IRCKickCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final LogHandler logHandler;

    public IRCKickCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.logHandler = instance.getLogHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "kick";
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        String msg = "Ingen grunn oppgitt, vi trenger ikke en.";
        for (User user : bot.getUsers(channel)) {
            if (user.getNick().equals(sender)) {
                if ((user.getPrefix().equalsIgnoreCase("%")) || (user.getPrefix().equalsIgnoreCase("@"))) {
                    String[] args = message.split(" ");
                    if (args.length > 1) {
                        StringBuilder build = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            build.append(args[i] + " ");
                        }
                        msg = build.toString().trim();
                    }
                    Player kick = this.plugin.playerMatch(args[0]);
                    if (kick != null) {
                        kick.kickPlayer(msg);
                        this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(kick), 0, 0, msg, MinecraftnoLog.KICK);

                        this.plugin.getServer().broadcastMessage(kick.getDisplayName() +
                            ChatColor.YELLOW + " ble kicket: " +
                            ChatColor.WHITE + msg);
                        this.plugin.getServer().broadcastMessage("Kicket av : " +
                            this.userHandler.getIrcToGamePrefix(user) + user.getNick());

                        Double x = kick.getLocation().getX();
                        Double y = kick.getLocation().getY();
                        Double z = kick.getLocation().getZ();
                        Float yaw = kick.getLocation().getYaw();
                        Float pitch = kick.getLocation().getPitch();

                        for (int i = x.intValue() - 2; i <= x.intValue() + 2; i++) {
                            Location strike = new Location(kick.getWorld(), i, y, z, yaw, pitch);
                            kick.getWorld().strikeLightningEffect(strike);
                        }

                        return;
                    } else {
                        bot.sendMessage(sender, Colors.RED + " Fant ikke brukeren du ville kicke.");
                    }
                }
            }
        }
    }
}
