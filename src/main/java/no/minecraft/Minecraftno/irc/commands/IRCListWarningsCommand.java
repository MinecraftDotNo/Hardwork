package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.util.ArrayList;
import java.util.List;

public class IRCListWarningsCommand implements IRCBotCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final WarningHandler warningHandler;

    public IRCListWarningsCommand(Minecraftno instance) {
        this.userHandler = instance.getUserHandler();
        this.warningHandler = instance.getWarningHandler();
        this.plugin = instance;
    }

    public String getCommandName() {
        return "lw";
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
                            List<String> warningList = this.warningHandler.getWarningsIRC(victim);
                            if (warningList == null) {
                                bot.sendNotice(sender, "Feil: Fikk ikke hentet advarsler for bruker.");
                            } else {
                                for (String s : warningList) {
                                    bot.sendNotice(sender, s);
                                }
                            }
                        }
                        String whoName = this.userHandler.getUsernameFromDB(args[0]);
                        List<String> warningList = this.warningHandler.getWarningsIRC(whoName);
                        if (warningList == null) {
                            bot.sendNotice(sender, "Feil: Fikk ikke hentet advarsler for bruker.");
                        } else {
                            for (String s : warningList) {
                                bot.sendNotice(sender, s);
                            }
                        }
                        if (!whoName.isEmpty()) {
                            if (this.userHandler.isBanned(whoName) || this.userHandler.isWeekBanned(whoName)) {
                                BanData banData = this.userHandler.getBanData(whoName);
                                if (banData != null) {
                                    if (banData.isWeekBan()) {
                                        ArrayList<Integer> data = this.plugin.formatTime(banData);
                                        int d = data.get(0);
                                        int h = data.get(1);
                                        int m = data.get(2);
                                        int s = data.get(3);
                                        String msg = (d > 0 ? d + " d, " : "") + (h > 0 ? h + " t, " : "") + (m > 0 ? m + " m, " : "") + s + " sek igjen.";

                                        bot.sendNotice(sender, "Spilleren er ukesbannet: " + banData.getReason() + " av: " + banData.getBannerPlayerName());
                                        bot.sendNotice(sender, "Tid igjen: " + msg);
                                        bot.sendNotice(sender, "Dato satt: " + banData.getTimeInDate());
                                    } else {
                                        bot.sendNotice(sender, "Spilleren er permbannet: " + banData.getReason() + " av: " + banData.getBannerPlayerName());
                                        bot.sendNotice(sender, "Dato satt: " + banData.getTimeInDate());
                                    }
                                } else {
                                    bot.sendNotice(sender, "Klarte ikke å hente ban informasjon!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
