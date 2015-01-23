package no.minecraft.Minecraftno.handlers.blocks;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.GroupHandler;
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
    private final GroupHandler groupHandler;

    SimpleDateFormat dateFormat = Util.dateFormat;

    public BlockInfoHandler(Minecraftno instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
    }

    public ArrayList<String> getBlockLog(Block block, boolean idToName) {
        return getBlockLog(block.getLocation(), idToName);
    }

    @SuppressWarnings("deprecation")
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

    /**
     * <p>Indicates if a <code>Block</code> is protected in blocks table. Uses <code>getOwnerId(Block)</code><p>
     * 
     * @see getOwnerId(Block)
     * @param block
     * @return
     */
    public boolean isProtected(Block block) {
    	// Just call getOwnerID and check if returned ID is over null.
    	return getOwnerId(block) > 0 ? true : false;
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
        return getOwnerID(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }

    /**
     * <p>Provides the user id of owner of a block at requested coordinates and world.</p>
     * @param Integer x
     * @param Integer z
     * @param Integer y
     * @param String world
     * @return Integer Returns zero (0) if no owner found.
     */
    public int getOwnerID(int x, int z, int y, String world) {
        int res = this.sqlHandler.getColumnInt("SELECT player FROM blocks WHERE x=" + x + " AND y=" + y + " AND z=" + z + " AND world='" + world + "'", "player");
        if (res != 0) {
            return res;
        } else {
            return 0;
        }
    }
    
    /**
     * <p>Checks if player can interact with block. See method <code>canInteractWithBlock(int, int, int, String, int, boolean)</code> for docs.</p>
     * 
     * @see canInteractWithBlock(int, int, int, String, int, boolean)
     * @param block
     * @param checkGroup
     * @return
     */
    public boolean canInteractWithBlock(Block block, Player player, boolean checkGroup) {
    	return canInteractWithBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), this.userHandler.getUserId(player), checkGroup);
    }
    
    /**
     * <p>Checks if player can interact with block in respect to ownership and groups.
     * Returns <code>true</code> if block is ownerless, is owner or (if <code>checkGroup</code>) is in group with
     * owner. Returns <code>false</code> if not owner or (if <code>checkGroup</code>) is not in group with owner.</p>
     * 
     * <p>The owner of the block at <code>x</code>, <code>y</code>, <code>z</code>, <code>world</code> will be fetched and checked against <code>playerID</code></p>
     * 
     * @param x X-coord of Block's location.
     * @param y Y-coord of Block's location.
     * @param z Z-coord of Block's location.
     * @param world Name of world to check.
     * @param playerID ID of player to check.
     * @param checkGroup If true then method will check groups on owner and playerUUID
     * @return boolean true if can interact or false if not. Will return true if no owner on block.
     */
    public boolean canInteractWithBlock(int x, int y, int z, String world, int playerID, boolean checkGroup) {
    	boolean ret = true;

    	int owner = getOwnerID(x, y, z, world);
    	
    	// As getOwnerID() returns an int over zero if owner is found simply
    	// check this by using:
    	if (owner > 0) {
    		// Block is owned by a user, check against current user if it is not the same ID.
    		if (playerID != owner) {
    			// By this point player can not interact with block as its not owner.
    			// If checkGroup is true keep on, but set ret=false in case it's false.
    			ret = false;
    			
    			if (checkGroup == true) {
    				// Fetch group IDs for each player.
    				int playerGroupID = this.groupHandler.getGroupIDFromUserId(playerID);
    				int ownerGroupID  = this.groupHandler.getGroupIDFromUserId(owner);
    				
    				// Check if player is in group.
    				if (playerGroupID > 0 && playerGroupID == ownerGroupID) {
    					// Player is in group with owner, change ret to true.
    					ret = true;
    				}
    			}
    		}
    	}
    	
    	return ret;
    }
}