package no.minecraft.Minecraftno.commands;

import java.util.ArrayList;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.WEBridge;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.blocks.PrivateProtectionHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class PrivateCommand extends MinecraftnoCommand {

    private final WEBridge weBridge;
    private final PrivateProtectionHandler pp;
    private final BlockInfoHandler bp;
    private final GroupHandler grHandler;

    public PrivateCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.weBridge = instance.getWeBridge();
        this.pp = instance.getPrivateProtectionHandler();
        this.bp = instance.getBlockInfoHandler();
        this.grHandler = instance.getGroupHandler();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage("-------- " + getDefaultChatColor() + "Privat kister/dører" + getVarChatColor() + " --------");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "privat sett" + getVarChatColor() + " - Sett privat beskyttelse på din merking, gjelder kun dører og kister.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "privat fjern" + getVarChatColor() + " - Fjern privat beskyttelse på din merking, gjelder kun dører og kister.");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("sett")) {
                // Set private
                runSelection(player, 0, false);
            } else if (args[0].equalsIgnoreCase("gruppe")) {
                if (this.grHandler.getGroupID(player) == 0) {
                    player.sendMessage(ChatColor.RED + "Du må være i en gruppe for å bruke /privat gruppe");
                    return true;
                }

                // Set private group
                runSelection(player, 1, false);
            } else if (args[0].equalsIgnoreCase("fjern")) {
                // Remove private.
                runSelection(player, 0, true);
            }
        }
        return true;
    }

    public void runSelection(Player player, int mode, boolean remove) {
        Selection sel = this.weBridge.getWePlugin().getSelection(player);
        if (sel == null) {
            player.sendMessage(getErrorChatColor() + "Du har ikke valgt et område.");
            return;
        }

        if (sel.getArea() > 10) {
            player.sendMessage(getErrorChatColor() + "Kan ikke sette/fjerne items som privat på mer enn 10 blokker om gangen. " +
                "Din selection var på: " + getVarChatColor() + sel.getArea());
            return;
        }

        Vector min = new Vector(sel.getNativeMinimumPoint());
        Vector max = new Vector(sel.getNativeMaximumPoint());

        int totalTried = 0;
        int totalAdded = 0;
        int totalFailed = 0;

        int fromX = min.getBlockX();
        int toX = max.getBlockX();
        int fromY = min.getBlockY();
        int toY = max.getBlockY();
        int fromZ = min.getBlockZ();
        int toZ = max.getBlockZ();
        for (int x = fromX; x <= toX; ++x) {
            for (int y = fromY; y <= toY; ++y) {
                for (int z = fromZ; z <= toZ; ++z) {
                    Block atm = player.getWorld().getBlockAt(x, y, z); // Blokken at the moment in the 'for' loop.
                    Block toAdd = null;

                    if (isDoor(atm.getType())) {
                        Block down = atm.getRelative(BlockFace.DOWN);
                        if (down != null && (isDoor(down.getType()))) {
                            toAdd = down; // Only protect bottom part.
                        }
                    } else if (atm.getType() == Material.CHEST) {
                        toAdd = atm;
                    }

                    // Is this block owned by the player wants to add.
                    if (toAdd != null) {
                        totalTried++;

                        if (!remove && this.pp.isPrivateItem(toAdd)) {
                            /* Defining this as failed because the item is already private. */
                            totalFailed++;
                            continue;
                        }

                        String owner = this.bp.getOwner(toAdd);
                        if ((owner != null) && (owner.equalsIgnoreCase(player.getName()))) {
                            if (remove) {
                                this.pp.deletePrivateItem(toAdd);
                            } else {
                                this.pp.addPrivateItem((mode == 0 ? this.userHandler.getUserId(player) : this.grHandler.getGroupID(player)), toAdd, mode);
                            }
                            totalAdded++;
                        }
                    } else { // end for toAdd, start else
                        /* Defining this as failed since block is null, this should happend.. */
                        totalFailed++;
                    }
                } // end for z
            } // end for y
        } // end for X

        /* Determine message to return. */
        /**
         * Possible statuses;
         * 0 = Nothing happened..
         * 1 = Success
         * 2 = Some wasn't added. (Because owner didn't match)
         * 3 = Some failed (because they are protected or was null).
         */
        int notAdded = (totalTried - totalAdded);
        int failed = (totalTried - totalFailed);
        int status = (totalTried == totalAdded ? 1 : (totalFailed > 0 ? 3 : (totalAdded < totalTried ? 2 : 0)));

        ArrayList<String> msg = new ArrayList<String>();
        msg.add(ChatColor.GREEN + "Ferdig, " + totalAdded + " av " + totalTried + " blokker ble behandlet (Type: for " +
            (mode == 0 ? "bruker" : "gruppe") + ").");

        switch (status) {
            case 1:
                msg.add(ChatColor.GREEN + " Alle blokker" + (remove ? "s privatisering ble fjernet." : " ble privatisert."));
                break;
            case 2:
                msg.add(ChatColor.YELLOW.toString() + (notAdded == 0 ? "Alle" : notAdded) + " blokk" + (notAdded > 1 ? "er" : "") + " ble ikke beskyttet siden de ikke eies av deg.");
                break;
            case 3:
                msg.add(ChatColor.RED.toString() + " Kunne ikke privatisere " + (failed > 0 ? failed : "alle") + " blokk" + (failed > 1 ? "er" : (failed == 1 ? "" : "ene")) + ".");
                msg.add(ChatColor.RED.toString() + " Mulige årsaker kan være at enten blokken(e) allerede er " +
                    "privatisert eller at plugin ikke klarte å hente de (Blokk = null).");
                break;
            default:
                msg.add(ChatColor.RED.toString() + " Status returnerte null... Kontakt utvikler!");
                break;
        }

        for (String s : msg)
            player.sendMessage(s);
    }

    /**
     * Indicates if the material is a door.
     * @param mat
     * @return
     */
    private boolean isDoor(Material mat) {
        if (
               mat == Material.WOODEN_DOOR
            || mat == Material.IRON_DOOR_BLOCK
            || mat == Material.SPRUCE_DOOR
            || mat == Material.ACACIA_DOOR
            || mat == Material.JUNGLE_DOOR
            || mat == Material.BIRCH_DOOR
            || mat == Material.DARK_OAK_DOOR
        ) { // startif mat == doors
            return true;
        } // endif mat == doors

        return false;
    }
}
