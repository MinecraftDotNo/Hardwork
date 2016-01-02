package no.minecraft.Minecraftno.handlers;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.BlockLogReason;
import no.minecraft.Minecraftno.handlers.player.MessageTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class WEBridge {
    private Minecraftno plugin;
    private WorldEditPlugin wePlugin;
    private static ArrayList<Integer> nonBlocks = new ArrayList<Integer>(Arrays.asList(0, 2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 39, 40, 51, 59, 60, 83, 141, 142, 355));

    public WEBridge(Minecraftno plugin) {
        this.plugin = plugin;
    }

    public boolean initialise() {
        this.wePlugin = (WorldEditPlugin) this.plugin.getServer().getPluginManager().getPlugin("WorldEdit");

        if (this.wePlugin == null) {
            this.plugin.getLogger().severe("Could not find WorldEdit!");
            return false;
        }

        return true;
    }

    public boolean isEnabled() {
        return this.wePlugin != null;
    }

    public WorldEditPlugin getWePlugin() {
        return this.wePlugin;
    }

    public void setArea(Selection sel, int playerID, int newOwnerID, int changeId, Set<Integer> ids) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new AreaProtecter(sel, playerID, newOwnerID, changeId, ids, plugin));
    }

    protected class AreaProtecter implements Runnable {
        protected Connection conn;
        protected Vector maxPoint;
        protected Vector minPoint;
        protected int playerID;
        protected World worldName;
        protected int newOwnerID;
        protected int changeId;
        protected Set<Integer> BlockID;

        public AreaProtecter(Selection sel, int playerID, int newOwnerID, int changeId, Set<Integer> BlockID, Minecraftno instance) {
            this.conn = instance.getConnection();
            this.maxPoint = new Vector(sel.getNativeMaximumPoint());
            this.minPoint = new Vector(sel.getNativeMinimumPoint());
            this.playerID = playerID;
            this.worldName = sel.getWorld();
            this.newOwnerID = newOwnerID;
            this.changeId = changeId;
            this.BlockID = BlockID;
        }

        @Override
        public void run() {
            PreparedStatement ps = null;
            PreparedStatement logPS = null;

            try {
                if (this.newOwnerID != 0) {
                    ps = this.conn.prepareStatement("REPLACE INTO `blocks` (`x`, `y`, `z`, `world`, `player`) " + "VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(5, this.newOwnerID);
                } else {
                    if (this.changeId != 0) {
                        ps = this.conn.prepareStatement("DELETE FROM blocks WHERE x=? AND y=? AND z=? AND world=? AND player=?");
                        ps.setInt(5, this.changeId);
                    } else {
                        ps = this.conn.prepareStatement("DELETE FROM blocks WHERE x=? AND y=? AND z=? AND world=?");
                    }
                }
                ps.setString(4, worldName.getName());

                logPS = this.conn.prepareStatement("INSERT INTO `blocklog` (`id`, `userid`, `action`, `x`, `y`, `z`, `world`, `material`, `time`) " + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP());");
                logPS.setInt(1, playerID);

                if (this.newOwnerID != 0) {
                    logPS.setString(2, BlockLogReason.CHANGEOWNER.toString());
                } else {
                    logPS.setString(2, BlockLogReason.UNPROTECTED.toString());
                }
                logPS.setString(6, this.worldName.getName());

                runOnSelection(this.minPoint, this.maxPoint, this.worldName, ps, logPS, BlockID);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (logPS != null) {
                        logPS.close();
                    }
                    if (conn != null) {
                        //conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageTask(playerID, ChatColor.GREEN + "Ferdig med å sette/fjerne blokkbeskyttelse", plugin));
        }
    }

    private static boolean runOnSelection(Vector min, Vector max, World worldname, PreparedStatement ps, PreparedStatement ps2, Set<Integer> BlockID) throws SQLException {
        int fromX = min.getBlockX();
        int toX = max.getBlockX();

        int fromY = min.getBlockY();
        int toY = max.getBlockY();

        int fromZ = min.getBlockZ();
        int toZ = max.getBlockZ();

        for (int x = fromX; x <= toX; ++x) {
            for (int y = fromY; y <= toY; ++y) {
                for (int z = fromZ; z <= toZ; ++z) {
                    Location loc = new Location(worldname, x, y, z);

                    if (BlockID == null) {
                        if (!nonBlocks.contains(Bukkit.getServer().getWorld(worldname.getName()).getBlockAt(loc).getTypeId())) {
                            ps.setInt(1, x);
                            ps.setInt(2, y);
                            ps.setInt(3, z);
                            ps.executeUpdate();

                            ps2.setInt(3, x);
                            ps2.setInt(4, y);
                            ps2.setInt(5, z);
                            ps2.setInt(7, loc.getBlock().getTypeId());
                            ps2.executeUpdate();
                        }
                    } else {
                        for (Integer id : BlockID) {
                            if (Bukkit.getServer().getWorld(worldname.getName()).getBlockAt(loc).getTypeId() == id) {
                                ps.setInt(1, x);
                                ps.setInt(2, y);
                                ps.setInt(3, z);
                                ps.executeUpdate();

                                ps2.setInt(3, x);
                                ps2.setInt(4, y);
                                ps2.setInt(5, z);
                                ps2.setInt(7, loc.getBlock().getTypeId());
                                ps2.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
