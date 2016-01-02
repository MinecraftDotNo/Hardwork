package no.minecraft.Minecraftno.handlers;

import com.sk89q.worldedit.blocks.BaseBlock;
import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class RollbackThread implements Runnable {
    private final Minecraftno plugin;
    private String playerName;
    private long afterTimeStamp = 0;
    private Location from;
    private Location to;

    public RollbackThread(String playerName, long afterTimeStamp, Location from, Location to, Player user, Minecraftno plugin) {
        super();
        this.plugin = plugin;
        this.playerName = playerName;
        this.afterTimeStamp = afterTimeStamp;
        this.from = from;
        this.to = to;
    }

    public static class BlockDataWithTime {
        BaseBlock block;
        long timeStamp;

        public BlockDataWithTime(BaseBlock block, long timeStamp) {
            super();
            this.block = block;
            this.timeStamp = timeStamp;
        }

        /**
         * @return the block
         */
        public BaseBlock getBlock() {
            return block;
        }

        /**
         * @param block the block to set
         */
        public void setBlock(BaseBlock block) {
            this.block = block;
        }

        /**
         * @return the timeStamp
         */
        public long getTimeStamp() {
            return timeStamp;
        }

        /**
         * @param timeStamp the timeStamp to set
         */
        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Connection conn = this.plugin.getConnection();
        int fromX = Math.min(from.getBlockX(), to.getBlockX());
        int toX = Math.max(from.getBlockX(), to.getBlockX());
        int fromY = Math.min(from.getBlockY(), to.getBlockY());
        int toY = Math.max(from.getBlockY(), to.getBlockY());
        int fromZ = Math.min(from.getBlockZ(), to.getBlockZ());
        int toZ = Math.max(from.getBlockZ(), to.getBlockZ());
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT 'action', 'x', 'y', 'z', 'world', 'material', 'time' FROM `blocklog` WHERE `player` = '" + playerName + "' AND `x` BETWEEN " + fromX + " AND " + toX + " AND `y` BETWEEN " + fromY + " AND " + toY + " AND `z` BETWEEN " + fromZ + " AND " + toZ + " AND `world` = '" + from.getWorld().getName() + "' AND `time` > " + afterTimeStamp);

            while (rs.next()) {

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL-feil:", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL-feil:", e);
                }
            }
            /*try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL-feil:", e);
            }*/
        }
    }
}
