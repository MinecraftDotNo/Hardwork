package no.minecraft.hardwork.handlers;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.database.DataConsumer;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: This class cannot have caching yet, because legacy code might change the database without us knowing.

public class BlockHandler implements Handler, DataConsumer {
    private final Hardwork hardwork;

    private PreparedStatement querySetBlockOwner;
    private PreparedStatement queryGetBlockOwner;
    private PreparedStatement queryDeleteBlockOwner;

    public BlockHandler(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void prepareStatements() throws SQLException {
        Connection conn = this.hardwork.getDatabase().getConnection();

        this.querySetBlockOwner = conn.prepareStatement("REPLACE INTO Hardwork.blocks (world, x, y, z, player) VALUES (?, ?, ?, ?, ?)");
        this.queryGetBlockOwner = conn.prepareStatement("SELECT player FROM Hardwork.blocks WHERE world=? AND x=? AND y=? AND z=?");
        this.queryDeleteBlockOwner = conn.prepareStatement("DELETE FROM Hardwork.blocks WHERE world=? AND x=? AND y=? AND z=?");
    }

    public void setBlockOwner(String world, int x, int y, int z, int uid) {
        this.hardwork.getDatabase().getConnection();

        try {
            this.querySetBlockOwner.setString(1, world);
            this.querySetBlockOwner.setInt(2, x);
            this.querySetBlockOwner.setInt(3, y);
            this.querySetBlockOwner.setInt(4, z);
            this.querySetBlockOwner.setInt(5, uid);

            if (this.querySetBlockOwner.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows! Debug data: (World: " + world + ", X:" + x + ", Y:" + y + ", Z:" + z + ", UID:" + uid + ")");
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().warning("SQLException while setting block owner!");
            exception.printStackTrace();
        }

        try {
            this.querySetBlockOwner.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }
    }

    public void setBlockOwner(Location location, int uid) {
        this.setBlockOwner(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), uid);
    }

    public void setBlockOwner(Block block, int uid) {
        this.setBlockOwner(block.getLocation(), uid);
    }

    public void setBlockOwner(String world, int x, int y, int z, User owner) {
        this.setBlockOwner(world, x, y, z, owner.getId());
    }

    public void setBlockOwner(Location location, User owner) {
        this.setBlockOwner(location, owner.getId());
    }

    public void setBlockOwner(Block block, User owner) {
        this.setBlockOwner(block.getLocation(), owner.getId());
    }

    public User getBlockOwner(String world, int x, int y, int z) {
        User owner = null;

        this.hardwork.getDatabase().getConnection();

        try {
            this.queryGetBlockOwner.setString(1, world);
            this.queryGetBlockOwner.setInt(2, x);
            this.queryGetBlockOwner.setInt(3, y);
            this.queryGetBlockOwner.setInt(4, z);

            ResultSet result = this.queryGetBlockOwner.executeQuery();

            if (result.next())
                owner = this.hardwork.getUserHandler().getUser(result.getInt(1));
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while fetching block owner!");
            exception.printStackTrace();
        }

        try {
            this.queryGetBlockOwner.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        return owner;
    }

    public User getBlockOwner(Location location) {
        return this.getBlockOwner(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public User getBlockOwner(Block block) {
        return this.getBlockOwner(block.getLocation());
    }

    public void deleteBlockOwner(String world, int x, int y, int z) {
        this.hardwork.getDatabase().getConnection();

        try {
            this.queryDeleteBlockOwner.setString(1, world);
            this.queryDeleteBlockOwner.setInt(2, x);
            this.queryDeleteBlockOwner.setInt(3, y);
            this.queryDeleteBlockOwner.setInt(4, z);

            if (this.queryDeleteBlockOwner.executeUpdate() > 1)
                throw new SQLException("Unexpected number of affected rows!");
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while deleting block ownership!");
            exception.printStackTrace();
        }

        try {
            this.queryDeleteBlockOwner.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }
    }

    public void deleteBlockOwner(Location location) {
        this.deleteBlockOwner(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void deleteBlockOwner(Block block) {
        this.deleteBlockOwner(block.getLocation());
    }
}
