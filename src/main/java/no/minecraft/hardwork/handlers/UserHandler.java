package no.minecraft.hardwork.handlers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class UserHandler {
    private Hardwork hardwork;

    private PreparedStatement queryUserId;
    private PreparedStatement queryUserUuid;
    private PreparedStatement queryUserName;
    private PreparedStatement queryLastLogin;

    public UserHandler(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    public void prepareStatements() throws SQLException {
        Connection conn = this.hardwork.getDatabase().getConnection();

        this.queryUserId = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.id=?");
        this.queryUserUuid = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.uuid=?");
        this.queryUserName = conn.prepareStatement("SELECT u.id, u.uuid, u.name, a.accesslevel FROM Minecraftno.users AS u, Hardwork.access AS a WHERE u.id=a.userID AND u.name=?");

        this.queryLastLogin = conn.prepareStatement("SELECT ip, time FROM Hardwork.userlog WHERE name=? ORDER BY time DESC LIMIT 1");
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
            if (result.next())
                user = new User(
                    result.getInt("id"),
                    UUID.fromString(result.getString("uuid")),
                    result.getString("name"),
                    result.getInt("accesslevel")
                );

            this.queryUserId.clearParameters();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (user != null) {
            cache.put(new Element(
                id,
                user
            ));

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

            if (result.next())
                user = new User(
                    result.getInt("id"),
                    UUID.fromString(result.getString("uuid")),
                    result.getString("name"),
                    result.getInt("accesslevel")
                );

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
}
