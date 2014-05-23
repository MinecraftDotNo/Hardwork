package no.minecraft.Minecraftno.handlers.blocks;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.MySQLHandler;
import no.minecraft.Minecraftno.handlers.Util;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class BlockInfoHandler {

    private final Minecraftno plugin;
    private final MySQLHandler sqlHandler;
    private final UserHandler userHandler;

    SimpleDateFormat dateFormat = Util.dateFormat;

    public BlockInfoHandler(Minecraftno instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.userHandler = instance.getUserHandler();
    }

    public ArrayList<String> getBlockLog(Block block, boolean idToName) {
        return getBlockLog(block.getLocation(), idToName);
    }

    public ArrayList<String> getBlockLog(Location loc, boolean idToName) {
        World world = loc.getWorld();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);
        if (wcfg.logBlocks) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            ArrayList<String> row = new ArrayList<String>();
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement("SELECT * FROM `blocklog` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=? ORDER BY `time` DESC LIMIT 10");
                ps.setShort(1, (short) loc.getX());
                ps.setShort(2, (short) loc.getY());
                ps.setShort(3, (short) loc.getZ());
                ps.setString(4, loc.getWorld().getName());
                ps.execute();
                rs = ps.getResultSet();

                while (rs.next()) {
                    Date date = new Date(rs.getLong("time") * 1000);
                    if (idToName) {
                        row.add(dateFormat.format(date) + " -- " + this.userHandler.getNameFromId(rs.getInt("userid")) + " " + rs.getString("action") + " " + Material.getMaterial(rs.getShort("material")).toString());
                    } else {
                        row.add(dateFormat.format(date) + " -- " + this.userHandler.getNameFromId(rs.getInt("userid")) + " " + rs.getString("action") + " " + rs.getShort("material"));
                    }
                }
                if (row.isEmpty()) {
                    row.add("Ingen historie for denne blokken.");
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception i getBlockLog ", e);
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
            return row;
        } else {
            return null;
        }
    }

    public boolean add(Player player, Painting block) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("REPLACE INTO `blocks` (`x`, `y`, `z`, `world`, `player`) VALUES (?, ?, ?, ?, ?)");
            ps.setShort(2, (short) block.getLocation().getBlockY());
            ps.setShort(3, (short) block.getLocation().getBlockZ());
            ps.setString(4, block.getWorld().getName());
            ps.setInt(5, this.userHandler.getUserId(player));
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke legge til bilde i blokk-beskyttelse.", ex);
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

    public boolean delete(Painting block) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("DELETE FROM blocks WHERE x=? AND y=? AND z=? AND world=?");
            ps.setShort(1, (short) block.getLocation().getBlockX());
            ps.setShort(2, (short) block.getLocation().getBlockY());
            ps.setShort(3, (short) block.getLocation().getBlockZ());
            ps.setString(4, block.getWorld().getName());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke slette bilde fra blokk-beskyttelse.");
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

    public boolean isProtected(Block block) {
        String res = this.sqlHandler.getColumn("SELECT player FROM blocks WHERE x=" + block.getX() + " AND y=" + block.getY() + " AND z=" + block.getZ() + " AND world='" + block.getWorld().getName() + "'");
        return res != null && !res.isEmpty();
    }

    public String getOwner(Location loc) {
        String res = this.sqlHandler.getColumn("SELECT player FROM blocks WHERE x=" + loc.getBlockX() + " AND y=" + loc.getBlockY() + " AND z=" + loc.getBlockZ() + " AND world='" + loc.getWorld().getName() + "'");
        if (res != null && !res.isEmpty()) {
            return this.userHandler.getNameFromId(Integer.parseInt(res));
        } else {
            return null;
        }
    }

    public String getOwner(Block block) {
        return getOwner(block.getLocation());
    }

    public int getOwnerId(Block block) {
        int res = this.sqlHandler.getColumnInt("SELECT player FROM blocks WHERE x=" + block.getX() + " AND y=" + block.getY() + " AND z=" + block.getZ() + " AND world='" + block.getWorld().getName() + "'", "player");
        if (res != 0) {
            return res;
        } else {
            return 0;
        }
    }
}