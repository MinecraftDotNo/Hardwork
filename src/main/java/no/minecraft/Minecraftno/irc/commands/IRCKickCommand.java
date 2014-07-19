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
        // Finn User-objektet for personen som sendte kommandoen.
        User user = null;
        for (User test : bot.getUsers(channel)) {
            if (test.getNick().equals(sender)) {
                user = test;
                break;
            }
        }

        // Better safe than sorry.
        if (user == null)
            return;

        // Bare de vi stoler på får kicke spillere.
        if (!user.isOwner() && !user.isAdmin() && !user.isOp() && !user.isHalfOp())
            return;

        String[] args = message.split(" ");

        // Spilleren som skal kickes.
        Player kick = this.plugin.playerMatch(args[0]);

        // Kick-melding.
        String msg = "Ingen grunn oppgitt, vi trenger ikke en.";
        if (args.length > 1) {
            StringBuilder build = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            msg = build.toString().trim();
        }

        // Igjen, better safe than sorry.
        if (kick == null) {
            bot.sendMessage(sender, Colors.RED + " Fant ikke brukeren du ville kicke.");
            return;
        }

        // Kick og logg.
        kick.kickPlayer(msg);
        this.logHandler.log(this.userHandler.getUserId(user.getNick()), this.userHandler.getUserId(kick), 0, 0, msg, MinecraftnoLog.KICK);

        // La spillerne in-game få vite hva som skjer.
        this.plugin.getServer().broadcastMessage(
            kick.getDisplayName() +
            ChatColor.YELLOW + " ble kicket: " +
            ChatColor.WHITE + msg
        );
        this.plugin.getServer().broadcastMessage(
            "Kicket av : " +
            this.userHandler.getIrcToGamePrefix(user) + user.getNick()
        );

        // Lynnedslag!
        Double x = kick.getLocation().getX();
        Double y = kick.getLocation().getY();
        Double z = kick.getLocation().getZ();

        for (int i = x.intValue() - 2; i <= x.intValue() + 2; i++) {
            Location strike = new Location(kick.getWorld(), i, y, z);
            kick.getWorld().strikeLightningEffect(strike);
        }
    }
}
