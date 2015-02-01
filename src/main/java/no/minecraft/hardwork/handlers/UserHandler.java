package no.minecraft.hardwork.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.User;
import no.minecraft.hardwork.database.DataConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class UserHandler implements Handler, DataConsumer {
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

    private File homesFile;
    private YamlConfiguration homes;

    private File workConfigFile;
    private YamlConfiguration workInventories;

    private File backConfigFile;
    private YamlConfiguration backLocations;
    
    public UserHandler(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @Override
    public void onEnable() {
        this.homesFile = new File(this.hardwork.getPlugin().getDataFolder(), "homes.yml");
        this.workConfigFile = new File(this.hardwork.getPlugin().getDataFolder(), "workInvs.yml");
        this.workConfigFile = new File(this.hardwork.getPlugin().getDataFolder(), "backLocations.yml");

        this.loadHomes();
        this.loadWorks();
        this.loadBackLocations();

        Scoreboard scoreboard = this.hardwork.getPlugin().getServer().getScoreboardManager().getMainScoreboard();

        for (Team team : scoreboard.getTeams()) {
            for (OfflinePlayer player : team.getPlayers()) {
                team.removePlayer(player);
            }
        }
    }

    @Override
    public void onDisable() {
        this.saveHomes();
    }

    @Override
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

        this.hardwork.getDatabase().getConnection();

        try {
            this.queryUserExists.setString(1, uuid.toString());
            this.queryUserExists.setString(2, name);

            ResultSet result = this.queryUserExists.executeQuery();

            exists = result.next();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while checking for user existence!");
            exception.printStackTrace();
        }

        try {
            this.queryUserExists.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        return exists;
    }

    public User createUser(UUID uuid, String name, int accessLevel) {
        User user = null;

        Connection conn = this.hardwork.getDatabase().getConnection();

        try {
            conn.setAutoCommit(false);

            this.queryInsertUser.setString(1, uuid.toString());
            this.queryInsertUser.setString(2, name);

            int userId = this.queryInsertUser.executeUpdate();

            if (userId <= 0)
                throw new SQLException("Unexpected result from RETURN_GENERATED_KEYS query!");

            this.queryInsertAccess.setInt(1, userId);
            this.queryInsertAccess.setInt(2, accessLevel);

            if (this.queryInsertAccess.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            conn.commit();

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
            this.queryInsertUser.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        try {
            this.queryInsertAccess.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException exception) {
            this.hardwork.getPlugin().getLogger().severe("SQLException while enabling auto commit!");
            exception.printStackTrace();
        }

        return user;
    }

    public User updateUser(int id, UUID uuid, String name, int accessLevel) {
        User user = null;

        Connection conn = this.hardwork.getDatabase().getConnection();

        try {
            conn.setAutoCommit(false);

            this.queryUpdateUser.setString(1, uuid.toString());
            this.queryUpdateUser.setString(2, name);
            this.queryUpdateUser.setInt(3, id);

            if (this.queryUpdateUser.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            this.queryUpdateAccess.setInt(1, accessLevel);
            this.queryUpdateAccess.setInt(2, id);

            if (this.queryUpdateAccess.executeUpdate() != 1)
                throw new SQLException("Unexpected number of affected rows!");

            conn.commit();

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

        try {
            this.queryUpdateUser.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        try {
            this.queryUpdateAccess.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
            exception.printStackTrace();
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while enabling auto commit!");
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

        this.hardwork.getDatabase().getConnection();

        try {
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
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while fetching user!");
            exception.printStackTrace();
        }

        try {
            this.queryUserId.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
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

        this.hardwork.getDatabase().getConnection();

        try {
            this.queryUserUuid.setString(1, uuid.toString());

            ResultSet result = this.queryUserUuid.executeQuery();
            if (result.next())
                user = new User(
                    result.getInt("id"),
                    UUID.fromString(result.getString("uuid")),
                    result.getString("name"),
                    result.getInt("accesslevel")
                );
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while fetching user!");
            exception.printStackTrace();
        }

        try {
            this.queryUserUuid.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
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

        this.hardwork.getDatabase().getConnection();

        try {
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
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while fetching user!");
            exception.printStackTrace();
        }

        try {
            this.queryUserName.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
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

        this.hardwork.getDatabase().getConnection();

        try {
            this.queryLastLogin.setInt(1, user.getId());

            ResultSet result = this.queryLastLogin.executeQuery();

            if (result.next())
                date = new Date((long) result.getInt("time") * 1000);
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while fetching last user login timestamp!");
            exception.printStackTrace();
        }

        try {
            this.queryLastLogin.clearParameters();
        } catch (SQLException exception) {
            this.hardwork.getLogger().warning("SQLException while clearing query parameters!");
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
    
    /*--------------------------------------------------------------*/
    /* Home methods                                                 */
    /*--------------------------------------------------------------*/

    public void loadHomes() {
        this.homes = YamlConfiguration.loadConfiguration(this.homesFile);
    }

    public void saveHomes() {
        try {
            this.homes.save(this.homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHome(User user, Location location) {
        this.homes.set(Integer.toString(user.getId()) + ".world", location.getWorld().getName());
        this.homes.set(Integer.toString(user.getId()) + ".x", location.getBlockX());
        this.homes.set(Integer.toString(user.getId()) + ".y", location.getBlockY());
        this.homes.set(Integer.toString(user.getId()) + ".z", location.getBlockZ());

        this.saveHomes();
    }

    public Location getHome(User user) {
        if (!this.homes.contains(Integer.toString(user.getId())))
            return null;

        World world = this.hardwork.getPlugin().getServer().getWorld(this.homes.getString(user.getId() + ".world"));

        if (world == null)
            return null;

        return new Location(
            world,
            this.homes.getInt(user.getId() + ".x"),
            this.homes.getInt(user.getId() + ".y"),
            this.homes.getInt(user.getId() + ".z")
        );
    }

    public void deleteHome(User user) {
        this.homes.set(Integer.toString(user.getId()), null);

        this.saveHomes();
    }
    
    /*--------------------------------------------------------------*/
    /* Work methods                                                 */
    /*--------------------------------------------------------------*/

    public void loadWorks() {
        this.workInventories = YamlConfiguration.loadConfiguration(workConfigFile);
    }
    
    /**
     * Indicates if Player is in work. Based on User.isWorking()
     * 
     * @param player
     * @return boolean
     */
    public boolean isInWork(Player player) {
        return getUser(player.getUniqueId()).isWorking();
    }
    
    /**
     * Saves player's inventory to disk and give work items.
     * 
     * @param player
     * @return false on failure.
     */
    public boolean saveInventoryForWork(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (isInWork(player) == true)
            return false;
        
        PlayerInventory pinv = player.getInventory();
        
        try {
            workInventories.set("inventory." + uuid, player.getInventory().getContents());
            workInventories.save(workConfigFile);
        } catch (Exception ex) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke lagre inventory.", ex); // TODO: Log with Hardwork.
            return false; // Stop method here.
        }
        
        // Seems that saving of inventory went fine, time to give user his/her items.
        pinv.clear();
        
        User user = getUser(player.getUniqueId());
        
        pinv.setItem(0, new ItemStack(Material.WATCH, 1));
        pinv.setItem(7, new ItemStack(Material.SPONGE, 1));
        
        if (user.getAccessLevel() > 2) {
            pinv.setItem(1, new ItemStack(Material.COMPASS, 1));
            pinv.setItem(2, new ItemStack(Material.STICK, 1));
            pinv.setItem(3, new ItemStack(Material.BOOK, 1));
            pinv.setItem(4, new ItemStack(Material.WOOD_AXE, 1));
            pinv.setItem(5, new ItemStack(Material.SLIME_BALL, 1));
            pinv.setItem(6, new ItemStack(Material.PAPER, 1));
            pinv.setItem(8, new ItemStack(Material.BEDROCK, -1));
            pinv.setItem(9, new ItemStack(Material.WATER, -1));
            pinv.setItem(10, new ItemStack(Material.LAVA, -1));
            pinv.setItem(11, new ItemStack(Material.FIRE, -1));
        }
        
        return true;
    }
    
    /**
     * Loads saved inventory and give's player back it's items.
     * 
     * @param player
     * @return false on failure.
     */
    @SuppressWarnings("unchecked")
    public boolean setSavedInventoryWork(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (isInWork(player) == false)
            return false;
        
        ItemStack[] content = null;
        
        // Try loading inventory from disk.
        try {
            List<ItemStack> list = (List<ItemStack>) workInventories.get("inventory." + uuid);
            content = list.toArray(new ItemStack[0]);
        } catch (Exception ex) {
            Minecraftno.log.log(Level.SEVERE, "[Hardwork] Kunne ikke hente inventory.", ex); // TODO: Log with Hardwork.
            return false;
        }
        
        if (content != null) {
            player.getInventory().clear();
            player.getInventory().setContents(content);
        }
        
        // "remove" inventory from work file.
        workInventories.set("inventory." + uuid, null);
        try {
            workInventories.save(workConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /*--------------------------------------------------------------*/
    /* Back-teleport methods                                        */
    /*--------------------------------------------------------------*/

    public void loadBackLocations() {
        this.backLocations = YamlConfiguration.loadConfiguration(this.backConfigFile);
    }
    
    public void saveBackLocations() {
        try {
            this.backLocations.save(this.backConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Set new back location.
     * 
     * @param user
     * @param loc
     */
    public void setBackLocation(User user, Location loc) {
        String key = Integer.toString(user.getId());
        this.backLocations.set(key + ".x", loc.getBlockX());
        this.backLocations.set(key + ".y", loc.getBlockY());
        this.backLocations.set(key + ".z", loc.getBlockZ());
        this.backLocations.set(key + ".pitch", loc.getPitch());
        this.backLocations.set(key + ".yaw", loc.getYaw());
        this.backLocations.set(key + ".world", loc.getWorld().getName());
        
        this.saveBackLocations();
    }
    
    /**
     * Provides latest Location saved. 
     * 
     * @param user
     * @return
     */
    public Location getBackLocation(User user) {
        String key = Integer.toString(user.getId());
        
        if (this.backLocations.contains(key) == false)
            return null;

        int x = 0;
        int y = 0;
        int z = 0;
        float pitch = 0;
        float yaw = 0;
        World world = this.hardwork.getPlugin().getServer().getWorld("world");
        
        try {
            x = this.backLocations.getInt(key + ".x");
            y = this.backLocations.getInt(key + ".y");
            z = this.backLocations.getInt(key + ".z");
            pitch = Float.parseFloat(this.backLocations.getString(key + ".pitch"));
            yaw = Float.parseFloat(this.backLocations.getString(key + ".yaw"));
            world = this.hardwork.getPlugin().getServer().getWorld(this.backLocations.getString(key + ".world"));
        } catch (NumberFormatException ex) {
            return null;
        }
        
        if (world == null)
            return null;        
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
}
