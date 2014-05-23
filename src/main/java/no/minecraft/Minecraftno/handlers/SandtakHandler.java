package no.minecraft.Minecraftno.handlers;

import com.sk89q.worldedit.bukkit.selections.Selection;
import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.SandtakData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class SandtakHandler {

    private final Minecraftno plugin;
    private final MySQLHandler sqlHandler;
    private HashMap<String, SandtakData> sandtak = new HashMap<String, SandtakData>();

    public SandtakHandler(Minecraftno plugin) {
        this.plugin = plugin;
        this.sqlHandler = plugin.getSqlHandler();
    }

    // Prepares all mysql statements
    public void initialise() {
        this.loadSandtak();
    }

    /**
     * Loads all sandtak entries from the database and into the hashmap
     */
    private void loadSandtak() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            this.sandtak.clear();
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `id`, `name`, `p1x`, `p1y`, `p1z`, `p2x`, `p2y`, `p2z`, `world` FROM sandtak");
            rs = ps.executeQuery();
            while (rs.next()) {
                int sandtakId = rs.getInt(1);
                String sandtakName = rs.getString(2);
                String worldName = rs.getString(9);
                Location pos1 = new Location(this.plugin.getServer().getWorld(worldName), rs.getInt(3), rs.getInt(4), rs.getInt(5));
                Location pos2 = new Location(this.plugin.getServer().getWorld(worldName), rs.getInt(6), rs.getInt(7), rs.getInt(8));
                this.sandtak.put(sandtakName, new SandtakData(sandtakId, sandtakName, pos1, pos2, worldName));
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL-feil i: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
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
     * Returns the map containing all loaded sandtak
     *
     * @return Hashmap
     */
    public HashMap<String, SandtakData> getSandtakMap() {
        return this.sandtak;
    }

    /**
     * Displays a sorted list of all sandtak
     *
     * @return List or null if empty
     */
    public List<String> getSandtakList() {
        if (!this.sandtak.isEmpty()) {
            List<String> sandtakList = new ArrayList<String>(this.sandtak.keySet());
            Collections.sort(sandtakList);
            return sandtakList;
        } else {
            return null;
        }
    }

    /**
     * Adds a new sandtak with the given name and positions
     *
     * @param name Name of the new sandtak
     * @param pos1 Position 1 of the sandtak
     * @param pos2 Position 2 of the sandtak
     *
     * @return true on success, false otherwise
     */
    public boolean addSandtak(String name, Location pos1, Location pos2) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `sandtak`(`name`, `p1x`, `p1y`, `p1z`, `p2x`, `p2y`, `p2z`, `world`)" + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setInt(2, (int) pos1.getX());
            ps.setInt(3, (int) pos1.getY());
            ps.setInt(4, (int) pos1.getZ());
            ps.setInt(5, (int) pos2.getX());
            ps.setInt(6, (int) pos2.getY());
            ps.setInt(7, (int) pos2.getZ());
            ps.setString(8, pos1.getWorld().getName());
            ps.executeUpdate();
            this.loadSandtak();
            return true;
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke legge til et nytt sandtak: ", e);
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
     * Deletes a given sandtak
     *
     * @param name Name of the sandtak to be deleted
     *
     * @return true on success, false otherwise
     */
    public boolean deleteSandtak(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("DELETE FROM `sandtak` WHERE `name` = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            this.loadSandtak();
            return true;
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke fjerne et sandtak: ", e);
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
     * Verifies if the given sandtak name exists in the database
     *
     * @param name Name of the sandtak
     *
     * @return String on success, null otherwise
     */
    public String getSandtakName(String name) {
        return this.sqlHandler.getColumn("SELECT `name` FROM `sandtak` WHERE `name` = '" + name + "'");
    }

    /**
     * Retrieves the sandtak inventory status for the given player
     *
     * @param playerName Player of which to retrieve inventory status
     * @param material   Material in inventory
     *
     * @return quantitiy of double chests in inventory, or -1 on failure
     */
    public int getSandtakInventoryStatus(String playerName, int material) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `doublechestQuantity` FROM `sandtak_inventory` WHERE userid" + "= (SELECT `id` FROM Minecraftno.users WHERE name = ?) AND material = ?");
            ps.setString(1, playerName);
            ps.setInt(2, material);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0; // if while loop doesn't run, user has no row and no quantity
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke hente sandtakInventoryStatus: ", e);
            return -1;
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
     * Updates information about a players sandtakinventorystatus
     *
     * @param playerName Name of the player
     * @param materialId Id of the material being added
     *
     * @return true on success, false otherwise
     */
    public boolean updateSandtakInventoryStatus(String playerName, int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("UPDATE `sandtak_inventory` SET `doublechestQuantity` = (`doublechestQuantity` + ?)" + "WHERE `userid` = (SELECT `id` FROM Minecraftno.users WHERE `name` = ?) AND `material` = ?");
            ps.setInt(1, 1); // quantity = 1 as we only allow one dk in the selection right now
            ps.setString(2, playerName);
            ps.setInt(3, materialId);
            if (ps.executeUpdate() == 0) { // affects no rows = we need to create a user row!
                ps2 = conn.prepareStatement("INSERT INTO `sandtak_inventory`" + "(`userid`, `material`, `doublechestQuantity`) VALUES((SELECT `id` FROM Minecraftno.users WHERE `name` = ?), ?, ?)");
                ps2.setString(1, playerName);
                ps2.setInt(2, materialId);
                ps2.setInt(3, 1); // quantity = 1 as we only allow one dk in the selection right now
                ps2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke oppdatere sandtakInventoryStatus: ", e);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (ps2 != null) {
                    ps2.close();
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
     * Removes double chests from a players inventory status
     *
     * @param playerName Name of the player
     * @param amountOfDk Amount being removed
     * @param materialId Id of material
     *
     * @return True on success, false otherwise
     */
    public boolean removeDksFromPlayerSandtakInventory(String playerName, int amountOfDk, int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("UPDATE `sandtak_inventory` SET `doublechestQuantity` = (`doublechestQuantity` - ?)" + "WHERE `userid` = (SELECT `id` FROM Minecraftno.users WHERE `name` = ?) AND `material` = ?");
            ps.setInt(1, amountOfDk);
            ps.setString(2, playerName);
            ps.setInt(3, materialId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke oppdatere removeDksFromPlayerSandtakInventory: ", e);
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
     * Checks if there exists any blocks within the area of the sandtak
     *
     * @param sandtakName The name of the sandtak to check
     *
     * @return True if the sandtak is empty, false otherwise
     */
    public boolean isSandtakEmpty(String sandtakName) {
        if (this.sandtak.containsKey(sandtakName)) {
            Location pos1 = this.sandtak.get(sandtakName).getPos1();
            Location pos2 = this.sandtak.get(sandtakName).getPos2();
            String worldName = this.sandtak.get(sandtakName).getWorldName();
            int startX, startY, startZ, endX, endY, endZ;

            if ((int) pos1.getX() >= (int) pos2.getX()) {
                startX = (int) pos1.getX();
                endX = (int) pos2.getX();
            } else {
                startX = (int) pos2.getX();
                endX = (int) pos1.getX();
            }

            if ((int) pos1.getY() >= (int) pos2.getY()) {
                startY = (int) pos1.getY();
                endY = (int) pos2.getY();
            } else {
                startY = (int) pos2.getY();
                endY = (int) pos1.getY();
            }

            if ((int) pos1.getZ() >= (int) pos2.getZ()) {
                startZ = (int) pos1.getZ();
                endZ = (int) pos2.getZ();
            } else {
                startZ = (int) pos2.getZ();
                endZ = (int) pos1.getZ();
            }

            for (int x = startX; x >= endX; x--) {
                for (int y = startY; y >= endY; y--) {
                    for (int z = startZ; z >= endZ; z--) {
                        Block block = this.plugin.getServer().getWorld(worldName).getBlockAt(x, y, z);
                        if (!block.getType().equals(Material.AIR)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return false; // Should never occur as we check if the desired sandtak already exists
        }

        return true;
    }

    /**
     * Fills the given sandtak with the given amount starting from the bottom
     *
     * @param sandtakName Name of sandtak
     * @param amountOfDk  Amount of double chests to fill the sandtak with
     * @param materialId  Material id to fill the sandtak with
     */
    public void fillSandtak(String sandtakName, int amountOfDk, int materialId) {
        Location pos1 = this.sandtak.get(sandtakName).getPos1();
        Location pos2 = this.sandtak.get(sandtakName).getPos2();
        String worldName = this.sandtak.get(sandtakName).getWorldName();
        int startX, startY, startZ, endX, endY, endZ;
        int counter = 0;

        if ((int) pos1.getX() >= (int) pos2.getX()) {
            startX = (int) pos1.getX();
            endX = (int) pos2.getX();
        } else {
            startX = (int) pos2.getX();
            endX = (int) pos1.getX();
        }

        if ((int) pos1.getY() < (int) pos2.getY()) {
            startY = (int) pos1.getY();
            endY = (int) pos2.getY();
        } else {
            startY = (int) pos2.getY();
            endY = (int) pos1.getY();
        }

        if ((int) pos1.getZ() >= (int) pos2.getZ()) {
            startZ = (int) pos1.getZ();
            endZ = (int) pos2.getZ();
        } else {
            startZ = (int) pos2.getZ();
            endZ = (int) pos1.getZ();
        }

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x >= endX; x--) {
                for (int z = startZ; z >= endZ; z--) {
                    if ((counter / 3456) == amountOfDk) {
                        return;
                    }
                    this.plugin.getServer().getWorld(worldName).getBlockAt(x, y, z).setTypeId(materialId);
                    counter++;
                }
            }
        }
    }

    /**
     * Verifies a players selection to make sure it contains a double chest filled with either stone or cobblestone
     *
     * @param sel The players selection
     *
     * @return true if selection is valid, false otherwise
     */
    public boolean verifyValidSelection(Selection sel) {
        if (sel.getArea() == 2) {
            Location pos1 = sel.getMaximumPoint();
            Location pos2 = sel.getMinimumPoint();

            if (pos1.getBlock().getType().equals(Material.CHEST) && pos2.getBlock().getType().equals(Material.CHEST)) {
                Chest chest = (Chest) pos1.getBlock().getState();

                if (chest.getInventory().getContents() != null) {
                    Material slot1Material = chest.getInventory().getItem(0).getType();
                    for (ItemStack stack : chest.getInventory().getContents()) { // unsure whether this contains the inventory for just one of the sides or the whole chest
                        if (stack == null || slot1Material != stack.getType() || !((stack.getType().equals(Material.COBBLESTONE) || stack.getType().equals(Material.STONE)) && stack.getAmount() == 64)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Removes protection from the double chest, clears inventory, and breaks the block
     *
     * @param sel Selection containing double chest
     */
    public void removeDoubleChest(Selection sel) { // this should be updated to include a single block and not two
        Location pos1 = sel.getMaximumPoint();
        Location pos2 = sel.getMinimumPoint();

        this.plugin.getBlockHandler().deleteBlockProtection(pos1.getBlock());
        this.plugin.getBlockHandler().deleteBlockProtection(pos2.getBlock());

        Chest chest1 = (Chest) pos1.getBlock().getState();
        chest1.getInventory().clear();
        Chest chest2 = (Chest) pos2.getBlock().getState();
        chest2.getInventory().clear();
        pos1.getBlock().breakNaturally();
        pos2.getBlock().breakNaturally();
    }

    /**
     * Returns the id of the material found inside the chest
     *
     * @param sel Players selection containing the chest
     *
     * @return id of material found inside chest
     */
    public int getMaterialFromChest(Selection sel) {
        Location pos1 = sel.getMaximumPoint();
        Chest chest = (Chest) pos1.getBlock().getState();
        return chest.getInventory().getItem(0).getTypeId();
    }
}