package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class MuteCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public MuteCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.displayMuteMenu(player);
        } else if (args.length == 1 || args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                this.listMutedPlayers(player);
            } else if (args[0].equalsIgnoreCase("add")) {
                this.addPlayerToMuteList(player, args[1]);
            } else if (args[0].equalsIgnoreCase("remove")) {
                this.removePlayerFromMuteList(player, args[1]);
            } else {
                player.sendMessage(getErrorChatColor() + "Ugyldig kommando.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "For mange argumenter.");
        }

        return true;
    }

    /**
     * Displays the mute list for the given player
     *
     * @param player Player issuing the command
     */
    private void displayMuteMenu(Player player) {
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "mute list" + getVarChatColor() + " - Viser en liste over alle påloggede spillere som er mutet.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "mute add spiller" + getVarChatColor() + " - Muter gitt spiller.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "mute remove spiller" + getVarChatColor() + " - Unmuter gitt spiller.");
    }

    /**
     * Displays a list of all online muted players
     *
     * @param player Players issuing the command
     */
    private void listMutedPlayers(Player player) {
        List<String> mutedPlayers = new ArrayList<String>();
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getMute()) {
                mutedPlayers.add(entry.getKey().getName());
            }
        }

        if (mutedPlayers.size() != 0) {
            StringBuilder build = new StringBuilder();
            for (String string : mutedPlayers) {
                build.append(string + ", ");
            }
            build.substring(0, build.length() - 2);
            build.append('.');
            player.sendMessage(getInfoChatColor() + "Spillere som er muted: " + build.toString());
        } else {
            player.sendMessage(getInfoChatColor() + "Ingen spillere online er mutet.");
        }
    }

    /**
     * Handles the add part of MuteCommand
     *
     * @param player     Player issuing the command
     * @param victimName Name of player to be muted
     */
    private void addPlayerToMuteList(Player player, String victimName) {
        Player victim = this.plugin.playerMatch(victimName);

        if (victim != null) {
            if (!this.userHandler.getMute(victim)) {
                this.userHandler.setMute(victim, true);
                player.sendMessage(getOkChatColor() + "Spilleren er nå mutet.");
                victim.sendMessage(getErrorChatColor() + "Du har blitt mutet. Kontakt en vakt eller stab når du er klar for å oppføre deg igjen.");
                this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " mutet " + victim.getName() + ".");
            } else {
                player.sendMessage(getErrorChatColor() + "Spilleren er allerede mutet.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Spilleren er ikke online eller finnes ikke.");
        }
    }

    /**
     * Handles the remove part of MuteCommand
     *
     * @param player     Player issuing the command
     * @param victimName Name of player to be unmuted
     */
    private void removePlayerFromMuteList(Player player, String victimName) {
        Player victim = this.plugin.playerMatch(victimName);

        if (victim != null) {
            if (this.userHandler.getMute(victim)) {
                this.userHandler.setMute(victim, false);
                player.sendMessage(getOkChatColor() + "Spilleren er nå unmutet.");
                victim.sendMessage(getOkChatColor() + "Du er ikke lengre mutet. Husk å oppføre deg!");
                this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " unmutet " + victim.getName() + ".");
            } else {
                player.sendMessage(getErrorChatColor() + "Spilleren er ikke mutet.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "Spilleren er ikke online eller finnes ikke.");
        }
    }
}
