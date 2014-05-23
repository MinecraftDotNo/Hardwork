package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.GroupLogAdmin;
import no.minecraft.Minecraftno.handlers.enums.GroupLogBank;
import no.minecraft.Minecraftno.handlers.enums.GroupLogMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;

public class GroupLogHandler {

    private final Minecraftno plugin;

    public GroupLogHandler(Minecraftno instance) {
        this.plugin = instance;
    }

    public boolean logMember(int player, int victim, String data, int groupID, GroupLogMember type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `grouplogg` VALUES(?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
            switch (type) {
                case create:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setNull(6, Types.INTEGER);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case invite:
                    if ((player != -1) && (victim != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                    }
                case accept:
                    if ((player != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case leave:
                    if ((player != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case updateowner:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case kick:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case groupoption:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case delgroup:
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
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

    public boolean logBank(int player, int victim, int amount, String data, int groupID, GroupLogBank type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `grouplogg` VALUES(?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
            switch (type) {
                case insert:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case remove:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case transfer:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
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

    public boolean logAdmin(int player, int victim, int amount, String data, int groupID, GroupLogAdmin type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `grouplogg` VALUES(?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
            switch (type) {
                case updateowner:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case joingroup:
                    if ((player != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case moveplayer:
                    if ((player != -1) && (victim != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            if (data != null) {
                                ps.setString(5, data);
                            } else {
                                ps.setNull(5, Types.VARCHAR);
                            }
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case deletegroup:
                    if ((player != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case renamegroup:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setString(5, data);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case kick:
                    if ((player != -1) && (victim != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.VARCHAR);
                            ps.setInt(6, groupID);
                            ps.setString(7, type.name());
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
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

    public boolean logGroupMessage(int groupId, int userId, String message) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `groupchat` (`id`, `groupid`, `userid`, `message`, `time`) VALUES (NULL, ?, ?, ?, UNIX_TIMESTAMP())");
            try {
                ps.setInt(1, groupId);
                ps.setInt(2, userId);
                ps.setString(3, message);
                ps.executeUpdate();
            } catch (SQLException e) {
                Util.logSqlError(e);
                return false;
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
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
}