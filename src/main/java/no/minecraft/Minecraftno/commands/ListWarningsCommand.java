package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListWarningsCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;
    private final WarningHandler warningHandler;

    public ListWarningsCommand(Minecraftno instance) {
        super(instance);
        this.userHandler = instance.getUserHandler();
        this.warningHandler = instance.getWarningHandler();
        setAccessLevel(1);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            List<String> warningList = this.warningHandler.getWarnings(player);
            if (warningList == null) {
                player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                return true;
            } else {
                for (String s : warningList) {
                    player.sendMessage(s);
                }
                if (this.userHandler.hadWeekBanned(player)) {
                    player.sendMessage(getErrorChatColor() + "Du har hatt ukesban.");
                }
            }
            return true;
        } else if (args.length == 1) {
            if (this.userHandler.getAccess(player) > 2) {
                Player victim = this.plugin.playerMatch(args[0]);
                if (victim != null) {
                    List<String> warningList = this.warningHandler.getWarnings(victim);
                    if (warningList == null) {
                        player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                        return true;
                    } else {
                        for (String s : warningList) {
                            player.sendMessage(s);
                        }
                    }
                } else {
                    String playerName = this.userHandler.getUsernameFromDB(args[0]);
                    if (!playerName.isEmpty()) {
                        List<String> warningList = this.warningHandler.getWarnings(playerName);
                        if (warningList == null) {
                            player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                            return true;
                        } else {
                            for (String s : warningList) {
                                player.sendMessage(s);
                            }
                        }
                        if (this.userHandler.isBanned(playerName) || this.userHandler.isWeekBanned(playerName)) {
                            BanData banData = this.userHandler.getBanData(playerName);
                            if (banData != null) {
                                if (banData.isWeekBan()) {
                                    ArrayList<Integer> data = this.plugin.formatTime(banData);
                                    int d = data.get(0);
                                    int h = data.get(1);
                                    int m = data.get(2);
                                    int s = data.get(3);

                                    String msg = (d > 0 ? d + " d, " : "") + (h > 0 ? h + " t, " : "") + (m > 0 ? m + " m, " : "") + s + " sek igjen.";

                                    player.sendMessage(getErrorChatColor() + "Spilleren er ukesbannet: " + getVarChatColor() + banData.getReason() + getErrorChatColor() + " av: " + getVarChatColor() + banData.getBannerPlayerName());
                                    player.sendMessage(getErrorChatColor() + "Tid igjen: " + getVarChatColor() + msg);
                                    player.sendMessage(getErrorChatColor() + "Dato satt: " + getVarChatColor() + banData.getTimeInDate());
                                } else {
                                    player.sendMessage(getErrorChatColor() + "Spilleren er permbannet: " + getVarChatColor() + banData.getReason() + getErrorChatColor() + " av: " + getVarChatColor() + banData.getBannerPlayerName());
                                    player.sendMessage(getErrorChatColor() + "Dato satt: " + getVarChatColor() + banData.getTimeInDate());
                                }
                            } else {
                                player.sendMessage(getErrorChatColor() + "Klarte ikke å hente ban informasjon!");
                            }
                        }
                        if (this.userHandler.hadWeekBanned(playerName)) {
                            player.sendMessage(getErrorChatColor() + "Spilleren har hatt ukesban.");
                        }
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Fant ikke brukeren: " + args[0] + " i databasen eller ingame");
                    }
                }
            } else {
                List<String> warningList = this.warningHandler.getWarnings(player);
                if (warningList == null) {
                    player.sendMessage(getErrorChatColor() + "Feil: Fikk ikke hentet advarsler for bruker.");
                    return true;
                } else {
                    for (String s : warningList) {
                        player.sendMessage(s);
                    }
                    if (this.userHandler.hadWeekBanned(player)) {
                        player.sendMessage(getErrorChatColor() + "Du har hatt ukesban.");
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
