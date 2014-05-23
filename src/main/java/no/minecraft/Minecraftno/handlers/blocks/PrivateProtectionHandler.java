package no.minecraft.Minecraftno.handlers.blocks;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * May be changed because of the mysql update tj was talking about.
 * TODO: Add a quene-sort-of-thing to this...
 *
 * @author Hultberg
 */
public class PrivateProtectionHandler {

    private Minecraftno plugin;
    private UserHandler userHandler;
    private GroupHandler groupHandler;

    public PrivateProtectionHandler(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
    }

    /**
     * Private Protect START
     * (Gjelder kun kister og dører.)
     */

    public boolean addPrivateItem(Player player, Block block, int mode) {
        return this.addPrivateItem(this.userHandler.getUserId(player), block, mode);
    }

    /**
     * Legg til en private item.
     *
     * @param id    Id
     * @param block Blokken som skal private protectes.
     *
     * @return true if success, false if failed.
     */
    public boolean addPrivateItem(int id, Block block, int mode) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("REPLACE INTO `privateItems` (`x`, `y`, `z`, `uid`, `world`, `type`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setShort(1, (short) block.getLocation().getBlockX());
            ps.setShort(2, (short) block.getLocation().getBlockY());
            ps.setShort(3, (short) block.getLocation().getBlockZ());
            ps.setInt(4, id);
            ps.setString(5, block.getWorld().getName());
            ps.setInt(6, mode);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke sette privat beskyttelse på et item.");
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Fjern en private protection item.
     *
     * @param block Blokken som fjernes fra private protectet.
     *
     * @return true if success, false if failed.
     */
    public boolean deletePrivateItem(Block block) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("DELETE FROM `privateItems` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?");
            ps.setShort(1, (short) block.getLocation().getBlockX());
            ps.setShort(2, (short) block.getLocation().getBlockY());
            ps.setShort(3, (short) block.getLocation().getBlockZ());
            ps.setString(4, block.getWorld().getName());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke slette private beskyttelse på et item.");
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    public boolean isPrivateItem(Block block) {
        return isPrivateItem(block.getLocation());
    }

    /**
     * Sjekk om en blokk er private protecta.
     *
     * @param block
     *
     * @return true hvis protecta, false hvis ikke.
     */
    public boolean isPrivateItem(Location loc) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT uid FROM `privateItems` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?");
            ps.setShort(1, (short) loc.getBlockX());
            ps.setShort(2, (short) loc.getBlockY());
            ps.setShort(3, (short) loc.getBlockZ());
            ps.setString(4, loc.getWorld().getName());
            rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke sjekke om en private item var protecta.");
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Hent eier av en private item.
     *
     * @param block Blokken som skal sjekkes.
     */
    public ArrayList<Integer> getOwnerPrivateItem(Block block) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `uid`,`type` FROM `privateItems` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?");
            ps.setShort(1, (short) block.getLocation().getBlockX());
            ps.setShort(2, (short) block.getLocation().getBlockY());
            ps.setShort(3, (short) block.getLocation().getBlockZ());
            ps.setString(4, block.getWorld().getName());
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt(1));
                result.add(rs.getInt(2));
            }
            return result;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke henter eier av en private item.");
            return result;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Handle onBlockBreak event for private items.
     *
     * @param b       The block in question, must be chest or door. (Handled (elns) inside BlockListener)
     * @param remover The remover of the block.
     *
     * @return <code>true</code> if get to remove it, <code>false</code> if not.
     */
    public boolean handlePrivateBlockBreak(Block b, Player remover) {
        if (this.isPrivateItem(b)) {
            ArrayList<Integer> result = this.getOwnerPrivateItem(b);
            if (result.size() < 0) {
                return true;
            }

            int owner = result.get(0);
            int mode = result.get(1);
            int gr = this.groupHandler.getGroupID(remover);
            if ((mode == 0 && (owner != 0 && owner != this.userHandler.getUserId(remover))) || (mode == 1 && (gr != 0 && gr != owner))) {
                remover.sendMessage(ChatColor.RED + "Denne blokken er satt som privat " + (mode == 0 ? "av " + ChatColor.WHITE + this.userHandler.getNameFromId(owner) + ChatColor.RED : "for en gruppe som du ikke er medlem av") + " og kan derfor ikke fjernes.");
                return true;
            } else {
                // Owner of private chest, let's delete the chest.
                remover.sendMessage(ChatColor.GREEN + "Denne blokken var satt som privat og er nå slettet."); // Really necessary?
                this.deletePrivateItem(b); // Deleting.
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Handle onPlayerInteract event for private items.
     *
     * @param b      The block in question, must be chest or door. (Handled (elns) inside PlayerListener)
     * @param player The interactive player.
     *
     * @return <code>true</code> if get to interact it, <code>false</code> if not.
     */
    public boolean allowedInteraction(Block b, Player player) {
        ArrayList<Integer> result = this.getOwnerPrivateItem(b);
        if (result.size() == 0) {
            return true;
        }

        int owner = result.get(0);
        int mode = result.get(1);
        int gr = this.groupHandler.getGroupID(player);
        if ((mode == 0 && (owner != 0 && owner != this.userHandler.getUserId(player))) || (mode == 1 && (gr != 0 && gr != owner))) {
            player.sendMessage(ChatColor.RED + "Denne blokken er satt som privat " + (mode == 0 ? "av " + ChatColor.WHITE + this.userHandler.getNameFromId(owner) + ChatColor.RED : "for en gruppe som du ikke er medlem av") + " og kan derfor ikke åpnes.");
            return false;
        }
        return true;
    }

    /**
     * Private Protect SLUTT
     */

}