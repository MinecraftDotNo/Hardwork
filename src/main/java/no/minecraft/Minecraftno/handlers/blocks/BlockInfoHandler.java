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
                        //conn.close();
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
                    //conn.close();
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
                    //conn.close();
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
    public int getOwnerID(int x, int y, int z, String world) {
        int res = this.sqlHandler.getColumnInt("SELECT player FROM blocks WHERE x=" + x + " AND y=" + y + " AND z=" + z + " AND world='" + world + "'", "player");
        if (res != 0) {
            return res;
        } else {
            return 0;
        }
    }
    
    /**
     * <p>Checks if player can interact with block. See method <code>canInteractWithBlock(int, int, int, int, boolean)</code> for docs.</p>
     * 
     * <p>This function fetches owner ID of block and groupID if checkGroup is true.</p>
     * 
     * @see canInteractWithBlock(int, int, int, int, boolean)
     * @param block
     * @param player
     * @param message A message to send to user if can't interact with block. Send {OWNER_NAME} to send owner's name.
     * @param checkGroup
     * @return
     */
    public boolean canInteractWithBlock(Block block, Player player, String message, boolean checkGroup) {
        int ownerID = getOwnerId(block);
        int ownerGroupID = 0;
        
        if (checkGroup == true && ownerID > 0) {
            ownerGroupID = this.groupHandler.getGroupIDFromUserId(ownerID);
        }
        
    	return canInteractWithBlock(ownerID, ownerGroupID, player, message, checkGroup);
    }
    
    /**
     * <p>Checks if player can interact with a block by checking owner. See method <code>canInteractWithBlock(int, int, int, int, boolean)</code> for docs.</p>
     * 
     * This method fetches current player's group and ID.
     * 
     * @see canInteractWithBlock(int, int, int, int, boolean)
     * @param ownerID ID of the owner.
     * @param player
     * @param ownerGroupID ID of owner's group. If not checking group just set it to 0.
     * @param message A message to send to user if can't interact with block. Send {OWNER_NAME} to send owner's name.
     * @param checkGroup
     * @return boolean Will return true if interaction is denied.
     */
    public boolean canInteractWithBlock(int ownerID, int ownerGroupID, Player player, String message, boolean checkGroup) {
        int canInteract = canInteractWithBlock(ownerID, this.userHandler.getUserId(player), ownerGroupID, this.groupHandler.getGroupID(player), checkGroup);
        
        // Handle message.
        if (canInteract > 0 && (message != null && message.trim().length() > 0)) {
            String ownerName = this.userHandler.getNameFromId(canInteract);
            message = message.replace("{OWNER_NAME}", ownerName);
            player.sendMessage(message);
        }
        
        return canInteract > 0 ? false : true;        
    }
    
    /**
     * <p>Checks if player can interact with block in respect to ownership and groups.
     * Returns <code>true</code> if block is ownerless, is owner or (if <code>checkGroup</code>) is in group with
     * owner. Returns <code>false</code> if not owner or (if <code>checkGroup</code>) is not in group with owner.</p>
     * 
     * <p>If checking in normal events where <code>Player</code> object is available its recommended to use
     * the method <code>canInteractWithBlock(int, int, Player, String, boolean)</code> as it supports sending a message.
     * Owner is not fetched in method as events have fetched owner.</p>
     * 
     * @param ownerID ID of the owner.
     * @param playerID ID of player to check.
     * @param playerGroupID ID of player's group. If not checking group just set it to 0.
     * @param ownerGroupID ID of owner's group. If not checking group just set it to 0.
     * @param checkGroup If true then method will check groups on owner and playerUUID
     * @return Integer Returns the ownerID if can't interact (check by canInteract > 0) and -1 if can interact.
     */
    public int canInteractWithBlock(int ownerID, int playerID, int ownerGroupID, int playerGroupID, boolean checkGroup) {
    	int ret = -1;

    	// Not fetching owner in this function as it might use more queries per event.    	
    	if (ownerID > 0) {
    		// Block is owned by a user, check against current user if it is not the same ID.
    		if (playerID != ownerID) {
    			// By this point player can not interact with block as its not owner.
    			// If checkGroup is true keep on, but set ret=false in case it's false.
    			ret = ownerID;
    			
    			if (checkGroup == true) {  				
    				// Check if player is in group.
    				if (playerGroupID > 0 && playerGroupID == ownerGroupID) {
    					// Player is in group with owner, change ret to -1.
    					ret = -1;
    				}
    			}
    		}
    	}

    	return ret;
    }
    
}