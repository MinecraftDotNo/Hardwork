package no.minecraft.Minecraftno.handlers.blocks;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.BlockData;
import no.minecraft.Minecraftno.handlers.enums.BlockLogReason;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Timertask. Run every secound (20 ticks) and run trough the block queue.
 * This is for changing, log, or adding information on blocks to database.
 */

public class BlockHandler extends TimerTask {

    private final Minecraftno plugin;
    private final Queue<BlockData> getblockdata = new LinkedBlockingQueue<BlockData>();
    private final Lock lock = new ReentrantLock();

    public BlockHandler(Minecraftno instance) {
        this.plugin = instance;
    }

    /**
     * Change block owner.
     *
     * @param Int, Block
     */

    public void updateBlockProtection(int playerId, Block block) {
        this.plugin.getHardwork().getBlockHandler().setBlockOwner(block, playerId);
    }

    /**
     * delete block owner.
     *
     * @param Int, Block
     */

    public void deleteBlockProtection(Block block) {
        deleteBlockProtection(block.getLocation());
    }

    /**
     * delete block owner.
     *
     * @param Int, Location
     */

    public void deleteBlockProtection(Location loc) {
        this.plugin.getHardwork().getBlockHandler().deleteBlockOwner(loc);
    }

    /**
     * Set block owner.
     *
     * @param Int, Block
     */

    public void setBlockProtection(int playerId, Block block) {
        setBlockProtection(playerId, block.getLocation());
    }

    /**
     * Set block owner.
     *
     * @param Int, Location
     */

    public void setBlockProtection(int playerId, Location l) {
        this.plugin.getHardwork().getBlockHandler().setBlockOwner(l, playerId);
    }

    /**
     * Add blocklog to database.
     *
     * @param Int, Block
     */

    public void setBlocklog(int playerId, Block block, BlockLogReason reason) {
        setBlocklog(playerId, block.getLocation(), block.getTypeId(), reason);
    }

    /**
     * Add blocklog to database.
     *
     * @param Int, Location
     */

    public void setBlocklog(int playerId, Location l, int blockId, BlockLogReason reason) {
        String statment = ("INSERT INTO `blocklog` (`id`, `userid`, `action`, `x`, `y`, `z`, `world`, `material`, `time`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP());");
        BlockData lbd = new BlockData(0, statment, playerId, blockId, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName(), reason.toString());
        getblockdata.add(lbd);
    }

    /**
     * @return size of block Queue
     */

    public int getQueueSize() {
        return getblockdata.size();
    }

    /**
     * Check if there is a active lock, or queue is empty before run.
     * Locks it down so it can't be interrupt by other tasks.
     * Add all information to the database that have been stored in queue.
     * Unlock the thread.
     */

    @Override
    public void run() {
        if (getblockdata.isEmpty() || !lock.tryLock()) {
            return;
        }
        final Connection conn = plugin.getConnection();
        PreparedStatement setLog = null;
        if (getQueueSize() > 500) {
            Minecraftno.log.log(Level.SEVERE, ("[Minecraftno] Queue Size WARNING!. Size: " + getQueueSize()));
        }
        try {
            if (conn == null) {
                return;
            }
            final long start = System.currentTimeMillis();
            conn.setAutoCommit(false); // Do not commit changes to database on error.
            while (!getblockdata.isEmpty() && (System.currentTimeMillis() - start < 1500)) { // Wil now not run forever. If the Queue is bug in anyway.
                final BlockData lbd = getblockdata.poll();
                try {
                    if (lbd.getType() == 0) {
                        setLog = conn.prepareStatement(lbd.getStatment());
                        setLog.setInt(1, lbd.getPlayerId());
                        setLog.setString(2, lbd.getBlockLogReason());
                        setLog.setShort(3, (short) lbd.getBlockX());
                        setLog.setShort(4, (short) lbd.getBlockY());
                        setLog.setShort(5, (short) lbd.getBlockZ());
                        setLog.setString(6, lbd.getWorld());
                        setLog.setShort(7, (short) lbd.getBlockTypeId());
                        setLog.executeUpdate();
                    } else if (lbd.getType() == 1) {
                        setLog = conn.prepareStatement(lbd.getStatment());
                        setLog.setShort(1, (short) lbd.getBlockX());
                        setLog.setShort(2, (short) lbd.getBlockY());
                        setLog.setShort(3, (short) lbd.getBlockZ());
                        setLog.setString(4, lbd.getWorld());
                        setLog.setInt(5, lbd.getPlayerId());
                        setLog.executeUpdate();
                    } else if (lbd.getType() == 2) {
                        setLog = conn.prepareStatement(lbd.getStatment());
                        setLog.setShort(1, (short) lbd.getBlockX());
                        setLog.setShort(2, (short) lbd.getBlockY());
                        setLog.setShort(3, (short) lbd.getBlockZ());
                        setLog.setString(4, lbd.getWorld());
                        setLog.executeUpdate();
                    } else if (lbd.getType() == 3) {
                        setLog = conn.prepareStatement(lbd.getStatment());
                        setLog.setInt(1, lbd.getPlayerId());
                        setLog.setShort(2, (short) lbd.getBlockX());
                        setLog.setShort(3, (short) lbd.getBlockY());
                        setLog.setShort(4, (short) lbd.getBlockZ());
                        setLog.setString(5, lbd.getWorld());
                        setLog.executeUpdate();
                    } else {
                        Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke legge til blokk i blokk-loggen. ");
                    }
                } catch (final SQLException e) {
                    Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke legge til blokk i blokk-loggen. ", e);
                    break;
                }
                conn.commit();
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL exception", e);
            e.printStackTrace();
        } finally {
            try {
                if (setLog != null) {
                    setLog.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL exception on close", ex);
            }
            lock.unlock();
        }
    }
}