package no.minecraft.hardwork.handlers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class UserHandler {
    private Hardwork hardwork;

    private PreparedStatement queryUserExists;

    private PreparedStatement queryInsertUser;
    private PreparedStatement queryUpdateUser;

    private PreparedStatement queryInsertAccess;
    private PreparedStatement queryUpdateAccess;

    private PreparedStatement queryUserId;
    private PreparedStatement queryUserUuid;
    private PreparedStatement queryUserName;

    private PreparedStatement queryLastLogin;

    public UserHandler(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    public void onEnable() {
        Scoreboard scoreboard = this.hardwork.getPlugin().getServer().getScoreboardManager().getMainScoreboard();

        for (Team team : scoreboard.getTeams()) {
            for (OfflinePlayer player : team.getPlayers()) {
                team.removePlayer(player);
            }
        }
    }

    public void prepareStatements() throws SQLException {
        Connection conn = this.hardwork.getDatabase().getConnection();

        this.queryUserExists = conn.prepareStatement("SELECT 1 FROM Minecraftno.users WHERE uuid=? OR (uuid='ingen' AND name=?)");

        this.queryInsertUser = conn.prepareStatement("INSERT INTO Minecraftno.users (uuid, name) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        this.queryUpdateUser = conn.prepareStatement("UPDATE Minecraftno.users SET uuid=?,name=? WHERE id=?");

        this.queryInsertAccess = conn.prepareStatement("INSERT INTO Hardwork.access (userID, accesslevel) VALUES (?, ?)");
        this.queryUpdateAccess = conn.prepareStatement("UPDATE Hardwork.access SET accesslevel=? WHERE userID=?");

        this.queryUserId = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.id=?");
        this.queryUserUuid = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.uuid=?");
        this.queryUserName = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.name=?");

        this.queryLastLogin = conn.prepareStatement("SELECT ip, time FROM Hardwork.userlog WHERE name=? ORDER BY time DESC LIMIT 1");
    }

    public boolean userExists(UUID uuid, String name) {
        boolean exists = false;

        try {
            this.queryUserExists.setString(1, uuid.toString());
            this.queryUserExists.setString(2, name);

            ResultSet result = this.queryUserExists.executeQuery();

            exists = result.next();

            this.queryUserExists.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().warning("SQLException while checking for user existence!");
            exception.printStackTrace();
        }

        return exists;
    }

    public User createUser(UUID uuid, String name, int accessLevel) {
        User user = null;

        try {
            this.hardwork.getDatabase().getConnection().setAutoCommit(false);

            this.queryInsertUser.setString(1, uuid.toString());
            this.queryInsertUser.setString(2, name);

            int userId = this.queryInsertUser.executeUpdate();

            if (userId <= 0)
                throw new SQLException("Unexpected result from RETURN_GENERATED_KEYS query!");

            this.queryInsertUser.clearParameters();

            this.queryInsertAccess.setInt(1, userId);
            this.queryInsertAccess.setInt(2, accessLevel);

            if (this.queryInsertAccess.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            this.queryInsertAccess.clearParameters();

            user = new User(
                userId,
                uuid,
                name,
                accessLevel
            );
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().warning("SQLException while inserting new user!");
            exception.printStackTrace();
        }

        try {
            this.hardwork.getDatabase().getConnection().setAutoCommit(true);
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().severe("SQLException while enabling auto commit!");
            exception.printStackTrace();
        }

        return user;
    }

    public User updateUser(int id, UUID uuid, String name, int accessLevel) {
        User user = null;

        try {
            this.queryUpdateUser.setString(1, uuid.toString());
            this.queryUpdateUser.setString(2, name);
            this.queryUpdateUser.setInt(3, id);

            if (this.queryUpdateUser.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            this.queryUpdateUser.clearParameters();

            this.queryUpdateAccess.setInt(1, accessLevel);
            this.queryUpdateAccess.setInt(2, id);

            if (this.queryUpdateAccess.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            this.queryUpdateAccess.clearParameters();

            user = new User(
                id,
                uuid,
                name,
                accessLevel
            );
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().warning("SQLException while updating user!");
            exception.printStackTrace();
        }

        return user;
    }

    public User getUser(int id) {
        Cache cache = this.hardwork.getCache("User_id");
        Element cached = cache.get(id);

        if (cached != null && !cached.isExpired())
            return (User) cached.getObjectValue();

        User user = null;

        try {
            this.hardwork.getDatabase().getConnection();

            this.queryUserId.setInt(1, id);

            ResultSet result = this.queryUserId.executeQuery();
            if (result.next()) {
                UUID uuid = null;

                try {
                    uuid = UUID.fromString(result.getString("uuid"));
                } catch (IllegalArgumentException ignored) { }

                user = new User(
                    result.getInt("id"),
                    uuid,
                    result.getString("name"),
                    result.getInt("accesslevel")
                );
            }

            this.queryUserId.clearParameters();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (user != null) {
            cache.put(new Element(
                id,
                user
            ));

            if (user.getUuid() != null)
                this.hardwork.getCache("User_uuid").put(new Element(
                    user.getUuid(),
                    user
                ));

            this.hardwork.getCache("User_name").put(new Element(
                user.getName(),
                user
            ));
        }

        return user;
    }

    public User getUser(UUID uuid) {
        Cache cache = this.hardwork.getCache("User_uuid");
        Element cached = cache.get(uuid);

        if (cached != null && !cached.isExpired())
            return (User) cached.getObjectValue();

        User user = null;

        try {
            this.hardwork.getDatabase().getConnection();

            this.queryUserUuid.setString(1, uuid.toString());

            ResultSet result = this.queryUserUuid.executeQuery();
            if (result.next())
                user = new User(
                    result.getInt("id"),
                    UUID.fromString(result.getString("uuid")),
                    result.getString("name"),
                    result.getInt("accesslevel")
                );

            this.queryUserUuid.clearParameters();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (user != null) {
            cache.put(new Element(
                uuid,
                user
            ));

            this.hardwork.getCache("User_id").put(new Element(
                user.getId(),
                user
            ));

            this.hardwork.getCache("User_name").put(new Element(
                user.getName(),
                user
            ));
        }

        return user;
    }

    public User getUser(String name) {
        Cache cache = this.hardwork.getCache("User_name");
        Element cached = cache.get(name);

        if (cached != null && !cached.isExpired())
            return (User) cached.getObjectValue();

        User user = null;

        try {
            this.hardwork.getDatabase().getConnection();

            this.queryUserName.setString(1, name);

            ResultSet result = this.queryUserName.executeQuery();

            if (result.next()) {
                UUID uuid = null;

                try {
                    uuid = UUID.fromString(result.getString("uuid"));
                } catch (IllegalArgumentException ignored) { }

                user = new User(
                    result.getInt("id"),
                    uuid,
                    result.getString("name"),
                    result.getInt("accesslevel")
                );
            }

            this.queryUserName.clearParameters();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (user != null) {
            cache.put(new Element(
                name,
                user
            ));

            this.hardwork.getCache("User_id").put(new Element(
                user.getId(),
                user
            ));

            if (user.getUuid() != null)
                this.hardwork.getCache("User_uuid").put(new Element(
                    user.getUuid(),
                    user
                ));
        }

        return user;
    }

    public Date getUserLastLogin(User user) {
        Date date = null;

        try {
            this.hardwork.getDatabase().getConnection();

            this.queryLastLogin.setInt(1, user.getId());

            ResultSet result = this.queryLastLogin.executeQuery();

            if (result.next())
                date = new Date((long) result.getInt("time") * 1000);

            this.queryLastLogin.clearParameters();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return date;
    }

    public void clearCachedUser(User user) {
        this.hardwork.getCache("User_id").remove(user.getId());
        this.hardwork.getCache("User_uuid").remove(user.getUuid());
        this.hardwork.getCache("User_name").remove(user.getName());
    }

    public void setDisplayName(User user) {
        Player player = this.hardwork.getPlugin().getServer().getPlayer(user.getUuid());

        if (player == null)
            return;

        ChatColor color = null;
        String prefix = "";

        switch (user.getAccessLevel()) {
            case 0: color = ChatColor.GRAY;      prefix = "Gjest"; break;
            case 1: color = ChatColor.WHITE;                       break;
            case 2: color = ChatColor.DARK_AQUA;                   break;
            case 3: color = ChatColor.BLUE;      prefix = "Vakt";  break;
            case 4: color = ChatColor.GOLD;      prefix = "Stab";  break;
            case 5: color = ChatColor.GREEN;     prefix = "Tech";  break;
        }

        if (player.getAddress().getAddress().getCanonicalHostName().endsWith(".gathering.org")) {
            prefix = "TG" + (prefix.length() > 0 ? "-" + prefix : "");
        }

        player.setDisplayName((color != null ? color : "") + (prefix.length() > 0 ? "[" + prefix + "] " : "") + player.getName() + ChatColor.RESET);

        player.setPlayerListName(color + (player.getName().length() > 14 ? player.getName().substring(0, 14) : player.getName()));

        Scoreboard scoreboard = this.hardwork.getPlugin().getServer().getScoreboardManager().getMainScoreboard();

        Team team = scoreboard.getPlayerTeam(player);

        if (team != null) {
            team.removePlayer(player);
        }

        if (prefix.length() > 0) {
            team = scoreboard.getTeam(prefix);

            if (team == null) {
                team = scoreboard.registerNewTeam(prefix);
                team.setPrefix(color + "[" + prefix + "] ");
            }

            team.addPlayer(player);
        }
    }
}
