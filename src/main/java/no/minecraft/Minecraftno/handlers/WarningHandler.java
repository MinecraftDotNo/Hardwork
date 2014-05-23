package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class WarningHandler {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final SimpleDateFormat sDFormat;

    public WarningHandler(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.sDFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");
    }

    public boolean addWarning(String playerName, String warnerName, String reason, Location loc) throws SQLException {
        int userId = this.userHandler.getUserId(playerName);
        int warnerId = this.userHandler.getUserId(warnerName);
        if (userId != -1) {
            if (warnerId != -1) {
                addWarning(userId, warnerId, reason, loc);
                return true;
            } else {
                Minecraftno.log.log(Level.SEVERE, "[Hardwork] Feil i addWarning (advarer eksisterte ikke).");
                throw new CommandException("Did not find warner in database!");
            }
        }
        return false;
    }

    public boolean addWarning(String playerName, Player warner, String reason, Location loc) throws SQLException {
        return this.addWarning(playerName, warner.getName(), reason, loc);
    }

    public boolean addWarning(Player player, Player warner, String reason, Location loc) throws SQLException {
        return this.addWarning(player.getName(), warner.getName(), reason, loc);
    }

    public boolean addWarning(int userId, int warnerId, String reason, Location loc) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `warnings` (`userId`, `warnerId`, " + "`reason`, `timestamp`, " + "`x`, `y`, `z`, `world`)" + " VALUES ( ?, ?, ?, UNIX_TIMESTAMP(), ?, ?, ?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, warnerId);
            ps.setString(3, reason);
            ps.setInt(4, loc.getBlockX());
            ps.setInt(5, loc.getBlockY());
            ps.setInt(6, loc.getBlockZ());
            ps.setString(7, loc.getWorld().getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke legge advarsel i databasen: ", e);
            throw e;
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
        return true;
    }

    public int countWarnings(String userName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int rowCount = -1;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(userId) FROM `warnings` WHERE `userId` = ?");
            ps.setInt(1, this.userHandler.getUserId(userName));
            rs = ps.executeQuery();
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke liste antall advarseler i databasen: ", e);
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
        return rowCount;
    }

    public int countWarnings(Player player) {
        return this.countWarnings(player.getName());
    }

    public boolean delWarning(int id, String userName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean istrue = false;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `userId` FROM `warnings` WHERE `id` = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (this.userHandler.getUserId(userName) == rs.getInt(1)) {
                    ps = conn.prepareStatement("DELETE FROM `warnings` WHERE `id` = ?");
                    ps.setInt(1, id);
                    if (ps.execute()) {
                        istrue = true;
                    }
                }
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke slette advarsel i databasen: ", e);
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
        return istrue;
    }

    public List<String> getWarnings(String userName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> warningList = null;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `id`, `warnerId`,`reason`,`timestamp`, `x`, `y`, `z`, `world`" + " FROM `warnings` WHERE `userId` = ? ORDER BY id DESC");
            ps.setInt(1, this.userHandler.getUserId(userName));
            rs = ps.executeQuery();
            warningList = new ArrayList<String>();
            warningList.add(ChatColor.DARK_GREEN + "Advarsler for " + ChatColor.WHITE + userName + ChatColor.DARK_GREEN + ".");
            while (rs.next()) {
                int warnerId = rs.getInt(2);
                String warnerName = this.userHandler.getNameFromId(warnerId);
                if (warnerName == null) {
                    warnerName = "Ukjent spiller (id: " + ChatColor.GOLD + warnerId + ChatColor.WHITE + ")";
                }
                Date date = new Date(rs.getLong(4) * 1000);

                warningList.add(ChatColor.DARK_GREEN + "Advarsel: " + ChatColor.WHITE + rs.getString(3));
                warningList.add(ChatColor.DARK_GREEN + "id: " + ChatColor.WHITE + rs.getInt(1));
                warningList.add(ChatColor.DARK_GREEN + "Gitt av " + ChatColor.WHITE + warnerName + ChatColor.GREEN + ". Satt: " + ChatColor.WHITE + sDFormat.format(date) + ChatColor.DARK_GREEN + ".");
                warningList.add(ChatColor.DARK_GREEN + "Posisjon: " + ChatColor.WHITE + rs.getInt(5) + ", " + rs.getInt(6) + ", " + rs.getInt(7) + ChatColor.DARK_GREEN + ". Verden: " + ChatColor.WHITE + rs.getString(8));
                warningList.add("\n");
            }
            if (warningList.size() == 1) {
                warningList.add("(ingen advarsler)");
            }
            if (warningList.size() == 2) {
                warningList.add(ChatColor.DARK_GREEN + "Totalt " + ChatColor.WHITE + countWarnings(userName) + ChatColor.DARK_GREEN + " advarsler.");
                warningList.add("------------------ SLUTT -------------------");
            } else {
                warningList.add(ChatColor.DARK_GREEN + "Totalt " + ChatColor.WHITE + countWarnings(userName) + ChatColor.DARK_GREEN + " advarseler.");
                warningList.add("------------------ SLUTT -------------------");
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
        if (warningList == null || warningList.isEmpty()) {
            return null;
        }
        return warningList;
    }

    public List<String> getWarnings(Player player) {
        return this.getWarnings(player.getName());
    }

    public List<String> getWarningsIRC(String userName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> warningList = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `id`, `warnerId`,`reason`,`timestamp`, `x`, `y`, `z`, `world`" + " FROM `warnings` WHERE `userId` = ? ORDER BY id DESC");
            ps.setInt(1, this.userHandler.getUserId(userName));
            rs = ps.executeQuery();
            warningList = new ArrayList<String>();

            warningList.add("Advarsler for " + userName + ".");
            while (rs.next()) {

                int warnerId = rs.getInt(2);
                String warnerName = this.userHandler.getNameFromId(warnerId);
                if (warnerName == null) {
                    warnerName = "Ukjent spiller (id: " + warnerId + ")";
                }
                Date date = new Date(rs.getLong(4) * 1000);

                warningList.add("Advarsel: " + rs.getString(3));
                warningList.add("id: " + rs.getInt(1));
                warningList.add("Gitt av " + warnerName + ". Satt: " + sDFormat.format(date) + ".");
                warningList.add("Posisjon: " + rs.getInt(5) + ", " + rs.getInt(6) + ", " + rs.getInt(7) + ". Verden: " + rs.getString(8));
                warningList.add("\n");
            }
            if (warningList.size() == 1) {
                warningList.add("(ingen advarsler)");
            } else {
                warningList.add("Totalt " + ChatColor.WHITE + countWarnings(userName) + " advarsler.");
                warningList.add("------------------ SLUTT -------------------");
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
        if (warningList == null || warningList.isEmpty()) {
            return null;
        }
        return warningList;
    }

    /**
     * Retrieves a single row from the warnings table containing information about a given warning
     *
     * @param userName  The username of the victim
     * @param warningId Id of the warning
     *
     * @return A list containing all columns in the row, or null
     */
    public List<String> getWarning(String userName, int warningId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> warningList = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT w.`id`, " +
                "(SELECT name FROM Minecraftno.users WHERE users.id = w.userId) as `victim`," +
                "(SELECT name FROM Minecraftno.users WHERE users.id = w.warnerId) as `warner`," +
                "w.`reason`, w.`x`, w.`y`, w.`z`, w.`world`, from_unixtime(timestamp) as `time` " +
                "FROM warnings w WHERE w.id = ? AND w.userId = ?");
            ps.setInt(1, warningId);
            ps.setInt(2, this.userHandler.getUserId(userName));
            rs = ps.executeQuery();

            while (rs.next()) {
                warningList = new ArrayList<String>();

                warningList.add(rs.getString(1)); // id of warning
                warningList.add(rs.getString(2)); // name of victim
                warningList.add(rs.getString(3)); // name of admin
                warningList.add(rs.getString(4)); // reason
                warningList.add(rs.getString(5)); // x
                warningList.add(rs.getString(6)); // y
                warningList.add(rs.getString(7)); // z
                warningList.add(rs.getString(8)); // world
                warningList.add(rs.getString(9)); // time
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
        if (warningList == null || warningList.isEmpty()) {
            return null;
        }
        return warningList;
    }

    public List<String> getWarningsIRC(Player player) {
        return this.getWarningsIRC(player.getName());
    }
}