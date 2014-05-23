package no.minecraft.Minecraftno.handlers.data;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.Util;
import no.minecraft.Minecraftno.handlers.player.UserHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanData {

    @SuppressWarnings("unused")
    private final Minecraftno plugin;
    private final UserHandler userHandler;
    SimpleDateFormat dateFormat = Util.dateFormat;

    private int player;
    private String reason;
    private int banner;
    private long unixTime;
    private int permban;
    private int weekBan;

    /**
     * @param instance Hardwork-Plugin
     * @param player   int Player ID som skal bannes
     * @param reason   string Grunn til ban
     * @param banner   int Player ID til den som banner en spiller
     * @param time     int tid i unix
     * @param weekBan  boolean true/false om det er ukesban
     */
    public BanData(Minecraftno instance, int player, String reason, int banner, long time, int permban, int weekBan) {
        super();
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();

        this.setPlayer(player);
        this.setReason(reason);
        this.setBanner(banner);
        this.setUnixTime(time);
        this.setPermban(permban);
        this.setWeekBan(weekBan);
    }

    /**
     * @return the player
     */
    public int getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(int player) {
        this.player = player;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        if (reason == null) {
            return "(ukjent grunn)";
        } else {
            return reason;
        }
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the banner
     */
    public int getBanner() {
        return banner;
    }

    /**
     * @param banner the banner to set
     */
    public void setBanner(int banner) {
        this.banner = banner;
    }

    /**
     * @return the unixTime
     */
    public long getUnixTime() {
        return unixTime;
    }

    /**
     * @param unixTime the unixTime to set
     */
    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    /**
     * @return the permban
     */
    public boolean isPermban() {
        return (permban == 1);
    }

    /**
     * @param permban the permban to set
     */
    public void setPermban(int permban) {
        this.permban = permban;
    }

    /**
     * @return the weekBan
     */
    public boolean isWeekBan() {
        return (weekBan == 1);
    }

    /**
     * @param weekBan the weekBan to set
     */
    public void setWeekBan(int weekBan) {
        this.weekBan = weekBan;
    }

    /**
     * @return String Henter dato i vanlig tid
     */
    public String getTimeInDate() {
        Date banDate = new Date(this.unixTime * 1000);
        return dateFormat.format(banDate);
    }

    /**
     * @return String Henter den som bannet sitt navn i steden for id
     */
    public String getBannerPlayerName() {
        return this.userHandler.getNameFromId(banner);
    }

    /**
     * @return String Henter spillerens navn i stenden for id
     */
    public String getPlayerName() {
        return this.userHandler.getNameFromId(player);
    }

    @Override
    public String toString() {
        return "BanData = {player = " + player + " : reason = " + reason + " : banner = " + banner + " : time = " + unixTime + " : + weekban = " + weekBan + "}";
    }
}
