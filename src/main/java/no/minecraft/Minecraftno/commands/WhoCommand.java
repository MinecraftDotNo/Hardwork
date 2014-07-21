package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class WhoCommand extends MinecraftnoCommand {
    private final Minecraftno plugin;

    private final UserHandler userHandler;
    private final WarningHandler warningHandler;

    public WhoCommand(Minecraftno instance) {
        super(instance);

        setAccessLevel(0);

        this.plugin = instance;

        this.userHandler = instance.getUserHandler();
        this.warningHandler = instance.getWarningHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player[] onlinePlayers = this.plugin.getServer().getOnlinePlayers();
            String utskrift;
            StringBuilder build = new StringBuilder();

            String[] tech = this.userHandler.getUsersSortOnAccess(5, onlinePlayers);
            String[] stab = this.userHandler.getUsersSortOnAccess(4, onlinePlayers);
            String[] vakt = this.userHandler.getUsersSortOnAccess(3, onlinePlayers);
            String[] hjelper = this.userHandler.getUsersSortOnAccess(2, onlinePlayers);
            String[] builder = this.userHandler.getUsersSortOnAccess(1, onlinePlayers);
            String[] gjest = this.userHandler.getUsersSortOnAccess(0, onlinePlayers);

            for (String aTech : tech) {
                build.append(aTech).append(getVarChatColor()).append(", ");
            }
            for (String aStab : stab) {
                build.append(aStab).append(getVarChatColor()).append(", ");
            }
            for (String aVakt : vakt) {
                build.append(aVakt).append(getVarChatColor()).append(", ");
            }
            for (String aHjelper : hjelper) {
                build.append(aHjelper).append(getVarChatColor()).append(", ");
            }
            for (String aBuilder : builder) {
                build.append(aBuilder).append(getVarChatColor()).append(", ");
            }
            for (String aGjest : gjest) {
                build.append(aGjest).append(getVarChatColor()).append(", ");
            }

            utskrift = getVarChatColor() + "Spillere pålogget: " + build.toString();
            utskrift = utskrift.substring(0, utskrift.length() - 2) + ".";

            player.sendMessage(utskrift);
            player.sendMessage("Antall spillere: " + onlinePlayers.length + ".");

            return true;
        }

        if (args.length == 1) {
            Player victim = this.plugin.playerMatch(args[0]);

            if (victim != null) { // Spilleren er online på serveren.
                player.sendMessage(getOkChatColor() + victim.getName() + getVarChatColor() + " er online på serveren nå.");

                if (this.userHandler.getAccess(player) > 2) {
                    player.sendMessage("Denne brukeren har " + this.warningHandler.countWarnings(victim.getName()) + " Advarsler");
                }

                return true;
            }

            // Spilleren er ikke online, henter informasjon fra databasen.
            long utime = 0;
            int name = 0;

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement("SELECT time, name FROM userlog WHERE name=? ORDER BY time DESC LIMIT 1");
                ps.setInt(1, this.userHandler.getUserId(args[0]));
                rs = ps.executeQuery();

                while (rs.next()) {
                    utime = rs.getLong("time");
                    name = rs.getInt("name");
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning, WhoCommand)", ex);
            } finally {
                try {
                    if (ps != null)
                        ps.close();

                    if (rs != null)
                        rs.close();

                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under lukking, WhoCommand)", ex);
                }
            }

            if (name != 0 && utime != 0) {
                String uname = this.userHandler.getNameFromId(name);

                SimpleDateFormat date = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
                Date time = new Date(utime * 1000);

                String extra = "";
                if (this.userHandler.isBanned(uname) || this.userHandler.isWeekBanned(uname)) {
                    BanData banData = this.userHandler.getBanData(uname);

                    if (banData != null) {
                        if (banData.isWeekBan()) {
                            ArrayList<Integer> data = this.plugin.formatTime(banData);
                            int d = data.get(0);
                            int h = data.get(1);
                            int m = data.get(2);
                            int s = data.get(3);

                            String msg = (d > 0 ? d + " d, " : "") + (h > 0 ? h + " t, " : "") + (m > 0 ? m + " m, " : "") + s + " sek igjen.";
                            extra = " og er ukesbannet, det er " + getVarChatColor() + msg;
                        } else {
                            extra = " og ble bannet " + getVarChatColor() + banData.getTimeInDate();
                        }
                    }
                }

                player.sendMessage(uname + getErrorChatColor() + " ble sist sett på Hardwork: (" + getVarChatColor() + date.format(time) + getErrorChatColor() + ")" + extra);

                if (this.userHandler.getAccess(player) > 2) {
                    player.sendMessage(getErrorChatColor() + "Denne brukeren har " + getVarChatColor() + this.warningHandler.countWarnings(args[0]) + getErrorChatColor() + " advarsler.");
                }

                return true;
            }

            player.sendMessage(getErrorChatColor() + "Fant ikke informasjon om spilleren " + args[0] + ".");

            return true;
        }

        return false;
    }
}
