package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import java.util.Collection;

public class IRCWhoCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public IRCWhoCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "who";
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        if (this.plugin.getServer().getOnlinePlayers().size() > 0) {
            String text = getOnlinePlayers();
            if (text.length() > 512) {
                if (text.length() > 1024) {
                    if (text.length() > 1536) {
                        bot.sendNotice(channel, text.substring(0, 512));
                        bot.sendNotice(channel, text.substring(512, 1024));
                        bot.sendNotice(channel, text.substring(1024, 1536));
                        bot.sendNotice(channel, text.substring(1536, text.length()));
                    } else {
                        bot.sendNotice(channel, text.substring(0, 512));
                        bot.sendNotice(channel, text.substring(512, 1024));
                        bot.sendNotice(channel, text.substring(1024, text.length()));
                    }
                } else {
                    bot.sendNotice(channel, text.substring(0, 512));
                    bot.sendNotice(channel, text.substring(512, text.length()));
                }
            } else {
                bot.sendNotice(channel, text.substring(0, text.length()));
            }
            // Not working for some weird shit!
            /*while (offset < text.length()) {
                int size = Math.min(256, text.length() - offset);
				System.out.println(Math.min(256, text.length() - offset));
				this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageTaskIrc(bot, channel, text.substring(offset, size)), 20);
				offset += size;
				System.out.println("Running: " + offset + size);
			}*/
            //bot.sendMessage(channel, getOnlinePlayers());
            bot.sendNotice(channel, ("Antall spillere: " + this.plugin.getServer().getOnlinePlayers().size() + "."));
        } else {
            bot.sendNotice(channel, ("Antall spillere: " + this.plugin.getServer().getOnlinePlayers().size() + "."));
        }
    }

    public String getOnlinePlayers() {
        Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();

        Player[] onlinePlayers = players.toArray(new Player[players.size()]);
        String[] tech = this.userHandler.getUsersSortOnIRCAccess(5, onlinePlayers);
        String[] stab = this.userHandler.getUsersSortOnIRCAccess(4, onlinePlayers);
        String[] vakt = this.userHandler.getUsersSortOnIRCAccess(3, onlinePlayers);
        String[] hjelper = this.userHandler.getUsersSortOnIRCAccess(2, onlinePlayers);
        String[] builder = this.userHandler.getUsersSortOnIRCAccess(1, onlinePlayers);
        String[] gjest = this.userHandler.getUsersSortOnIRCAccess(0, onlinePlayers);

        StringBuilder build = new StringBuilder();
        for (int i = 0; i < tech.length; i++) {
            build.append(tech[i] + Colors.NORMAL + ", ");
        }

        for (int i = 0; i < stab.length; i++) {
            build.append(stab[i] + Colors.NORMAL + ", ");
        }

        for (int i = 0; i < vakt.length; i++) {
            build.append(vakt[i] + Colors.NORMAL + ", ");
        }

        for (int i = 0; i < hjelper.length; i++) {
            build.append(hjelper[i] + Colors.NORMAL + ", ");
        }

        for (int i = 0; i < builder.length; i++) {
            build.append(builder[i] + Colors.NORMAL + ", ");
        }

        for (int i = 0; i < gjest.length; i++) {
            build.append(gjest[i] + Colors.NORMAL + ", ");
        }
        String utskrift = "Spillere pålogget: " + build.toString();
        utskrift = utskrift.substring(0, utskrift.length() - 2) + ".";
        return utskrift;
    }
}