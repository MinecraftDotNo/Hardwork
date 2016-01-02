package no.minecraft.Minecraftno.handlers.player;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.MySQLHandler;
import no.minecraft.Minecraftno.handlers.SavedObject;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.User;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class UserHandler {

    private final Minecraftno plugin;
    private final MySQLHandler sqlHandler;
    private GroupHandler groupHandler;
    private Vector<Player> regUsers;
    private ConcurrentHashMap<Player, PlayerData> onlineUsers;
    private ConcurrentHashMap<Player, PermissionAttachment> permissionUsers;

    public static final String SERVER_USERNAME = "server-user";

    public UserHandler(Minecraftno instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.regUsers = new Vector<Player>();
        this.onlineUsers = new ConcurrentHashMap<Player, PlayerData>();
        this.permissionUsers = new ConcurrentHashMap<Player, PermissionAttachment>();
    }

    public boolean initialise() {
        this.groupHandler = this.plugin.getGroupHandler();

        // Get the main scoreboard. We use it for tags.
        Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getMainScoreboard();

        // Loop through all existing teams.
        for (Team team : scoreboard.getTeams()) {
            // Loop through all members of this team.
            for (OfflinePlayer player : team.getPlayers()) {
                // Remove the player from this team.
                team.removePlayer(player);
            }
        }

        return true;
    }

    public void addPlayer(Player p) {
        if (!userExists(p)) {
            addUser(p, true);
        } else {
        	int id = getUserIdByUUID(p.getUniqueId().toString());
            addPlayer(p, getAccessFromDB(p), this.groupHandler.getGroupIDFromUserId(id), id);
        }

        // Adds UUID for new users.
        // Updates name for existing users.
        updatePlayer(p);
    }

    public void cleanup() {
        if (!(this.onlineUsers.isEmpty())) {
            try {
                for (Player p : this.onlineUsers.keySet()) {
                    SavedObject.save(this.onlineUsers.get(p), new File(this.plugin.getDataFolder() + "/players/", p.getUniqueId().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.onlineUsers.clear();
    }

    public void delPlayer(Player p) {
        updatePlayer(p, 1);
        if (!(this.onlineUsers.isEmpty())) {
            try {
                SavedObject.save(this.onlineUsers.get(p), new File(this.plugin.getDataFolder() + "/players/", p.getUniqueId().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.onlineUsers.remove(p);
    }

    public void addRegPlayer(Player player) {
        if (!this.regUsers.contains(player)) {
            this.regUsers.add(player);
        }
    }

    public void removeRegPlayer(Player player) {
        if (this.regUsers.contains(player)) {
            this.regUsers.remove(player);
        }
    }
    
    /**
     * Because mojang will allow name changing soon,
     * this function will then update name in database if its different.
     * That means rest of the plugin will work as before.
     * 
     * If name has changed its inserted into name_history table.
     * @param p
     */
    public void updatePlayer(Player p)
    {
    	// Update UUID in db (in case it's not set)
    	//updatePlayerUUID(p);
    	
    	// Try fetching the current bank amount to verify that the row exists on the user.
    	this.plugin.getBankHandler().getAmount(p.getName());
    	
    	// Fetch current nick from database.
    	String dbName = this.sqlHandler.getColumn("SELECT `name` FROM `Minecraftno`.`users` WHERE `uuid` = '" + p.getUniqueId().toString() + "'");
    	
    	if (dbName == null)
    		return; // User not registered, no need to update a namechange as player do not exist in system.
    	
    	// Check if players current name is different to what we have in the database.
    	if (!(p.getName().equalsIgnoreCase(dbName))) {        	
    	    // Do name change in database.
        	this.sqlHandler.update("UPDATE `Minecraftno`.`users` SET `name` = '" + p.getName() + "' WHERE `uuid` = '" + p.getUniqueId().toString() + "'");
        	
        	// Put this name change in own table to keep a record.
        	this.sqlHandler.update("INSERT INTO `Minecraftno`.`name_history`(uuid, old_name, new_name)" +
        			"VALUES('"+p.getUniqueId().toString()+"', '"+dbName+"', '"+p.getName()+"')");
    	}
    	
    	// Rename PlayerData file if it still exist with nick to UUID.
    	File search = new File(plugin.getDataFolder() + "/players/", dbName);
    	if (search.exists()) {
    		search.renameTo(new File(plugin.getDataFolder() + "/players/", p.getUniqueId().toString()));
    	}
    }
    
    /**
     * Because mojang will allow name changing soon,
     * this function will then update name in database if its different.
     * That means rest of the plugin will work as before.
     * @param p
     */
    public void updatePlayerUUID(Player p)
    {
    	// method handled in no.hardwork
    }
    
    /**
     * Uses UUID of player to check if user exists in users table.
     * @param p Player
     * @return boolean
     */
    public boolean userExists(Player p)
    {
    	return null != this.sqlHandler.getColumn("SELECT id FROM Minecraftno.users WHERE uuid='" + p.getUniqueId() + "' OR (name='" + p.getName() + "' AND uuid='ingen')");
    }

    public boolean isRegPlayer(Player player) {
        return this.regUsers.contains(player);
    }

    public List<Player> getReg() {
        return this.regUsers;
    }

    public void addPlayer(Player p, int accessLevel, int groupId, int userId) {
        PlayerData pd = new PlayerData(accessLevel, groupId);
        pd.setUserId(userId);
        File dataFile = new File(this.plugin.getDataFolder() + "/players/", p.getUniqueId().toString());
        if (dataFile.exists()) {
            try {
                this.onlineUsers.put(p, (PlayerData) SavedObject.load(dataFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.onlineUsers.get(p).setAccessLevel(accessLevel);
            this.onlineUsers.get(p).setGroupId(groupId);
            this.onlineUsers.get(p).setUserId(userId);
            this.onlineUsers.get(p).setCachedUUID(p.getUniqueId().toString());
        } else {
            this.onlineUsers.put(p, pd);
        }
    }

    public void addPlayer(Player p, int accessLevel) {
        addPlayer(p, accessLevel, this.groupHandler.getGroupIDFromName(p.getName()), getUserId(p));
    }

    /**
     * Returns the map of users. The returned map is (immutable).
     *
     * @return map of online users
     */
    public Map<Player, PlayerData> getOnlineUsers() {
        return Collections.unmodifiableMap(this.onlineUsers);
    }

    public PlayerData getPlayerData(Player player) {
        return this.onlineUsers.get(player);
    }

    public PlayerData getPlayerData(String playerName) {
        Player player = this.plugin.getServer().getPlayer(playerName);
        return (player != null) ? this.onlineUsers.get(player) : null;
    }

    /**
     * Henter ut accesslevel til brukeren
     *
     * @param player (Player) Spilleren man skal hente ut accesslevel til
     *
     * @return (int) accesslevel
     */
    public int getAccess(Player player) {
        PlayerData pd = this.onlineUsers.get(player);
        if (pd != null) {
            return pd.getAccessLevel();
        } else {
            return getAccessFromDB(player);
        }
    }

    public int getAccess(String player) {
        return getAccessFromDB(player);
    }

    /**
     * Henter ut accesslevel til brukeren
     *
     * @param player (Player) Spilleren man skal hente ut accesslevel til
     *
     * @return (int) accesslevel
     */
    private final int getAccessFromDB(Player player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int accesslevel = 0;
        int resultsCounter = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `accesslevel` FROM access WHERE `userID` = '" + getUserId(player) + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                accesslevel = rs.getInt("accesslevel");
            }
            if (resultsCounter == 0) {
                sqlHandler.update("REPLACE INTO `access` (`userID`, `accesslevel`) VALUES (" + getUserId(player) + ", " + accesslevel + ")");
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
                    //conn.close();
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under lukking)", ex);
            }
        }
        return accesslevel;
    }

    private final int getAccessFromDB(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int accesslevel = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `accesslevel` FROM access WHERE `userID` = " + getUserId(player));
            rs = ps.executeQuery();

            while (rs.next()) {
                accesslevel = rs.getInt("accesslevel");
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
                    //conn.close();
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under lukking)", ex);
            }
        }
        return accesslevel;
    }

    /**
     * Henter ut brukernavnet fra databasen, slik at man får en
     * autoComplete-funksjon lik playerMatch.
     *
     * @param playerName   (String) Spillernavnet som det skal autocompletes på
     * @param autocomplete (boolean) Om LIKE eller = skal brukes i spørringen
     *
     * @return String Navnet, eller tom streng om det ikke finnes.
     */
    private final String getUsernameFromDB(String playerName, boolean autocomplete) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String userName = "";
        String grab = "";
        if (autocomplete) {
            grab = "LIKE '%" + playerName + "%'";
        } else {
            grab = "= '" + playerName + "'";
        }
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT name FROM Minecraftno.users WHERE name " + grab);
            rs = ps.executeQuery();
            while (rs.next()) {
                userName = rs.getString("name");
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
                    //conn.close();
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception (under lukking)", ex);
            }
        }
        return userName;
    }

    /**
     * Henter ut brukernavnet fra databasen, slik at man får en
     * autoComplete-funksjon lik playerMatch.
     *
     * @param playerName (String) Spillernavnet som det skal autocompletes på
     *
     * @return String Navnet, eller tom streng om det ikke finnes.
     */
    public String getUsernameFromDBAutoComplete(String playerName) {
        return getUsernameFromDB(playerName, true);
    }

    public String getUsernameFromDB(String playerName) {
        return getUsernameFromDB(playerName, false);
    }

    public static String getPrefix(int rankId) {
        switch (rankId) {
            case 0:
                return "Gjest";
            case 1:
                return "";
            case 2:
                return "";
            case 3:
                return "Vakt";
            case 4:
                return "Stab";
            case 5:
                return "Tech";
        }

        return null;
    }

    public static ChatColor getPrefixColor(int rankId) {
        switch (rankId) {
            case 0:
                return ChatColor.GRAY;
            case 1:
                return ChatColor.WHITE;
            case 2:
                return ChatColor.DARK_AQUA;
            case 3:
                return ChatColor.BLUE;
            case 4:
                return ChatColor.GOLD;
            case 5:
                return ChatColor.GREEN;
        }

        return null;
    }

    public void tagPlayer(Player player) {
        int rankId = this.getAccess(player);

        ChatColor color = getPrefixColor(rankId);
        String tag = getPrefix(rankId);

        if (player.getAddress().getAddress().getCanonicalHostName().endsWith(".gathering.org")) {
            tag = "TG" + (tag.length() > 0 ? "-" + tag : "");
        }

        player.setPlayerListName(color + (player.getName().length() > 14 ? player.getName().substring(0, 14) : player.getName()));
        player.setDisplayName(color + (tag.length() > 0 ? "[" + tag + "] " : "") + player.getName());

        // Get the main scoreboard. We use it for tags.
        Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getMainScoreboard();

        // Get the player's current team, if any.
        Team team = scoreboard.getPlayerTeam(player);

        // Remove the player from the current team.
        if (team != null) {
            team.removePlayer(player);
        }

        // Do we have a tag to use (ie. do we want to put the player in a group)?
        if (tag.length() > 0) {
            // Get the target team.
            team = scoreboard.getTeam(tag);

            // Does the team exist?
            if (team == null) {
                // No. Create it and set the prefix for it.
                team = scoreboard.registerNewTeam(tag);
                team.setPrefix(color + "[" + tag + "] ");
            }

            // Add the player to this team.
            team.addPlayer(player);
        }
    }

    public String getPlayerDisplayName(String player) {
        int rankId = this.getAccess(player);

        ChatColor color = getPrefixColor(rankId);
        String tag = getPrefix(rankId);

        return color + (tag.length() > 0 ? "[" + tag + "] " : "") + player;
    }

    public String getIrcToGamePrefix(User user) {
        // Fetch the user's in-game access level.
        // No mode on IRC = max access level of 1 (user).
        // Mode on IRC = max access level of 5 (tech).
        int accessLevel = Math.min(this.getAccess(user.getNick()), (user.getPrefix().length() == 0 ? 1 : 5));

        // Can we trust that this is who the nick says it is?
        if (accessLevel > 1) {
            // Yes. Check if the user is in-game, and use the scoreboard prefix, if any.
            Player player = this.plugin.getServer().getPlayerExact(user.getNick());

            if (player != null) {
                Team team = this.plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);

                if (team != null) {
                    return team.getPrefix();
                }
            }
        }

        // The user is not in-game right now. Construct the tag based on access level.
        String tag = this.getPrefix(accessLevel);

        return this.getPrefixColor(accessLevel) + (tag.length() == 0 ? "" : "[" + tag + "] ");
    }

    public String getIRCPrefix(int rankId) {
        String tag = this.getPrefix(rankId);

        return this.getIRCPrefixColor(rankId) + (tag.length() == 0 ? "" : "[" + tag + "] ");
    }

    public String getIRCPrefixColor(int groupId) {
        switch (groupId) {
            case 0:
                return Colors.DARK_GRAY;
            case 2:
                return "";
            case 3:
                return Colors.BLUE;
            case 4:
                return Colors.OLIVE;
            case 5:
                return Colors.DARK_GREEN;
        }

        return "";
    }

    /**
     * Legger brukeren til i databasen
     *
     * @param player    (Player) spilleren som skal legges til
     * @param firstTime (boolean) Er det første gang spilleren logger inn?
     *
     * @return true/false alt ettersom om ting går bra
     */
    public boolean addUser(Player player, boolean firstTime) {
        int accessLevel = 1;
        if (firstTime) {
            if (sqlHandler.update("REPLACE INTO Minecraftno.users (`name`, `uuid`) VALUES ('" + player.getName() + "', '"+player.getUniqueId().toString()+"')")) {
                if (sqlHandler.update("REPLACE INTO `access` (`userID`, `accesslevel`) VALUES (" + getUserId(player) + ", " + accessLevel + ")")) {
                    //delPlayer(player);
                    updatePlayer(player, 0);
                    addPlayer(player);
                    return true;
                }
            }
        } else {
            addPlayer(player, accessLevel);
            return changeAccessLevel(player, accessLevel);
        }
        return false;
    }

    /**
     * Setter brukeren til gjest
     *
     * @param name (String) Navnet på spilleren som skal få gjestestatus
     *
     * @return true/false alt ettersom om ting går bra
     */
    public boolean delUser(String name) {
        return changeAccessLevel(name, 0);
    }

    public boolean delUser(Player name) {
        return changeAccessLevel(name, 0);
    }

    /**
     * Setter brukerens accessLevel
     *
     * @param name        (String) Navnet på spilleren som skal få endret accessLevel
     * @param accessLevel Access level spilleren skal få
     *
     * @return true/false alt ettersom om ting går bra
     */

    public boolean changeAccessLevel(String name, int accessLevel) {
        assert (accessLevel < 5 && accessLevel >= 0);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.plugin.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE access SET accesslevel = " + accessLevel + " WHERE userID = " + getUserId(name) + "");
            stmt.close();
            Player player = plugin.getServer().getPlayer(name);
            if (player != null) {
                this.onlineUsers.get(player).setAccessLevel(accessLevel);
                tagPlayer(player);
            }
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return false;
        }
    }

    public boolean changeAccessLevel(Player player, int accessLevel) {
        assert (accessLevel < 5 && accessLevel >= 0);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.plugin.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE access SET accesslevel = " + accessLevel + " WHERE userID = " + getUserId(player) + "");
            stmt.close();
            if (player != null) {
                this.onlineUsers.get(player).setAccessLevel(accessLevel);
                tagPlayer(player);
            }
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return false;
        }
    }

    /**
     * Banner brukeren
     *
     * @param name   (String) Navnet på spilleren som bli bannet
     * @param reason (String) Grunnen til at spilleren blir bannet
     *
     * @return true/false alt ettersom om ting går bra
     */

    public boolean banUser(String name, String reason, Player banner) {
        return banUser(name, reason, banner, false);
    }

    public boolean banUser(Player name, String reason, Player banner) {
        return banUser(name, reason, banner, false);
    }

    public boolean banUser(String name, String reason, String banner) {
        return banUser(name, reason, banner, false);
    }

    public boolean banUser(Player name, String reason, String banner) {
        return banUser(name, reason, banner, false);
    }

    /**
     * ukesbanner brukeren
     *
     * @param name   (String) Navnet på spilleren som bli bannet
     * @param reason (String) Grunnen til at spilleren blir bannet
     *
     * @return true/false alt ettersom om ting går bra
     */

    public boolean WeekbanUser(String name, String reason, Player banner) {
        return banUser(name, reason, banner, true);
    }

    public boolean WeekbanUser(Player name, String reason, Player banner) {
        return banUser(name, reason, banner, true);
    }

    public boolean WeekbanUser(String name, String reason, String banner) {
        return banUser(name, reason, banner, true);
    }

    public boolean WeekbanUser(Player name, String reason, String banner) {
        return banUser(name, reason, banner, true);
    }

    /**
     * Banner brukeren med ukesban
     *
     * @param name    (String) Navnet på spilleren som bli bannet
     * @param reason  (String) Grunnen til at spilleren blir bannet
     * @param weekBan (Boolean) true/false om det er ukesban
     *
     * @return true/false alt ettersom om ting går bra
     */
    private final boolean banUser(String name, String reason, Player banner, boolean weekBan) {
        if (weekBan) {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 0, 1, UNIX_TIMESTAMP())");
        } else {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 1, 0, UNIX_TIMESTAMP())");
        }
    }

    private final boolean banUser(Player player, String reason, Player banner, boolean weekBan) {
        if (weekBan) {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(player) + ", '" + reason + "', " + getUserId(banner) + ", 0, 1, UNIX_TIMESTAMP())");
        } else {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(player) + ", '" + reason + "', " + getUserId(banner) + ", 1, 0, UNIX_TIMESTAMP())");
        }
    }

    private final boolean banUser(String name, String reason, String banner, boolean weekBan) {
        if (weekBan) {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 0, 1, UNIX_TIMESTAMP())");
        } else {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 1, 0, UNIX_TIMESTAMP())");
        }
    }

    private final boolean banUser(Player name, String reason, String banner, boolean weekBan) {
        if (weekBan) {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 0, 1, UNIX_TIMESTAMP())");
        } else {
            return this.sqlHandler.update("REPLACE INTO Minecraftno.bans (userID, reason, bannerID, permban, weekban, time) VALUES (" + getUserId(name) + ", '" + reason + "', " + getUserId(banner) + ", 1, 0, UNIX_TIMESTAMP())");
        }
    }

    /**
     * Sjekker om ban table exist
     *
     * @param name (String) Navnet på spilleren som skal sjekkes.
     *
     * @return true hvis bannet, false ellers.
     */
    public boolean BanCollumExist(String name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name));
        return result != null && !result.isEmpty();
    }

    public boolean BanCollumExist(Player name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name));
        return result != null && !result.isEmpty();
    }

    /**
     * Unbanner brukeren
     *
     * @param name (String) Navnet på spilleren som bli unbannet
     *
     * @return true/false alt ettersom om ting går bra
     */
    public boolean unBanUser(String name) {
        return unBanUser(name, false);
    }

    /**
     * Unbanner brukeren som hadde ukesban
     *
     * @param name    (String) Navnet på spilleren som bli unbannet
     * @param weekBan (Boolean) True/False om det er ukes ban
     *
     * @return true/false alt ettersom om ting går bra
     */
    public boolean unBanUser(String name, boolean weekBan) {
        if (weekBan) {
            this.sqlHandler.update("UPDATE Minecraftno.bans SET weekban = '2' WHERE userID = " + getUserId(name));
        }
        this.sqlHandler.update("UPDATE Minecraftno.bans SET permban = '2' WHERE userID = " + getUserId(name));
        return true;
    }

    /**
     * Sjekker om brukeren er bannet
     *
     * @param name (String) Navnet på spilleren som skal sjekkes.
     *
     * @return true hvis bannet, false ellers.
     */
    public boolean isBanned(Player name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND permban = '1'");
        return result != null && !result.isEmpty();
    }

    public boolean isBanned(String name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND permban = '1'");
        return result != null && !result.isEmpty();
    }

    /**
     * Sjekker om brukeren er ukesbannet
     *
     * @param name (String) Navnet på spilleren som skal sjekkes.
     *
     * @return true hvis ukesbannet, false ellers.
     */
    public boolean isWeekBanned(Player name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND weekban = 1");
        return result != null && !result.isEmpty();
    }

    public boolean hadWeekBanned(Player name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND weekban = 2");
        return result != null && !result.isEmpty();
    }

    public boolean isWeekBanned(String name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND weekban = 1");
        return result != null && !result.isEmpty();
    }

    public boolean hadWeekBanned(String name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE userID = " + getUserId(name) + " AND weekban = 2");
        return result != null && !result.isEmpty();
    }

    /**
     * Sjekker om brukeren kan bli unbannet fra ukes ban
     *
     * @param name (String) Navnet på spilleren som skal sjekkes.
     *
     * @return true hvis ban skal fjernes, false ellers.
     */

    public boolean compareWeekBan(String name) {
        String result = this.sqlHandler.getColumn("SELECT * FROM Minecraftno.bans WHERE time < UNIX_TIMESTAMP(DATE_SUB(NOW() , INTERVAL 7 DAY)) AND userID = " + getUserId(name));
        return result != null && !result.isEmpty();
    }

    /**
     * Gets the id for the player (checks users that are online first)
     *
     * @param player The player to search for
     *
     * @return the player's id, or -1 if the player does not exist.
     */
    public int getUserId(Player player) {
        PlayerData pd = this.onlineUsers.get(player);
        int id = -1;
        if (pd != null) {
            id = pd.getUserId();
        } else {
            id = getUserId(player.getName());
        }
        return id;
    }

    /**
     * Gets the id for the playerName
     *
     * @param playerName The player name to search for
     *
     * @return the player's id, or -1 if the player does not exist.
     */
    public int getUserId(String playerName) {
        int userId = -1;
        for (Entry<Player, PlayerData> entry : getOnlineUsers().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(playerName)) {
                return entry.getValue().getUserId();
            }
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT id FROM Minecraftno.users WHERE name = ?");
            ps.setString(1, playerName);
            rs = ps.executeQuery();

            while (rs.next()) {
                userId = rs.getInt(1);
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
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
        return userId;
    }

    /**
     * Gets the id for the uuid
     *
     * @param uuid Unique User ID of player.
     *
     * @return the player's id, or -1 if the player does not exist.
     */
    public int getUserIdByUUID(String uuid) {
        int userId = -1;
        for (Entry<Player, PlayerData> entry : getOnlineUsers().entrySet()) {
            if (entry.getKey().getUniqueId().toString().equalsIgnoreCase(uuid)) {
                return entry.getValue().getUserId();
            }
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT id FROM Minecraftno.users WHERE uuid = ?");
            ps.setString(1, uuid);
            rs = ps.executeQuery();

            while (rs.next()) {
                userId = rs.getInt(1);
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
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
        return userId;
    }

    /**
     * Gets the username associated with the given id
     *
     * @param id The user id to check
     *
     * @return The username found in the database, or null if it doesn't exist
     */
    public String getNameFromId(int id) {
        String userName = null;
        for (Entry<Player, PlayerData> entry : getOnlineUsers().entrySet()) {
            if (entry.getValue().getUserId() == id) {
                return entry.getKey().getName();
            }
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT name FROM Minecraftno.users WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                userName = rs.getString(1);
            }
        } catch (SQLException e) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL-feil i: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return userName;
    }

    public BanData getBanData(String playerName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BanData ret = null;
        int playerid = getUserId(playerName);
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT * FROM Minecraftno.bans WHERE userID = ?");
            ps.setInt(1, playerid);
            rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    String reason = "";
                    if (rs.getString(1) != null) {
                        reason = rs.getString(2);
                    }
                    int banner = rs.getInt(3);
                    int permban = rs.getInt(4);
                    int weekBan = rs.getInt(5);
                    if (rs.wasNull()) {
                        weekBan = 0;
                    }
                    long time = rs.getLong(6);
                    ret = new BanData(this.plugin, playerid, reason, banner, time, permban, weekBan);
                }
            } else {
                ret = new BanData(this.plugin, 0, "", 0, 0, 0, 0);
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
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
        return ret;
    }

    public String[] getUsersSortOnAccess(int accessLevel, Player[] users) {
        if (users.length > 0) {
            int counter = 0;
            String[] usersSorted = new String[countUsersOnAccess(accessLevel, users)];
            for (int i = 0; i < users.length; i++) {
                int userAccessLevel = getAccess(users[i]);
                if (userAccessLevel == accessLevel) {
                    usersSorted[counter] = UserHandler.getPrefixColor(userAccessLevel) + users[i].getName();
                    counter++;
                }
            }
            NameComparator c = new NameComparator();
            Arrays.sort(usersSorted, c);
            counter = 0;
            return usersSorted;
        } else {
            return null;
        }
    }

    public String[] getUsersSortOnIRCAccess(int accessLevel, Player[] users) {
        if (users.length > 0) {
            int counter = 0;
            String[] usersSorted = new String[countUsersOnAccess(accessLevel, users)];
            for (int i = 0; i < users.length; i++) {
                int userAccessLevel = getAccess(users[i]);
                if (userAccessLevel == accessLevel) {
                    usersSorted[counter] = getIRCPrefix(userAccessLevel) + users[i].getName();
                    counter++;
                }
            }
            NameComparator c = new NameComparator();
            Arrays.sort(usersSorted, c);
            counter = 0;
            return usersSorted;
        } else {
            return null;
        }
    }

    public int countUsersOnAccess(int accessLevel, Player[] users) {
        int counter = 0;
        for (int i = 0; i < users.length; i++) {
            if (getAccess(users[i]) == accessLevel) {
                counter++;
            }
        }
        return counter;
    }

    public void reloadUsers() {
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            addPlayer(p);
            tagPlayer(p);
        }
    }

    public void removePermissions(Player player) {
        if (player == null || !this.plugin.getWeBridge().isEnabled()) {
            return;
        }
        this.permissionUsers.remove(player);
    }

    public long getSlowMode(Player player) {
        return this.onlineUsers.get(player).getSlowMode();
    }

    public void setSlowMode(Player player, long slowmode) {
        this.onlineUsers.get(player).setSlowMode(slowmode);
    }

    public boolean getMute(Player player) {
        return this.onlineUsers.get(player).getMute();
    }

    public void setMute(Player player, boolean Mute) {
        this.onlineUsers.get(player).setMute(Mute);
    }

    public boolean getFreeze(Player player) {
        return this.onlineUsers.get(player).getFreeze();
    }

    public void setFreeze(Player player, boolean Freeze) {
        this.onlineUsers.get(player).setFreeze(Freeze);
    }

    public boolean getInvisible(Player player) {
        return this.onlineUsers.get(player).getInvisible();
    }

    public void setInvisible(Player player, boolean Invisible) {
        this.onlineUsers.get(player).setInvisible(Invisible);
    }

    public boolean getGroupChatBind(Player player) {
        return this.onlineUsers.get(player).getGroupChatBind();
    }

    public void setGroupChatBind(Player player, boolean groupchatbind) {
        this.onlineUsers.get(player).setGroupChatBind(groupchatbind);
    }

    public boolean hasAdminChatActivated(Player player) {
        return !this.onlineUsers.get(player).hasAdminChatDeactivated();
    }

    public void setAdminChatDeactivated(Player player, boolean adminchat) {
        this.onlineUsers.get(player).setAdminChatDeactivated(adminchat);
    }

    public boolean getTradeChat(Player player) {
        return this.onlineUsers.get(player).getTradeChat();
    }

    public void setTradeChat(Player player, boolean tradechat) {
        this.onlineUsers.get(player).setTradeChat(tradechat);
    }

    public boolean getHovedChat(Player player) {
        return this.onlineUsers.get(player).getHovedChat();
    }

    public void setHovedChat(Player player, boolean hovedchat) {
        this.onlineUsers.get(player).setHovedChat(hovedchat);
    }

    public boolean getAnnonseringer(Player player) {
        return this.onlineUsers.get(player).getAnnonseringer();
    }

    public void setAnnonseringer(Player player, boolean annonseringer) {
        this.onlineUsers.get(player).setAnnonseringer(annonseringer);
    }

    public boolean getirc(Player player) {
        return this.onlineUsers.get(player).getirc();
    }

    public void setirc(Player player, boolean irc) {
        this.onlineUsers.get(player).setirc(irc);
    }

    public String getLastMsgSend(Player player) {
        return this.onlineUsers.get(player).getlastmsgsend();
    }

    public void setLastMsgSend(Player player, String lastmsgsend) {
        this.onlineUsers.get(player).setlastmsgsend(lastmsgsend);
    }

    public String getLastMsgGet(Player player) {
        return this.onlineUsers.get(player).getlastmsgget();
    }

    public void setLastSendtMsg(Player player, String lastsendtmsg) {
        this.onlineUsers.get(player).setlastsendtmsg(lastsendtmsg);
    }

    public String getLastSendtMsg(Player player) {
        return this.onlineUsers.get(player).getlastsendtmsg();
    }

    public void setLastMsgGet(Player player, String lastmsgget) {
        this.onlineUsers.get(player).setlastmsgget(lastmsgget);
    }

    public String getLastMsgLock(Player player) {
        return this.onlineUsers.get(player).getlastmsgLock();
    }

    public void setLastMsgLock(Player player, String lastmsgLock) {
        this.onlineUsers.get(player).setlastmsgLock(lastmsgLock);
    }

    public String getPlayerName(Player player) {
        return this.onlineUsers.get(player).getPlayerName();
    }

    public void setPlayerName(Player player, String PlayerName) {
        this.onlineUsers.get(player).setPlayerName(PlayerName);
    }

    public Location getTeleportBackLocation(Player player) {
        return this.onlineUsers.get(player).getTeleportBackLocation();
    }

    public void setTeleportBackLocation(Player player, Location loc) {
        this.onlineUsers.get(player).setTeleportBackLocation(loc);
    }

    public String getToolData(Player player) {
        return this.onlineUsers.get(player).getToolData();
    }

    public void setToolData(Player player, String data) {
        this.onlineUsers.get(player).setToolData(data);
    }

    public void setToolLocation1(Player player, Location loc) {
        this.onlineUsers.get(player).setToolLocation1(loc);
    }

    public void setToolLocation2(Player player, Location loc) {
        this.onlineUsers.get(player).setToolLocation2(loc);
    }

    public Location getToolLocation1(Player player) {
        return this.onlineUsers.get(player).getToolLocation1();
    }

    public Location getToolLocation2(Player player) {
        return this.onlineUsers.get(player).getToolLocation2();
    }

    public long getLastMessageTime(Player player) {
        return this.onlineUsers.get(player).getLastMessageTime();
    }

    public void setLastMessageTime(Player player, long LastMessageTime) {
        this.onlineUsers.get(player).setLastMessageTime(LastMessageTime);
    }

    public int getAmountRepeatString(Player player) {
        return this.onlineUsers.get(player).getRepeatString();
    }

    public void setAmountRepeatString(Player player, int repeatString) {
        this.onlineUsers.get(player).setRepeatString(repeatString);
    }

    public boolean updatePlayer(Player player, int i) {
        if (i == 0) {
            if (Minecraftno.getPlayerIP(player) != null) {
                return this.sqlHandler.update("INSERT into userlog (`name`, `ip`, `type`, `time`) VALUES(" + getUserId(player) + ", '" + Minecraftno.getPlayerIP(player) + "', 0, UNIX_TIMESTAMP());");
            } else if (i == 1) {
                return this.sqlHandler.update("INSERT into userlog (`name`, `ip`, `type`, `time`) VALUES(" + getUserId(player) + ", '" + Minecraftno.getPlayerIP(player) + "', 1, UNIX_TIMESTAMP());");
            }
        }
        return false;
    }

    public static class NameComparator implements Comparator<String> {
        public int compare(String strA, String strB) {
            return strA.compareToIgnoreCase(strB);
        }
    }
}
