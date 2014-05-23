package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.GroupData;
import no.minecraft.Minecraftno.handlers.enums.GroupLogAdmin;
import no.minecraft.Minecraftno.handlers.enums.GroupLogMember;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class GroupHandler {
    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final MySQLHandler sqlHandler;
    private final GroupLogHandler groupLogHandler;
    public final HashMap<Integer, GroupData> groupData;

    public GroupHandler(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.sqlHandler = instance.getSqlHandler();
        this.groupLogHandler = instance.getGroupLogHandler();
        this.groupData = new HashMap<Integer, GroupData>();
    }

    public enum OptionColumns {
        bankOn, inviteOn, publicOn, colorID
    }

    public ChatColor fromInt(int colorId) {
        switch (colorId) {
            case 1:
                return ChatColor.WHITE;
            case 2:
                return ChatColor.BLACK;
            case 3:
                return ChatColor.AQUA;
            case 4:
                return ChatColor.BLUE;
            case 5:
                return ChatColor.DARK_AQUA;
            case 6:
                return ChatColor.DARK_BLUE;
            case 7:
                return ChatColor.DARK_GRAY;
            case 8:
                return ChatColor.DARK_GREEN;
            case 9:
                return ChatColor.DARK_PURPLE;
            case 10:
                return ChatColor.DARK_RED;
            case 11:
                return ChatColor.GOLD;
            case 12:
                return ChatColor.GRAY;
            case 13:
                return ChatColor.GREEN;
            case 14:
                return ChatColor.LIGHT_PURPLE;
            case 15:
                return ChatColor.RED;
            case 16:
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }

    public int toInt(ChatColor chatcolor) {
        switch (chatcolor) {
            case WHITE:
                return 1;
            case BLACK:
                return 2;
            case AQUA:
                return 3;
            case BLUE:
                return 4;
            case DARK_AQUA:
                return 5;
            case DARK_BLUE:
                return 6;
            case DARK_GRAY:
                return 7;
            case DARK_GREEN:
                return 8;
            case DARK_PURPLE:
                return 9;
            case DARK_RED:
                return 10;
            case GOLD:
                return 11;
            case GRAY:
                return 12;
            case GREEN:
                return 13;
            case LIGHT_PURPLE:
                return 14;
            case RED:
                return 15;
            case YELLOW:
                return 16;
            default:
                return 1;
        }
    }

    public void addGroup(int groupID, String groupName) {
        GroupData groupData = new GroupData(groupName);
        this.groupData.put(groupID, groupData);

        //Eier
        this.groupData.get(groupID).setGroupOwner(getGroupOwnerFromDB(groupID));

        // Bank
        List<Integer> bankInfo = getGroupBankFromDB(groupID);
        this.groupData.get(groupID).setBank(bankInfo.get(0));
        this.groupData.get(groupID).setBankOn(bankInfo.get(1));

        //Invites
        this.groupData.get(groupID).setInviteOn(getGroupOption(groupID, OptionColumns.inviteOn));

        //Public
        this.groupData.get(groupID).setPublicOn(getGroupOption(groupID, OptionColumns.publicOn));

        //Gruppe Farge
        this.groupData.get(groupID).setChatColor(fromInt(getGroupOption(groupID, OptionColumns.colorID)));
    }

    public GroupData getGroupData(int groupID) {
        return groupData.get(groupID);
    }

    /**
     * Henter id på gruppen til en bruker.
     *
     * @param player
     *
     * @return int gruppe id.
     */
    public int getGroupID(Player player) {
        return this.userHandler.getPlayerData(player).getGroupId();
    }

    /**
     * Sets the group id for the player in the map of online players
     *
     * @param player
     * @param groupId
     */
    private void setGroupIdInMap(Player player, int groupId) {
        this.userHandler.getPlayerData(player).setGroupId(groupId);
    }

    /**
     * Sets the players group id in the map to the value from the database.
     *
     * @param player Spilleren som det skal endres på
     */
    private void updateGroupIdInMap(Player player) {
        this.userHandler.getPlayerData(player).setGroupId(getGroupIDFromName(player.getName()));
    }

    public void cleanup() {
        this.groupData.clear();
    }

    public void getAllGroups() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `id`, `group_name` FROM `groups`");
            rs = ps.executeQuery();

            while (rs.next()) {
                addGroup(rs.getInt(1), rs.getString(2));
            }
            // conn.close();
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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

    public void sendGroupMessage(Player player, String groupMessage) {
        int groupID = getGroupID(player);
        ChatColor color = getGroupData(groupID).getChatColor();
        String groupName = getGroupData(groupID).getGroupName();
        for (Player vplayer : this.plugin.getServer().getOnlinePlayers()) {
            if (getGroupID(vplayer) == groupID) {
                vplayer.sendMessage(ChatColor.WHITE + "(" + ChatColor.DARK_GREEN + groupName + ChatColor.WHITE + ") " + player.getName() + ": " + color + groupMessage);
            }
        }
        this.plugin.getIrcBot().sendMessage("#hardwork.pm", "[" + groupName + "]" + "(" + player.getName() + ") " + groupMessage);
        this.groupLogHandler.logGroupMessage(groupID, this.userHandler.getUserId(player), groupMessage);
    }

    public void broadcastMessage(int groupID, String message) {
        for (Player vplayer : this.plugin.getServer().getOnlinePlayers()) {
            if (getGroupID(vplayer) == groupID) {
                vplayer.sendMessage(ChatColor.RED + "(Gruppe) " + ChatColor.WHITE + message);
            }
        }
    }

	/*
     * - Public ting for kommandoer
	 */

    /**
     * Retunerer om spilleren er i en gruppe eller ikke
     *
     * @param player Spilleren som skal sjekkes
     *
     * @return false om spilleren ikke er i en gruppe, ellers true
     */
    public boolean isInGroup(Player player) {
        if (groupData.containsKey(this.userHandler.getPlayerData(player).getGroupId())) {
            return true;
        } else {
            String result = sqlHandler.getColumn("SELECT * FROM `groupusers` WHERE `userID` = " + userHandler.getUserId(player));
            return result != null && !result.isEmpty();
        }
    }

    /**
     * Sjekker om en gruppe eskiterers
     * Kun <b>en</b> verdi kan bli oppgitt, enten <u>gruppeID</u> <b>eller</b> <u>name</u> og <u>autoCompleteName</u>
     *
     * @param groupID          Hvis 0, blir name brukt
     * @param name             Hvis null blir groupID brukt
     * @param autoCompleteName om den skal ta et verbose søk på gruppenavn
     *
     * @return false om flere verdier er oppgitt, retuner også false om gruppen ikke eksiterer. Ellers retunerer den true om gruppen ekisterer.
     */
    public boolean groupExist(int groupID, String name, boolean autoCompleteName) {
        if (groupID != 0 && name != null) {
            return false;
        } else {
            if (name != null) {
                String result;
                if (autoCompleteName) {
                    result = sqlHandler.getColumn("SELECT * FROM `groups` WHERE `group_name` LIKE '%" + name + "%'");
                } else {
                    result = sqlHandler.getColumn("SELECT * FROM `groups` WHERE `group_name` = '" + name + "'");
                }

                return result != null && !result.isEmpty();
            }
            if (groupID != 0) {
                String result = sqlHandler.getColumn("SELECT * FROM `groups` WHERE `id` = " + groupID);
                return result != null && !result.isEmpty();
            }
        }
        return false;
    }

    /**
     * Sjekker om brukerne har invitasjoner
     *
     * @param player spiller som skal sjekkes
     *
     * @return false hvis ingen invitasjoner eksiterer, ellers true
     */
    public boolean hasInvites(Player player) {
        String result = sqlHandler.getColumn("SELECT COUN(id) FROM `groupinvites` WHERE `userID` = " + userHandler.getUserId(player));
        return result != null && !result.isEmpty();
    }

    /**
     * Oppretter en gruppe
     *
     * @param groupName Navnet på gruppen
     * @param player    Spiller som oppretter gruppen
     *
     * @return false om feil oppstod, ellers true
     *
     * @throws SQLException
     */
    public boolean createGroup(String groupName, Player player) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        int id = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `groups` (`id`, `group_name`, `ownerID`) VALUES(NULL, ?, ?)");
            ps.setString(1, groupName);
            ps.setInt(2, userHandler.getUserId(player));
            ps.executeUpdate();
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke legge til gruppen i databasen: ", e);
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
        id = getGroupIDFromGroupName(groupName);
        if (id != 0) {
            if (sqlHandler.update("INSERT INTO `groupusers` VALUES(NULL, " + id + ", " + userHandler.getUserId(player) + ")")) {
                addGroup(id, groupName);
                setGroupIdInMap(player, id);
                this.groupLogHandler.logMember(userHandler.getUserId(player), 0, "Navn:" + groupName, id, GroupLogMember.create);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sender en invitasjon til en spiller
     *
     * @param fromPlayer Spiller som sender invitasjon
     * @param toPlayer   Spiller som skal motta
     *
     * @return 1 hvis spilleren allerede har invitasjon, 2 hvis spilleren ikke ble funnet (offline), 3 hvis spilleren er online (sendt), 4 hvis spilleren er offline (sendt)
     */
    public int sendInvite(Player fromPlayer, String toPlayer) {
        Player victim = plugin.playerMatch(toPlayer);
        if (victim != null) {
            if (!checkInviteExist(getGroupID(fromPlayer), victim)) {
                this.sqlHandler.insert("INSERT INTO `groupinvites` VALUES(NULL, " + this.userHandler.getUserId(victim) + ", " + getGroupID(fromPlayer) + ", " + this.userHandler.getUserId(fromPlayer) + ")");
                int id = this.sqlHandler.getColumnInt("SELECT `group_id` FROM `groupinvites` WHERE group_id = " + getGroupID(fromPlayer) + " AND `userID` = " + this.userHandler.getUserId(victim), "group_id");
                victim.sendMessage(ChatColor.DARK_GREEN + "Du ble invitert til gruppen: " + ChatColor.WHITE + this.getGroupData(getGroupID(fromPlayer)).getGroupName() + ChatColor.DARK_GREEN + ". Av: " + ChatColor.WHITE + fromPlayer.getName());
                victim.sendMessage(ChatColor.DARK_GREEN + "Skriv: /gr godta " + ChatColor.WHITE + id + ChatColor.DARK_GREEN + ". For å bli med.");
                this.groupLogHandler.logMember(this.userHandler.getUserId(fromPlayer), this.userHandler.getUserId(victim), null, getGroupID(fromPlayer), GroupLogMember.invite);
                broadcastMessage(getGroupID(fromPlayer), victim.getName() + ChatColor.DARK_GREEN + " ble invitert til gruppen.");
                return 3;
            } else {
                return 1;
            }
        } else {
            String victimName = userHandler.getUsernameFromDBAutoComplete(toPlayer);
            if (victimName != "") {
                if (!checkInviteExist(getGroupID(fromPlayer), victimName)) {
                    int victimID = this.userHandler.getUserId(victimName);
                    sqlHandler.insert("INSERT INTO `groupinvites` VALUES(NULL, " + victimID + ", " + getGroupID(fromPlayer) + ", " + userHandler.getUserId(fromPlayer) + ")");
                    this.groupLogHandler.logMember(userHandler.getUserId(fromPlayer), victimID, null, getGroupID(fromPlayer), GroupLogMember.invite);
                    broadcastMessage(getGroupID(fromPlayer), victimName + ChatColor.DARK_GREEN + " ble invitert til gruppen.");
                    return 4;
                } else {
                    return 1;
                }
            } else {
                return 2;
            }
        }
    }

    /**
     * Godtar en gruppe invitasjon
     *
     * @param player  Spiller som godtar
     * @param groupID gruppe ID på den han godtok
     *
     * @return 1 hvis gruppen ikke eksiterer, 2 hvis spilleren ikke har invitasjon til den gruppen, 3 hvis alt gikk ok
     */
    public int acceptInvite(Player player, int groupID) {
        if (groupExist(groupID, null, false)) {
            if (checkInviteExist(groupID, player.getName())) {
                boolean insertUser = sqlHandler.update("INSERT INTO `groupusers` VALUES(NULL, " + groupID + ", " + userHandler.getUserId(player) + ")");
                boolean deleteUserInvite = sqlHandler.update("DELETE FROM `groupinvites` WHERE userID = " + userHandler.getUserId(player) + " AND group_id = " + groupID);
                if (insertUser && deleteUserInvite) {
                    setGroupIdInMap(player, groupID);
                    this.groupLogHandler.logMember(this.userHandler.getUserId(player), 0, null, getGroupID(player), GroupLogMember.accept);
                    broadcastMessage(getGroupID(player), player.getName() + ChatColor.DARK_GREEN + " ble med i gruppen.");
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            return 1;
        }

        return 0;
    }

    /**
     * Avslår en invitasjon
     *
     * @param player Spiller som godtar
     *
     * @return true hvis alt gikk ok
     */
    public boolean denyInvite(Player player, int groupId) {
        return this.denyInvite(this.userHandler.getUserId(player), groupId);
    }

    public boolean denyInvite(String player, int groupId) {
        return this.denyInvite(this.userHandler.getUserId(player), groupId);
    }

    public boolean denyInvite(int userId, int groupId) {
        return sqlHandler.update("DELETE FROM `groupinvites` WHERE userID = " + userId + " AND group_id = " + groupId);
    }

    /**
     * Avslår alle invitasjoner
     *
     * @param player Spiller som det skal slettes på
     *
     * @return true hvis alt gikk ok
     */
    public boolean denyAllInvites(Player player) {
        return sqlHandler.update("DELETE FROM `groupinvites` WHERE userID = " + userHandler.getUserId(player));
    }

    /**
     * Forlater en gruppe
     *
     * @param player Spiller som forlater
     *
     * @return true hvis alt gikk ok
     */
    public boolean leaveGroup(Player player) {
        if (sqlHandler.update("DELETE FROM `groupusers` WHERE userID = " + this.userHandler.getUserId(player))) {
            this.groupLogHandler.logMember(this.userHandler.getUserId(player), 0, null, getGroupID(player), GroupLogMember.leave);
            broadcastMessage(getGroupID(player), player.getName() + ChatColor.DARK_GREEN + " forlot gruppen.");
            setGroupIdInMap(player, 0);
            return true;
        }
        return false;
    }

    /**
     * Forlater en gruppe
     *
     * @param stiring Spiller som forlater
     *
     * @return true hvis alt gikk ok
     */
    /*public boolean leaveGroup(String player) {
        if (sqlHandler.update("DELETE FROM `groupusers` WHERE userID = " + this.userHandler.getUserId(player))) {
			this.groupLogHandler.logMember(this.userHandler.getUserId(player), 0, null, getGroupIDFromName(player), GroupLogMember.leave);
			broadcastMessage(getGroupIDFromName(player), player + ChatColor.DARK_GREEN + " forlot gruppen.");
			updateGroupIdInMap(player);
			return true;
		}
		return false;
	}
	
	/**
	 * Sletter en gruppe totalt
	 * @param player Spiller som slettet gruppen
	 * @return true hvis alt gikk ok
	 */
    public boolean deleteGroup(Player player) {
        boolean deleteInvites = sqlHandler.update("DELETE FROM `groupinvites` WHERE `group_id` = " + getGroupID(player));
        boolean deleteUsers = sqlHandler.update("DELETE FROM `groupusers` WHERE `group_id` = " + getGroupID(player));
        boolean deleteGroup = sqlHandler.update("DELETE FROM `groups` WHERE `id` = " + getGroupID(player));

        if (deleteInvites && deleteUsers && deleteGroup) {
            setGroupIdInMap(player, 0);
            return true;
        }

        return false;
    }

    /**
     * Skifter eier på en gruppe
     *
     * @param player Spiller som endret eier
     *
     * @return 1 hvis spilleren ikke eksiterer (eller flere funnet), 2 hvis ikke samme gruppe, 3 hvis alt gikk ok
     */
    public int updateOwner(Player player, String newOwner) {
        Player victim = this.plugin.playerMatch(newOwner);
        if (victim != null) {
            if (getGroupIDFromName(victim.getName()) == getGroupID(player)) {
                if (sqlHandler.update("UPDATE `groups` SET `ownerID` = " + this.userHandler.getUserId(victim) + " WHERE id = " + getGroupID(player))) {
                    this.groupLogHandler.logMember(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), null, getGroupID(player), GroupLogMember.updateowner);
                    broadcastMessage(getGroupID(player), ChatColor.DARK_GREEN + "Eier av gruppen ble skiftet til: " + ChatColor.GRAY + victim.getName() + ChatColor.DARK_GREEN + ".");
                    groupData.get(getGroupID(player)).setGroupOwner(victim.getName());
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            String victimName = this.userHandler.getUsernameFromDBAutoComplete(newOwner);
            if (victimName != null) {
                if (getGroupIDFromName(victimName) == getGroupID(player)) {
                    if (sqlHandler.update("UPDATE `groups` SET `ownerID` = " + this.userHandler.getUserId(victimName) + " WHERE id = " + getGroupID(player))) {
                        this.groupLogHandler.logMember(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), null, getGroupID(player), GroupLogMember.updateowner);
                        broadcastMessage(getGroupID(player), ChatColor.DARK_GREEN + "Eier av gruppen ble skiftet til: " + ChatColor.GRAY + victimName + ChatColor.DARK_GREEN + ".");
                        groupData.get(getGroupID(player)).setGroupOwner(victimName);
                        return 3;
                    }
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Sparker en spiller ut av gruppen
     *
     * @param player Spiller som utfører handlingen
     * @param toKick Hvem som ble sparket ut
     *
     * @return 1 hvis spilleren ikke eksiterer (eller flere funnet), 2 hvis ikke samme gruppe, 3 hvis alt gikk ok
     */
    public int kickMember(Player player, String toKick) {
        Player victim = this.plugin.playerMatch(toKick);
        if (victim != null) {
            if (getGroupIDFromName(victim.getName()) == getGroupID(player)) {
                if (sqlHandler.update("DELETE FROM `groupusers` WHERE `userID` = " + this.userHandler.getUserId(victim))) {
                    this.groupLogHandler.logMember(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), null, getGroupID(player), GroupLogMember.kick);
                    this.userHandler.getOnlineUsers().get(victim).setGroupId(0);
                    broadcastMessage(getGroupID(player), victim.getName() + ChatColor.DARK_GREEN + " ble sparket ut av gruppen.");
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            String victimName = this.userHandler.getUsernameFromDBAutoComplete(toKick);
            if (victimName != null) {
                if (getGroupIDFromName(victimName) == getGroupID(player)) {
                    if (sqlHandler.update("DELETE FROM `groupusers` WHERE `userID` = " + this.userHandler.getUserId(victimName))) {
                        this.groupLogHandler.logMember(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), null, getGroupID(player), GroupLogMember.kick);
                        broadcastMessage(getGroupID(player), victimName + ChatColor.DARK_GREEN + " ble sparket ut av gruppen.");
                        return 3;
                    }
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Endrer gruppe innstillinger for farge
     *
     * @param groupID gruppe ID som skal endres
     * @param id      til farge
     *
     * @return true om alt gikk ok
     */
    public boolean editGroupOptionColor(int groupID, ChatColor color) {
        if (sqlHandler.update("UPDATE `groups` SET `" + OptionColumns.colorID.name() + "` = " + toInt(color) + " WHERE id = " + groupID)) {
            addGroup(groupID, getGroupData(groupID).getGroupName());
            this.groupLogHandler.logMember(0, 0, "Farge endret: " + color.name(), groupID, GroupLogMember.groupoption);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Endrer gruppe innstillinger
     *
     * @param groupID  gruppe ID som skal endres
     * @param editWhat Hva skal endres
     * @param enabled  true om den er på, false om den er av
     *
     * @return true om alt gikk ok
     */
    public boolean editGroupOption(int groupID, OptionColumns editWhat, boolean enabled) {
        int value = (enabled) ? 1 : 0;
        if (sqlHandler.update("UPDATE `groups` SET `" + editWhat.name() + "` = " + value + " WHERE id = " + groupID)) {
            addGroup(groupID, getGroupData(groupID).getGroupName());
            this.groupLogHandler.logMember(0, 0, "Innstilling endret: " + editWhat.name() + "|" + value, groupID, GroupLogMember.groupoption);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkInviteExist(int groupID, Player player) {
        int result = this.sqlHandler.getColumnInt("SELECT COUNT(id) AS total FROM groupinvites WHERE group_id = " + groupID + " AND userID = " + this.userHandler.getUserId(player), "total");
        return result != 0;
    }

    public boolean checkInviteExist(int groupID, String player) {
        int result = this.sqlHandler.getColumnInt("SELECT COUNT(id) AS total FROM groupinvites WHERE group_id = " + groupID + " AND userID = " + this.userHandler.getUserId(player), "total");
        return result != 0;
    }

    /**
     * Henter gruppenavn fra databasen
     *
     * @param player Spiller som det skal hentes på
     *
     * @return gruppenavn
     */
    public String getGroupNameFromDB(Player player) {
        String groupName = sqlHandler.getColumn("SELECT `group_name` FROM `groups` WHERE `id` = " + getGroupID(player));
        if (groupName == null) {
            groupName = "feil oppstod!";
        }
        return groupName;
    }

    /**
     * Henter ut gruppenavn basert på gruppe ID
     *
     * @param groupID gruppe ID det skal hentes fra
     *
     * @return grupppenavn
     */
    public String getGroupNameFromID(int groupID) {
        if (groupData.containsKey(groupID)) {
            return groupData.get(groupID).getGroupName();
        } else {
            String groupName = sqlHandler.getColumn("SELECT `group_name` FROM `groups` WHERE `id` = " + groupID);
            if (groupName == null) {
                groupName = "feil oppstod!";
            }
            return groupName;
        }
    }

    /**
     * Henter ut gruppe eier fra databasne
     *
     * @param groupID gruppe ID det skal hentes på
     *
     * @return eier
     */
    public String getGroupOwnerFromDB(int groupID) {
        int owner = sqlHandler.getColumnInt("SELECT `ownerID` FROM `groups` WHERE id = " + groupID, "ownerID");
        return userHandler.getNameFromId(owner);
    }

    /**
     * Henter ut gruppeid fra spiller navn fra databasbe
     *
     * @param String playerName spiller som det skal sjekkes på
     *
     * @return (int) gruppe ID
     */
    public int getGroupIDFromName(String playerName) {
        int groupID = 0;
        groupID = sqlHandler.getColumnInt("SELECT `group_id` FROM `groupusers` WHERE `userID`  = " + this.userHandler.getUserId(playerName), "group_id");
        return groupID;
    }

    /**
     * Henter ut gruppeid fra spiller id fra databasbe
     *
     * @param id UserId spiller som det skal sjekkes på
     *
     * @return (int) gruppe ID
     */

    public int getGroupIDFromUserId(int UserId) {
        int groupID = 0;
        groupID = sqlHandler.getColumnInt("SELECT `group_id` FROM `groupusers` WHERE `userID`  = " + UserId, "group_id");
        return groupID;
    }

    /**
     * Henter ut gruppeid fra spiller navn fra databasbe
     *
     * @param String playerName spiller som det skal sjekkes på
     *
     * @return (int) gruppe ID
     */
    public int getGroupIDFromGroupName(String groupName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int groupID = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `id` FROM `groups` WHERE `group_name` = ?");
            ps.setString(1, groupName);
            rs = ps.executeQuery();

            while (rs.next()) {
                groupID = rs.getInt(1);
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
        return groupID;
    }

    /**
     * Henter ut gruppe instillinger fra DB
     *
     * @param groupID       gruppe ID
     * @param OptionColumns hva som skal hentes
     *
     * @return verdi på innstilling (1, 0)
     */
    public int getGroupOption(int groupID, OptionColumns OptionColumns) {
        return sqlHandler.getColumnInt("SELECT `" + OptionColumns.name() + "` FROM `groups` WHERE id = " + groupID, OptionColumns.name());
    }

    /**
     * Tilpasset for å hente ut antall gruppeinvitasjoner ved login
     *
     * @param player Spiller som skal sjekkes
     *
     * @return int antall invites
     */
    public int getInvitesForLogin(Player player) {
        int result = sqlHandler.getColumnInt("SELECT COUNT(id) AS totalt FROM `groupinvites` WHERE userID = " + this.userHandler.getUserId(player), "totalt");
        if (result != 0) {
            return result;
        } else {
            return 0;
        }
    }

    /**
     * Retunerer hvor mange spillere det er i en bruppe
     *
     * @param groupID Gruppe ID som skal sjekkes
     *
     * @return int Antall det er i en gruppe
     */
    public int getUserCount(int groupID) {
        int result = sqlHandler.getColumnInt("SELECT COUNT(userID) AS totalt FROM `groupusers` WHERE group_id = " + groupID, "totalt");
        if (result != 0) {
            return result;
        } else {
            return 0;
        }
    }

    /**
     * Henter ut alle invitasjoner på en spiller
     *
     * @param player Spiller det skal hentes på
     *
     * @return 2-D ArrayList med alle invitasjoner
     */
    public ArrayList<ArrayList<String>> getInvites(Player player) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT groupinvites.fromID, groups.group_name, groupinvites.group_id FROM `groupinvites`, `groups` WHERE groupinvites.userID = " + this.userHandler.getUserId(player) + " AND groupinvites.group_id = groups.id");
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(new ArrayList<String>(Arrays.asList(rs.getString(1), rs.getString(2), rs.getString(3))));
            }
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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

        return result;
    }

    /**
     * Henter ut gruppenavn + gruppe eier
     *
     * @param groupID gruppe ID det skal hentes på
     *
     * @return ArrayList med informasjonen
     */
    public ArrayList<String> getGroupInformation(int groupID) {
        ArrayList<String> result = new ArrayList<String>();
        if (groupData.containsKey(groupID)) {
            result.add(groupData.get(groupID).getGroupName());
            result.add(groupData.get(groupID).getGroupOwner());
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement("SELECT groups.group_name, groups.ownerID FROM groups WHERE groups.id = " + groupID);
                rs = ps.executeQuery();

                while (rs.next()) {
                    result.add(rs.getString(1));
                    result.add(rs.getString(2));
                }
                // conn.close();
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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
        return result;
    }

    /**
     * Henter ut en liste med alle medlemmer i gruppen
     *
     * @param groupID gruppe ID det skal hentes på
     *
     * @return ArrayList med medlemene
     */
    public ArrayList<Integer> getGroupMembers(int groupID) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT groupusers.userID FROM `groupusers` WHERE group_id = " + groupID);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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

        return result;
    }

    /**
     * Tilpasset for å liste alle synlige grupper i en kommando
     *
     * @return Alle grupper som har: publicOn på 1 (true). Henter ut gruppenavn + eier.
     */
    public ArrayList<ArrayList<String>> getListGroups() {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT groups.group_name, groups.ownerID FROM `groups` WHERE groups.publicOn = 1 ORDER BY groups.group_name ASC");
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(new ArrayList<String>(Arrays.asList(rs.getString(1), rs.getString(2))));
            }
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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

        return result;
    }

    /**
     * Tilpasset for å liste alle kommandoer
     *
     * @return Alle grupper som har: publicOn på 1 (true). Henter ut gruppenavn + eier.
     */
    public ArrayList<String> getHelpList(Player player) {
        ArrayList<String> commandLists = new ArrayList<String>();

        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr hjelp " + ChatColor.GRAY + "(sidetall)" + ChatColor.WHITE + " - Viser side 1 eller (sidetall).");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr ny " + ChatColor.GRAY + "[navn]" + ChatColor.WHITE + " - Oppretter en ny gruppe med [navn].");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr liste " + ChatColor.GRAY + "(sidetall) " + ChatColor.WHITE + " - Viser alle offentlige grupper.");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr invitasjoner " + ChatColor.GRAY + "(side) " + ChatColor.WHITE + " - Viser dine 10 siste invitasjoner eller (sidetall).");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr godta " + ChatColor.GRAY + "[id]" + ChatColor.WHITE + " - Godtar en invitasjon.");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr avslå " + ChatColor.GRAY + "[id]" + ChatColor.WHITE + " - Avlsår en invitasjon.");
        commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr avslå alle" + ChatColor.WHITE + " - Sletter alle dine invitasjoner.");

        if (isInGroup(player)) {
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr info" + ChatColor.WHITE + " - Viser informasjon om gruppen din.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr info2" + ChatColor.WHITE + " - Viser informasjon om hva som er påslått");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr invlist" + ChatColor.WHITE + " - Viser alle utestående invitasjoner.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr inv " + ChatColor.GRAY + "[spiller]" + ChatColor.WHITE + " - Inviterer [spiller] til gruppen.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr uninv " + ChatColor.GRAY + "[spiller]" + ChatColor.WHITE + " - Sletter [spiller]ens invitasjon til gruppen.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr forlat " + ChatColor.WHITE + " - Forlater gruppen du er i.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr who" + ChatColor.WHITE + " - Viser hvem som er pålogget i gruppen.");
            commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr loc" + ChatColor.GRAY + "[spiller]" + ChatColor.WHITE + " - Viser hvor [spiller] befinner seg.");

            if (getGroupData(getGroupID(player)).getGroupOwner().equalsIgnoreCase(player.getName())) {
                commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr kick " + ChatColor.GRAY + "[spiller]" + ChatColor.WHITE + " - Kicker en spiller fra gruppen.");
                commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr eier " + ChatColor.GRAY + "[spiller]" + ChatColor.WHITE + " - Skifter eier av gruppen.");
                commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr mod list" + ChatColor.WHITE + " - Viser hva du kan endre på.");
                commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr mod info " + ChatColor.GRAY + "[navn]" + ChatColor.WHITE + " - Viser informasjon om [navn] hva den gjør.");
                commandLists.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr mod inst " + ChatColor.GRAY + "[navn] [av/på]" + ChatColor.WHITE + " - Endrer på angitt innstilling [navn] til [av/på].");
            }
        }

        return commandLists;
    }

    /**
     * Henter ut informasjon ang. bank
     *
     * @param groupID gruppe ID det skal hentes på
     *
     * @return ArrayList med informasjonen
     */
    private ArrayList<Integer> getGroupBankFromDB(int groupID) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `bank`, `bankOn` FROM `groups` WHERE `id` = " + groupID);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getInt(1));
                result.add(rs.getInt(2));
            }
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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

        return result;
    }

	/*
	 * - Vakt + stab
	 */

    /**
     * Oppdaterer eier på en gruppe
     *
     * @param player   Spilleren som utfører kommandoen.
     * @param groupID  Gruppe ID som det skal skiftes eier på
     * @param newOwner Nye eieren
     *
     * @return 1 hvis brukeren ikke eksiterer, 2 hvis gruppen ikke eksiterer, 3 hvis spilleren ikke er i samme gruppe som gruppeID som skal endres. 4 hvis det ble utført
     */
    public int admUpdateGroupOwner(Player player, int groupID, String newOwner) {
        Player victim = this.plugin.playerMatch(newOwner);
        if (victim != null) {
            if (groupExist(groupID, null, false)) {
                if (getGroupIDFromName(victim.getName()) == groupID) {
                    if (sqlHandler.update("UPDATE `groups` SET ownerID = " + this.userHandler.getUserId(victim) + " WHERE id = " + groupID)) {
                        this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, "Tidligere eier: " + victim.getName(), groupID, GroupLogAdmin.updateowner);
                        broadcastMessage(groupID, ChatColor.DARK_GREEN + "Eier av gruppen ble skiftet til: " + ChatColor.WHITE + newOwner + ChatColor.DARK_GREEN + ".");
                        broadcastMessage(groupID, ChatColor.DARK_GREEN + "Av: " + player.getDisplayName());
                        this.groupData.get(groupID).setGroupOwner(victim.getName());
                        return 4;
                    }
                } else {
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            String victimName = this.userHandler.getUsernameFromDB(newOwner);
            if (victimName != null) {
                if (groupExist(groupID, null, false)) {
                    if (getGroupIDFromName(victimName) == groupID) {
                        if (sqlHandler.update("UPDATE `groups` SET ownerID = " + this.userHandler.getUserId(victimName) + " WHERE id = " + groupID)) {
                            this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, "Tidligere eier: " + victimName, groupID, GroupLogAdmin.updateowner);
                            broadcastMessage(groupID, ChatColor.DARK_GREEN + "Eier av gruppen ble skiftet til: " + ChatColor.WHITE + newOwner + ChatColor.DARK_GREEN + ".");
                            broadcastMessage(groupID, ChatColor.DARK_GREEN + "Av: " + player.getDisplayName());
                            this.groupData.get(groupID).setGroupOwner(victimName);
                            return 4;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Retunerer gruppenavn hvilken spiller er i
     *
     * @param playerName Spillernavnet det skal søkes etter
     *
     * @return null hvis spilleren ikke ble funnet, ellers gruppenavnet + id
     */
    public ArrayList<String> admGroupSearchPlayer(String playerName) {
        ArrayList<String> result = new ArrayList<String>();
        Player victim = this.plugin.playerMatch(playerName);

        if (victim != null) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement("SELECT groups.group_name, groups.id FROM `groupusers`, `groups` WHERE groupusers.group_id = groups.id AND groupusers.userID = " + this.userHandler.getUserId(victim));
                rs = ps.executeQuery();

                while (rs.next()) {
                    result.add(victim.getName());
                    result.add(rs.getString(1));
                    result.add(Integer.toString(rs.getInt(2)));
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement("SELECT groups.group_name, groups.id FROM `groupusers`, `groups` WHERE groupusers.group_id = groups.id AND groupusers.userID = " + this.userHandler.getUserId(playerName));
                rs = ps.executeQuery();

                while (rs.next()) {
                    result.add(playerName);
                    result.add(rs.getString(1));
                    result.add(Integer.toString(rs.getInt(2)));
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under åpning)", ex);
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
        return result;
    }

    /**
     * @param groupID Gruppe ID spilleren skal bli medlem av
     * @param player  Spilleren skal bli med i gruppen
     *
     * @return 1 hvis gruppen ikke eksiterer ellers 2 hvis brukeren ble medlem av gruppen.
     */
    public int admJoinGroup(int groupID, Player player) {
        if (groupExist(groupID, null, false)) {
            int oldGroup = getGroupID(player);
            if (oldGroup != 0) {
                this.userHandler.getPlayerData(player).setOldGroupID(oldGroup);
                leaveGroup(player);
            }
            if (this.sqlHandler.update("INSERT INTO `groupusers` VALUES(NULL, " + groupID + ", " + this.userHandler.getUserId(player) + ")")) {
                this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), 0, 0, null, groupID, GroupLogAdmin.joingroup);
                updateGroupIdInMap(player);
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    /**
     * Flytter spilleren til en annen gruppe
     *
     * @param groupID   Gruppe ID spilleren skal bli medlem av
     * @param player    Spilleren som utfører kommandoen
     * @param thePlayer Spilleren som skal flyttes
     *
     * @return 1 hvis spilleren ikke eksiterer, 2 hvis den nye gruppen ikke eksiterer, 3 hvis spilleren er eier av en annen gruppe, 4 hvis spilleren ble flyttet
     */
    public int admMovePlayer(int groupID, Player player, String thePlayer) {
        if (!groupExist(groupID, null, false)) {
            return 2;
        } else {
            Player victim = this.plugin.playerMatch(thePlayer);
            if (victim != null) {
                int oldGroup = this.userHandler.getPlayerData(victim).getGroupId();
                if (oldGroup != 0) {
                    if (getGroupOwnerFromDB(oldGroup).equalsIgnoreCase(victim.getName())) {
                        return 3;
                    }
                    this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, "Tidligere gruppe: " + oldGroup, groupID, GroupLogAdmin.moveplayer);
                    this.sqlHandler.update("UPDATE `groupusers` SET group_id = " + groupID + " WHERE userID = " + this.userHandler.getUserId(victim));
                    updateGroupIdInMap(victim);
                    return 4;
                } else {
                    this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, null, groupID, GroupLogAdmin.moveplayer);
                    this.sqlHandler.insert("INSERT INTO `groupusers` VALUES(NULL, " + groupID + ", " + this.userHandler.getUserId(victim) + ")");
                    updateGroupIdInMap(victim);
                    return 4;
                }
            } else {
                String victimName = this.userHandler.getUsernameFromDB(thePlayer);
                if (victimName != null) {
                    int oldGroup = getGroupIDFromName(victimName);
                    if (oldGroup != 0) {
                        if (getGroupOwnerFromDB(oldGroup).equalsIgnoreCase(victimName)) {
                            return 3;
                        }
                        this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, "Tidligere gruppe: " + oldGroup, groupID, GroupLogAdmin.moveplayer);
                        this.sqlHandler.update("UPDATE `groupusers` WHERE SET group_id = " + groupID + " WHERE userID = " + this.userHandler.getUserId(victimName));
                        return 4;
                    } else {
                        this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, null, groupID, GroupLogAdmin.moveplayer);
                        this.sqlHandler.insert("INSERT INTO `groupusers` VALUES(NULL, " + groupID + ", " + this.userHandler.getUserId(victimName) + ")");
                        return 4;
                    }
                } else {
                    return 1;
                }
            }
        }
    }

    /**
     * Sletter hele gruppen
     *
     * @param player  Spilleren som utfører kommandoen
     * @param groupID Gruppe ID som skal slettes
     *
     * @return 1 hvis gruppen ikke eksiterer, 2 hvis gruppen ble slettet
     */
    public int admDeleteGroup(Player player, int groupID) {
        if (groupExist(groupID, null, false)) {
            boolean deleteInvites = sqlHandler.update("DELETE FROM `groupinvites` WHERE group_id = " + groupID);
            boolean deleteUsers = sqlHandler.update("DELETE FROM `groupusers` WHERE group_id = " + groupID);
            boolean deleteGroup = sqlHandler.update("DELETE FROM `groups` WHERE id = " + groupID);

            if (deleteInvites && deleteUsers && deleteGroup) {
                this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), 0, 0, null, groupID, GroupLogAdmin.deletegroup);
                this.groupData.remove(groupID);
                for (Player vplayer : this.plugin.getServer().getOnlinePlayers()) {
                    if (this.userHandler.getPlayerData(vplayer).getGroupId() == groupID) {
                        updateGroupIdInMap(vplayer);
                    }
                }
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    /**
     * Bytter navn på gruppen
     *
     * @param player  Spiller som utfører kommandoen
     * @param groupID Gruppe ID det skal skiftes navn på
     * @param newName Nye navnet til gruppen
     *
     * @return 1 hvis gruppen ikke eksiterer, 2 hvis gruppen fikk skiftet navn.
     */
    public int admRenameGroup(Player player, int groupID, String newName) {
        if (groupExist(groupID, null, false)) {
            String oldName = this.groupData.get(groupID).getGroupName();
            if (this.sqlHandler.update("UPDATE `groups` SET group_name = '" + newName + "' WHERE id = " + groupID)) {
                this.groupData.get(groupID).setGroupName(newName);
                this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), 0, 0, "Tidligere navn: " + oldName, groupID, GroupLogAdmin.renamegroup);
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    /**
     * Flytter spilleren tilbake til sin orginale gruppe
     *
     * @param player Spilleren som skal flyttes tilbake
     *
     * @return 0 hvis alt gikk galt, 1 hvis spilleren ikke har noe gruppe å gå tilbake til, 2 hvis gamle gruppen ikke eksiterer. <b>3 hvis alt gikk ok.</b>
     */
    public int admGoBackToGroup(Player player) {
        int groupID = this.userHandler.getPlayerData(player).getOldGroupID();
        if (groupID != 0) {
            if (groupExist(groupID, null, false)) {
                if (leaveGroup(player)) {
                    if (sqlHandler.update("INSERT INTO `groupusers` VALUES(NULL, " + groupID + ", " + userHandler.getUserId(player) + ")")) {
                        updateGroupIdInMap(player);
                        return 3;
                    } else {
                        return 0;
                    }
                }
            } else {
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    /**
     * Sparker en spiller ut av gruppen
     *
     * @param player Spiller som utfører handlingen
     * @param toKick Hvem som ble sparket ut
     *
     * @return 1 hvis spilleren ikke eksiterer (eller flere funnet), 2 hvis gruppen ikke eksiterer, 3 hvis spilleren ikke er i en gruppe, 4 hvis spilleren ikke er i samme gruppe som angitt. <b>5 hvis alt gikk ok</b>
     */
    public int admKickMember(int groupID, Player player, String toKick) {
        Player victim = this.plugin.playerMatch(toKick);
        if (victim != null) {
            if (groupExist(groupID, null, false)) {
                if (isInGroup(victim)) {
                    if (getGroupID(victim) == groupID) {
                        if (sqlHandler.update("DELETE FROM `groupusers` WHERE `userID` = " + this.userHandler.getUserId(victim))) {
                            this.userHandler.getOnlineUsers().get(victim).setGroupId(0);
                            this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victim), 0, null, getGroupID(player), GroupLogAdmin.kick);
                            broadcastMessage(groupID, victim.getName() + ChatColor.DARK_GREEN + " ble fjernet fra gruppen av en vakt/stab.");
                            return 5;
                        }
                    } else {
                        return 4;
                    }
                } else {
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            String victimName = this.userHandler.getUsernameFromDB(toKick);
            if (!victimName.isEmpty()) {
                if (groupExist(groupID, null, false)) {
                    int groupId = getGroupIDFromName(victimName);
                    if (groupId != 0) {
                        if (groupId == groupID) {
                            if (sqlHandler.update("DELETE FROM `groupusers` WHERE `group_id` = " + groupId + " AND `userID` = (SELECT us.id FROM Minecraftno.users us WHERE name = '" + victimName + "')")) {
                                this.groupLogHandler.logAdmin(this.userHandler.getUserId(player), this.userHandler.getUserId(victimName), 0, null, getGroupID(player), GroupLogAdmin.kick);
                                broadcastMessage(groupID, victimName + ChatColor.DARK_GREEN + " ble fjernet fra gruppen av en vakt/stab.");
                                return 5;
                            }
                        } else {
                            return 4;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        }
        return 0;
    }

    public Map<String, String> getInvites(int groupId) {
        Map<String, String> invites = new HashMap<>();

        Connection db = this.plugin.getConnection();

        try {
            PreparedStatement query = db.prepareStatement("SELECT u1.name AS invited, u2.name AS inviter FROM Hardwork.groupinvites i LEFT JOIN Minecraftno.users u1 ON i.userID=u1.id LEFT JOIN Minecraftno.users u2 ON i.fromID=u2.id WHERE i.group_id=?");
            query.setInt(1, groupId);
            ResultSet result = query.executeQuery();

            while (result.next()) {
                invites.put(result.getString("invited"), result.getString("inviter"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invites;
    }

	/*
	 * Bank
	 */

    //TODO Bank må skrives ferdig!

    /**
     * Oppdatere antall gull banken i groupData med info fra DB
     *
     * @param groupID gruppe ID det skal gjøres på (sjekker om gruppen eksiterer først)
     *
     * @return false hvis gruppen ikke eksiterer, true hvis alt gikk bra
     */
    public boolean updateBank(int groupID) {
        //TODO logg
        if (groupExist(groupID, null, false)) {
            int bank = this.sqlHandler.getColumnInt("SELECT `bank` FROM `groups` WHERE `id` = " + groupID, "bank");
            this.groupData.get(groupID).setBank(bank);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Setter inn gull på bankkonto til en gruppe
     *
     * @param amount antall gull som skal settes inn
     * @param player spilleren som utfører handlingen
     *
     * @return false hvis feil oppstod, true hvis gullet ble satt inn
     */
    public boolean insertAmount(int amount, Player player) {
        //TODO logg
        if (sqlHandler.update("UPDATE `groups` SET `bank` = `bank` + " + amount + " WHERE `id` = " + userHandler.getPlayerData(player).getGroupId())) {
            updateBank(userHandler.getPlayerData(player).getGroupId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fjerner gull fra bankkonto til en gruppe
     *
     * @param amount antall gull som skal fjernes
     * @param player spilleren som utfører handlingen
     *
     * @return 1 hvis ikke nok gull i banken, 2 hvis alt ble ok
     */
    public int removeAmount(int amount, Player player) {
        //TODO logg
        if (!(groupData.get(userHandler.getPlayerData(player).getGroupId()).getBank() > amount)) {
            if (sqlHandler.update("UPDATE `groups` SET `bank` = `bank` - " + amount + " WHERE `id` = " + userHandler.getPlayerData(player).getGroupId())) {
                updateBank(userHandler.getPlayerData(player).getGroupId());
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    /**
     * Overfører gull til en annen gruppe - kun eier kan overføre
     *
     * @param amount  antall gull som overføres
     * @param player  spilleren som utfører handlingen
     * @param groupID gruppe ID som mottar gullet
     *
     * @return 1 hvis personen ikke er eier, 2 hvis gruppen har for lite gull, 3 hvis gruppen ikke eksiterer ellers 4 hvis det ble sendt
     */

    public int transferMoney(int amount, Player player, int groupID) {
        //TODO logg
        if (groupData.get(userHandler.getPlayerData(player).getGroupId()).getGroupOwner().equalsIgnoreCase(player.getName())) {
            if (!(groupData.get(userHandler.getPlayerData(player).getGroupId()).getBank() > amount)) {
                if (groupExist(groupID, null, false)) {
                    if (sqlHandler.update("UPDATE `groups` SET `bank` = `bank` - " + amount + " WHERE `id` = " + groupID)) {
                        removeAmount(amount, player);
                        updateBank(userHandler.getPlayerData(player).getGroupId());
                        updateBank(groupID);
                        return 4;
                    }
                } else {
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }
}