package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class FreezeCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public FreezeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.displayMenu(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                this.listFreezedPlayers(player);
            } else {
                player.sendMessage(getErrorChatColor() + "Ukjent kommando. Se " + getCommandChatColor() + "/freeze" + getErrorChatColor() + " for en liste over tilgjengelige freeze-kommandoer.");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                Player victim = this.plugin.playerMatch(args[1]);
                if (victim != null) {
                    if (this.userHandler.getAccess(victim) < 3) {
                        if (!this.userHandler.getFreeze(victim)) {
                            this.freezeAddPlayer(player, victim);
                        } else {
                            player.sendMessage(getErrorChatColor() + "Spilleren er allerede fryst.");
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du kan ikke fryse medlemmer av staben (kun gamle folk med stokk).");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Spilleren må være pålogget for å bli fryst.");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                Player victim = this.plugin.playerMatch(args[1]);
                if (victim != null) {
                    if (this.userHandler.getFreeze(victim)) {
                        this.freezeRemovePlayer(player, victim);
                    } else {
                        player.sendMessage(getErrorChatColor() + "Spilleren er ikke fryst.");
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Spilleren må være pålogget for å kunne \"tines\".");
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Ukjent kommando.");
            }
        } else {
            player.sendMessage(getErrorChatColor() + "For mange argumenter gitt.");
        }
        return true;
    }

    /**
     * Displays freeze menu for the player
     *
     * @param player Player issuing the command
     */
    private void displayMenu(Player player) {
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "freeze list" + getVarChatColor() + " - Viser en liste over alle påloggede spillere som er fryst.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "freeze add spiller" + getVarChatColor() + " - Fryser gitt spiller på en blokk.");
        player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "freeze remove spiller" + getVarChatColor() + " - Fryser gitt spiller.");
    }

    /**
     * Displays a list of all online freezed players
     *
     * @param player Players issuing the command
     */
    private void listFreezedPlayers(Player player) {
        List<String> freezedPlayers = new ArrayList<String>();
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getFreeze()) {
                freezedPlayers.add(entry.getKey().getName());
            }
        }

        if (freezedPlayers.size() != 0) {
            StringBuilder build = new StringBuilder();
            for (String string : freezedPlayers) {
                build.append(string + ", ");
            }
            build.substring(0, build.length() - 2);
            build.append('.');
            player.sendMessage(getInfoChatColor() + "Spillere som er fryst: " + build.toString());
        } else {
            player.sendMessage(getInfoChatColor() + "Ingen spillere online er fryst.");
        }
    }

    /**
     * Freezes a player
     *
     * @param player Player issuing the command
     * @param victim Victim being freezed
     */
    private void freezeAddPlayer(Player player, Player victim) {
        this.movePlayerToTheGround(victim);
        this.userHandler.setFreeze(victim, true);
        player.sendMessage(victim.getName() + getErrorChatColor() + " er nå fryst.");
        victim.sendMessage(getErrorChatColor() + "Du er nå fryst.");
    }

    /**
     * Unfreezes a player
     *
     * @param player Player issuing the command
     * @param victim Victim being unfreezed
     */
    private void freezeRemovePlayer(Player player, Player victim) {
        this.userHandler.setFreeze(victim, false);
        player.sendMessage(getOkChatColor() + "Spilleren er ikke lengre fryst.");
        victim.sendMessage(getOkChatColor() + "Du er nå ikke lengre fryst.");
    }

    /**
     * Moves the player to the first solid block found beneath the player
     *
     * @param player Player that should be moved
     */
    private void movePlayerToTheGround(Player player) {
        Block block = player.getLocation().getBlock();

        while (block.getType().equals(Material.AIR)) {
            block = block.getRelative(BlockFace.DOWN);
        }

        player.teleport(block.getRelative(BlockFace.UP).getLocation());
    }
}















