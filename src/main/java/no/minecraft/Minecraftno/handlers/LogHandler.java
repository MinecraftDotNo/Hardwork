package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;

public class LogHandler {
    private final Minecraftno plugin;
    private final MySQLHandler sqlHandler;

    public LogHandler(Minecraftno instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
    }

    public boolean log(int player, int victim, int amount, int itemid, String data, MinecraftnoLog type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `logg` VALUES(?,?,?,?,?,?,?,UNIX_TIMESTAMP())");
            switch (type) {
                case BAN:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "BAN");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " bannet " + plugin.getUserHandler().getNameFromId(victim) + " Grunnlag: " + data + ".");
                        break;
                    }
                case UNBAN:
                    if ((player != -1) && (victim != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "UNBAN");
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " unbannet " + plugin.getUserHandler().getNameFromId(victim) + ".");
                        break;
                    }
                case BANKINN:
                    if ((player != -1) && (amount >= 1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "BANKINN");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.bank", plugin.getUserHandler().getNameFromId(player) + " satt inn " + amount + " gull.");
                        break;
                    }
                case BANKUT:
                    if ((player != -1) && (amount >= 1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "BANKUT");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.bank", plugin.getUserHandler().getNameFromId(player) + " tok ut " + amount + " gull.");
                        break;
                    }
                case BANKTRANSFER:
                    if ((player != -1) && (victim != -1) && (amount >= 1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setInt(4, amount);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "BANKTRANSFER");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.bank", plugin.getUserHandler().getNameFromId(player) + " overførte " + amount + " gull til " + plugin.getUserHandler().getNameFromId(victim) + ": " + data + ".");
                        break;
                    }
                case CHAT:
                    if ((player != -1) && (data != null)) {
                        Object[] array = {player, data};
                        if (this.sqlHandler.update("INSERT INTO chat (id, name, text, time) VALUES (NULL, ?, ?, UNIX_TIMESTAMP());", array)) {
                            break;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                case TRADECHAT:
                    if ((player != -1) && (data != null)) {
                        Object[] array = {player, data};
                        if (this.sqlHandler.update("INSERT INTO handelchat (id, name, text, time) VALUES (NULL, ?, ?, UNIX_TIMESTAMP());", array)) {
                            break;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                case DELWARP:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "DELWARP");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " slettet warp: " + data);
                        break;
                    }
                case GIVE:
                    if ((player != -1) && (itemid != 0) && (amount >= 0)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setInt(5, itemid);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "GIVE");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " spawnet " + amount + "x" + Material.getMaterial(itemid).toString() + ".");
                        break;
                    }
                case KICK:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "KICK");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " kicket " + plugin.getUserHandler().getNameFromId(victim) + " Grunnlag: " + data + ".");
                        break;
                    }
                case MELDING:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        Object[] array = {victim, player, data};
                        if (this.sqlHandler.update("INSERT INTO `meldinger` (`id`, `to_name`, `from_name`, `message`, `time`) VALUES (NULL, ?, ?, ?, UNIX_TIMESTAMP());", array)) {
                            break;
                        } else {
                            return false;
                        }
                    }
                case SETWARP:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "SETWARP");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " opprettet warp: " + data);
                        break;
                    }
                case PURCHASE:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.INTEGER);
                            ps.setInt(4, amount);
                            ps.setInt(5, itemid);
                            ps.setString(6, data);
                            ps.setString(7, "PURCHASE");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " kjøpte " + amount + " " + data);
                        break;
                    }
                case ILLEGAL:
                    if ((player != -1)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setInt(5, itemid);
                            ps.setNull(6, Types.VARCHAR);
                            ps.setString(7, "ILLEGAL");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        break;
                    }
                case WEEKBAN:
                    if ((player != -1) && (victim != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setInt(3, victim);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "WEEKBAN");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", plugin.getUserHandler().getNameFromId(player) + " ukesbannet " + plugin.getUserHandler().getNameFromId(victim) + " Grunnlag: " + data + ".");
                        break;
                    }
                case SETBY:
                    break;
                case SETPOI:
                    break;
                case SETTETTSTED:
                    break;
                case SANDTAKFILL:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "SANDTAKFILL");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                    }
                    break;
                case SANDTAKNEW:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "SANDTAKNEW");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                    }
                    break;
                case SANDTAKDELETE:
                    if ((player != -1) && (data != null)) {
                        try {
                            ps.setNull(1, Types.INTEGER);
                            ps.setInt(2, player);
                            ps.setNull(3, Types.INTEGER);
                            ps.setNull(4, Types.SMALLINT);
                            ps.setNull(5, Types.SMALLINT);
                            ps.setString(6, data);
                            ps.setString(7, "SANDTAKFILL");
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Error: " + ex);
                            return false;
                        }
                    }
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
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
        return true;
    }

    /**
     * Logger valgt handling
     *
     * @param player (Player) spilleren som skal logges
     * @param victim (Player) victim (kan være null)
     * @param amount (int) antall? (kan være null)
     * @param id (int) itemid/blockid (kan være null)
     * @param data (String) tileggstekst som skal logges (kan være null)
     * @param type (MinecraftnoLog) handling kan ikke være null.
     * @return true/false om handlingen har blitt logget eller ikke
     */
    /*public boolean log(Player player, Player victim, int amount, int itemid, String data, MinecraftnoLog type) {
        int playerint = this.userHandler.getUserId(player);
		int victimint = this.userHandler.getUserId(victim);
		if(victim != null) {	
			return log(playerint, victimint, amount, itemid, data, type);
		} else {
			return log(playerint, 0, amount, itemid, data, type);
		}
	}
	
	public boolean log(String player, String victim, int amount, int itemid, String data, HardworkLog type) {
		int playerint = this.userHandler.getUserId(player);
		int victimint = this.userHandler.getUserId(victim);
		if(victim != null) {
			return log(playerint, victimint, amount, itemid, data, type);
		} else {
			return log(playerint, 0, amount, itemid, data, type);
		}
	}*/
}