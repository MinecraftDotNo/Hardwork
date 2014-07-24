package no.minecraft.Minecraftno;

import no.minecraft.Minecraftno.commands.*;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.handlers.*;
import no.minecraft.Minecraftno.handlers.blocks.BlockHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.blocks.PrivateProtectionHandler;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.NisseHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import no.minecraft.Minecraftno.handlers.scheduler.ServerLogBackup;
import no.minecraft.Minecraftno.handlers.scheduler.SpawnProtection;
import no.minecraft.Minecraftno.handlers.scheduler.WorldBackup;
import no.minecraft.Minecraftno.irc.IRCBot;
import no.minecraft.Minecraftno.listener.*;
import no.minecraft.Minecraftno.sql.MySQLConnectionPool;
import no.minecraft.hardwork.Hardwork;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Step;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Logger;

public class Minecraftno extends JavaPlugin {
    private final Hardwork hardwork = new Hardwork(this);

    // Compatibility with old classes.
    public static Logger log;
    public static Logger debugLog;

    // Timer
    private Timer timer;

    // SQL
    private boolean sqlConnected;
    private final MySQLHandler sqlHandler = new MySQLHandler(this);
    private final LogHandler logHandler = new LogHandler(this);
    private MySQLConnectionPool sqlc;

    // LogHandlersThreads
    private final BlockHandler blockHandler = new BlockHandler(this);

    // Handlers
    private final UserHandler userHandler = new UserHandler(this);
    private final BankHandler bankHandler = new BankHandler(this);
    private final BlockInfoHandler blockinfoHandler = new BlockInfoHandler(this);
    private final WarningHandler warningHandler = new WarningHandler(this);
    private final StartupHandler startupHandler = new StartupHandler(this);
    private final WorldHandler worldHandler = new WorldHandler(this);
    private final GroupLogHandler groupLogHandler = new GroupLogHandler(this);
    private final ChatHandler chathandler = new ChatHandler(this);
    private final GroupHandler groupHandler = new GroupHandler(this);
    private final PrivateProtectionHandler privatePHandler = new PrivateProtectionHandler(this);
    private final SandtakHandler sandtakHandler = new SandtakHandler(this);
    private final NisseHandler nisseHandler = new NisseHandler(this);

    private WEBridge weBridge = new WEBridge(this);

    // IRC
    private final IRCBot bot = new IRCBot(this);

    // Configuration
    protected final ConfigurationServer configuration = new ConfigurationServer(this);

    // Listeners
    private final MinecraftnoEntityListener entityListener = new MinecraftnoEntityListener(this);
    private final MinecraftnoBlockListener blockListener = new MinecraftnoBlockListener(this);
    private final MinecraftnoPlayerListener playerListener = new MinecraftnoPlayerListener(this);
    private final MinecraftnoVehicleListener vehicleListener = new MinecraftnoVehicleListener(this);
    private final MinecraftnoWeatherListener weatherListener = new MinecraftnoWeatherListener(this);
    private final MinecraftnoInventoryListener inventoryListener = new MinecraftnoInventoryListener(this);
    private final MinecraftnoWorldListener worldListener = new MinecraftnoWorldListener(this);
    private final MinecraftnoServerListener serverListener = new MinecraftnoServerListener(this);
    private final MinecraftnoHangingListener hangingListener = new MinecraftnoHangingListener(this);

    public void onEnable() {
        hardwork.onEnable();

        // Use the default Bukkit logger.
        // Previous log handler is no longer needed, as "Overloaded!" messages can be disabled in config.
        Minecraftno.log = this.getLogger();
        Minecraftno.debugLog = this.getLogger();

        this.getLogger().info("Loading native Bukkit configuration...");
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getLogger().info("Loading custom configuration...");
        try {
            this.configuration.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getLogger().info("Enabling Minecraftno plugin...");

        this.getLogger().info(" - Enabling IRC bot...");
        bot.ircBot();

        this.getLogger().info(" - Binding to events...");
        registerEvents();

        this.getLogger().info(" - Connecting to SQL server...");
        if (!sqlConnection()) {
            return;
        }

        this.getLogger().info(" - Initializing handlers...");
        this.userHandler.initialise();
        this.bankHandler.initialise();
        this.weBridge.initialise();
        this.worldHandler.initialise();
        this.sandtakHandler.initialise();

        this.getLogger().info(" - Initializing handlers, part 2...");
        //this.afkkickhandler.reloadAllAfk();
        this.userHandler.reloadUsers();
        this.startupHandler.weather();
        this.startupHandler.checkSpace();
        this.groupHandler.getAllGroups();

        this.getLogger().info(" - Scheduling tasks...");
        new WorldBackup(this).scheduleWorldBackup();
        new ServerLogBackup(this).scheduleServerLogBackup();
        new SpawnProtection(this).scheduleSpawnProtection();

        if (getServer().getScheduler().runTaskTimerAsynchronously(this, this.blockHandler, 20, 20).getTaskId() > 0) {
            this.getLogger().info("Scheduled start up.");
        } else {
            this.getLogger().info("Scheduled failed. Starting now manualy.");
            timer = new Timer();
            timer.scheduleAtFixedRate(this.blockHandler, 1000, 1000);
        }

        this.getLogger().info(" - Enabling custom recipes...");
        new RecipeHandler(this).registerRecipes();

        this.getLogger().info(" - Registering commands...");
        registerCommands();

        // Enable or disable whitelist on plugin load.
        // Enable whitelist in server.properties to ensures nobody
        // can join the server until we're really ready for it.
        if (this.getConfig().getBoolean("whitelist.on_enable.toggle", false)) {
            this.getServer().setWhitelist(this.getConfig().getBoolean("whitelist.on_enable.state", false));
        }

        this.getLogger().info("Finished enabling Minecraftno plugin.");
    }

    public void onDisable() {
        this.getLogger().info("Disabling Minecraftno plugin...");

        // Enable whitelist to stop players joining while we're shutting down.
        if (this.getConfig().getBoolean("whitelist.on_disable.toggle", false)) {
            this.getServer().setWhitelist(this.getConfig().getBoolean("whitelist.on_disable.toggle", true));

            // Kick everyone who's already online before we start disabling functionality.
            // They're not supposed to be here anyway.
            for (Player player : this.getServer().getOnlinePlayers()) {
                player.kickPlayer(this.getConfig().getString("whitelist.message", "The server is shutting down..."));
            }
        }

        if (configuration.irc && bot.isConnected()) {
            this.getLogger().info(" - Disabling IRC bot...");
            bot.disconnect();
            bot.dispose();
        }

        this.getLogger().info(" - Handlers cleanup...");
        this.groupHandler.cleanup();
        this.userHandler.cleanup();

        try {
            configuration.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.blockHandler != null) {
            if (this.blockHandler.getQueueSize() > 0) {
                this.getLogger().info(" - Waiting for block handler to finish...");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.blockHandler.run();
            }
        }

        if (sqlc != null) {
            this.getLogger().info(" - Closing SQL connection...");
            sqlc.close();
        }

        this.getLogger().info(" - Cancelling scheduled tasks...");
        getServer().getScheduler().cancelAllTasks();

        this.getLogger().info("Finished disabling Minecraftno plugin.");
    }

    public ConfigurationServer getGlobalConfiguration() {
        return configuration;
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(vehicleListener, this);
        pm.registerEvents(weatherListener, this);
        pm.registerEvents(inventoryListener, this);
        pm.registerEvents(worldListener, this);
        pm.registerEvents(serverListener, this);
        pm.registerEvents(hangingListener, this);
    }

    public boolean sqlConnection() {
        try {
            sqlc = new MySQLConnectionPool(this.configuration.dbhost, this.configuration.dbport, this.configuration.dbname, this.configuration.dbuser, this.configuration.dbpass);

            // Create a connection to test for success...
            final Connection conn = getConnection();
            if (conn == null) {
                this.getLogger().severe("Could not connect to SQL server!");
                this.getServer().shutdown();

                return false;
            }

            conn.close();
            sqlConnected = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sqlConnected;
    }

    public Connection getConnection() {
        try {
            final Connection conn = sqlc.getConnection();

            if (!sqlConnected && conn != null) {
                this.getLogger().info("SQL connection re-established.");
                sqlConnected = true;
            }

            return conn;
        } catch (final Exception e) {
            sqlConnected = false;

            this.getLogger().severe("Could not fetch SQL connection! " + e.getMessage());

            return null;
        }
    }

    public void registerCommands() {
        CommandListHelper helper = new CommandListHelper();

        WarpHandler wh = new WarpHandler(this);
        wh.initialise();

        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this, wh));
        getCommand("warp").setExecutor(new WarpCommand(this, wh));
        getCommand("warps").setExecutor(new WarpsCommand(this, wh));
        getCommand("msg").setExecutor(new MCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tphere").setExecutor(new TpHereCommand(this));
        getCommand("give").setExecutor(new ICommand(this));
        getCommand("gr").setExecutor(new GrCommand(this));
        getCommand("g").setExecutor(new GCommand(this));
        getCommand("stuck").setExecutor(new StuckCommand(this));
        getCommand("bc").setExecutor(new BcCommand(this));
        getCommand("hjelp").setExecutor(new HelpCommand(this, helper));
        getCommand("invisible").setExecutor(new InvisibleCommand(this));
        getCommand("goto").setExecutor(new GotoCommand(this));
        getCommand("ci").setExecutor(new CiCommand(this));
        getCommand("lok").setExecutor(new LocCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("add").setExecutor(new AddCommand(this));
        getCommand("del").setExecutor(new DelCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("mod").setExecutor(new ModCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this, wh));
        getCommand("reg").setExecutor(new RegCommand(this));
        getCommand("c").setExecutor(new CCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("listwarnings").setExecutor(new ListWarningsCommand(this));
        getCommand("work").setExecutor(new WorkCommand(this));
        getCommand("world").setExecutor(new WorldCommand(this));
        getCommand("itemarmor").setExecutor(new ItemArmor(this));
        getCommand("mount").setExecutor(new MountCommand(this));
        getCommand("tooldata").setExecutor(new ToolDataCommand(this));
        getCommand("saveplayerdata").setExecutor(new SavePlayerDataCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("t").setExecutor(new TradeCommand(this));
        getCommand("r").setExecutor(new ReplayCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("chat").setExecutor(new ChatCommand(this));
        getCommand("tid").setExecutor(new TimeCommand(this));
        getCommand("me").setExecutor(new MeCommand(this));
        getCommand("tips").setExecutor(new TipsCommand(this));
        getCommand("xp").setExecutor(new XPCommand(this));
        getCommand("openinv").setExecutor(new OpenInvCommand(this));
        getCommand("mobspawn").setExecutor(new MobSpawnCommand(this));
        getCommand("mode").setExecutor(new ModeCommand(this));
        getCommand("nodrop").setExecutor(new NoDropCommand(this));
        getCommand("ukesban").setExecutor(new WeekBanCommand(this));
        getCommand("back").setExecutor(new TpBackCommand(this));
        getCommand("ram").setExecutor(new MemoryCommand(this));
        getCommand("gadm").setExecutor(new GroupAdminCommand(this));
        getCommand("cms").setExecutor(new ChangeMSCommand(this));
        getCommand("seteffect").setExecutor(new SetPlayerEffectCommand(this));
        getCommand("allowfly").setExecutor(new allowflyCommand(this));
        getCommand("annonsering").setExecutor(new AnnouncementCommand(this));
        getCommand("mworld").setExecutor(new MultiworldCommand(this));
        getCommand("warning").setExecutor(new WarningCommand(this));
        getCommand("filter").setExecutor(new WordFilterCommand(this));
        getCommand("srvinfo").setExecutor(new ServerInfoCommand(this));
        getCommand("gwarps").setExecutor(new GwarpsCommand(this, wh));
        getCommand("gwarp").setExecutor(new GwarpCommand(this, wh));
        getCommand("setgwarp").setExecutor(new SetGwarpCommand(this, wh));
        getCommand("delgwarp").setExecutor(new DelGwarpCommand(this, wh));
        getCommand("privat").setExecutor(new PrivateCommand(this));
        getCommand("sandtak").setExecutor(new SandtakCommand(this));
        getCommand("hest").setExecutor(new HorsesCommand(this));
        getCommand("nametag").setExecutor(new NametagCommand(this));
        getCommand("nisse").setExecutor(new NisseCommand(this));
        getCommand("minecart").setExecutor(new MinecartCommand(this));
        getCommand("wiki").setExecutor(new WikiCommand(this));
        getCommand("irc").setExecutor(new IrcCommand(this));

        if (weBridge.isEnabled()) {
            // Bare bruk kommandoene hvis WE er lastet.
            getCommand("setareabp").setExecutor(new SetAreaBPCommand(this));
            getCommand("unprotectarea").setExecutor(new UnprotectAreaCommand(this));
        }

        // Denne må kjøres etter at alle kommandoene er satt for å få
        // access-levels.
        helper.populateLists(this);
    }

    // Todo: Get rid of these methods. They should be replaced by config calls.
    public final static String notRegistredMessage() {
        return ChatColor.RED + " Du er ikke registrert på Hardwork! Sjekk /reg.";
    }

    public final static String notAllowedItemMessage() {
        return ChatColor.RED + " Dette er ett ulovlig item, det blir nå slettet.";
    }

    public final static String getPlayerIP(Player player) {
        String address = player.getAddress().toString();

        address = address.substring(1);
        address = address.split(":")[0];

        return address;
    }

    public User playerMatchIRC(String name) {
        if (getServer().getOnlinePlayers().length < 1) {
            return null;
        }

        for (User user : getIrcBot().getUsers("#hardwork")) {
            if (user.getNick().equalsIgnoreCase(name)) {
                return user;
            }
        }

        return null;
    }

    public Player playerMatch(String name) {
        return this.getServer().getPlayerExact(name);
    }

    public ArrayList<Integer> formatTime(BanData banData) {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        long set = banData.getUnixTime();

        int time = (int) (System.currentTimeMillis() / 1000L);
        int diff = (int) ((set - time) + 604800);

        int d = diff / 86400 % 7;
        int h = diff / 3600 % 24;
        int m = diff / 60 % 60;
        int s = diff % 60;

        ret.add(d);
        ret.add(h);
        ret.add(m);
        ret.add(s);

        return ret;
    }

    public double getDistance2(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player1.getLocation();

        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2.0D) + Math.pow(loc1.getY() - loc2.getY(), 2.0D) + Math.pow(loc1.getZ() - loc2.getZ(), 2.0D));
    }

    public IRCBot getIrcBot() {
        return this.bot;
    }

    public UserHandler getUserHandler() {
        return this.userHandler;
    }

    public WorldHandler getWorldHandler() {
        return this.worldHandler;
    }

    public MySQLHandler getSqlHandler() {
        return this.sqlHandler;
    }

    public LogHandler getLogHandler() {
        return this.logHandler;
    }

    public GroupLogHandler getGroupLogHandler() {
        return this.groupLogHandler;
    }

    public ChatHandler getChatHandler() {
        return this.chathandler;
    }

    public BankHandler getBankHandler() {
        return this.bankHandler;
    }

    public GroupHandler getGroupHandler() {
        return this.groupHandler;
    }

    public BlockHandler getBlockHandler() {
        return this.blockHandler;
    }

    public BlockInfoHandler getBlockInfoHandler() {
        return this.blockinfoHandler;
    }

    public PrivateProtectionHandler getPrivateProtectionHandler() {
        return this.privatePHandler;
    }

    public WarningHandler getWarningHandler() {
        return this.warningHandler;
    }

    public SandtakHandler getSandtakHandler() {
        return this.sandtakHandler;
    }

    public NisseHandler getNisseHandler() {
        return this.nisseHandler;
    }

    public WEBridge getWeBridge() {
        return weBridge;
    }

    public void setWeBridge(WEBridge weBridge) {
        this.weBridge = weBridge;
    }

    public ItemStack matchItem(String name) {
        int id = 0;
        int dmg = 0;
        String dataName = null;

        if (name.contains(":")) {
            String[] parts = name.split(":");
            dataName = parts[1];
            name = parts[0];
        }
        try {
            id = Integer.parseInt(name);
            Material valid = Material.getMaterial(id);
            if (valid == null) {
                return null;
            }
        } catch (NumberFormatException e) {
            Material type = Material.getMaterial(name.toUpperCase());

            if (type == null) {
                return null;
            }

            id = type.getId();
        }

        if (dataName != null) {
            dmg = matchItemData(id, dataName);

            if (dmg < 1) {
                return null;
            }
        }

        return new ItemStack(id, 1, (short) dmg);
    }

    public static int matchItemData(int id, String filter) {
        // Missing some key code, need to be finished sometime in future.
        try {
            return Integer.parseInt(filter);
        } catch (NumberFormatException ignored) {

        }

        if (Material.WOOD.getId() == id) {
            for (TreeSpecies search : TreeSpecies.values()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return search.getData();
                }
            }

            return 0;
        } else if (Material.WOOL.getId() == id) {
            for (DyeColor search : DyeColor.values()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return search.getWoolData();
                }
            }

            return 0;
        } else if (Material.INK_SACK.getId() == id) {
            for (DyeColor search : DyeColor.values()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return search.getDyeData();
                }
            }

            return 0;
        } else if (Material.STEP.getId() == id) {
            Step s = new Step();
            for (Material search : s.getTextures()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return s.getTextures().indexOf(search);
                }
            }

            return 0;
        } else if (Material.DOUBLE_STEP.getId() == id) {
            Step s = new Step();
            for (Material search : s.getTextures()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return s.getTextures().indexOf(search);
                }
            }

            return 0;
        } else if (Material.SMOOTH_BRICK.getId() == id) {
            SmoothBrick s = new SmoothBrick();
            for (Material search : s.getTextures()) {
                if (filter.equalsIgnoreCase(search.toString())) {
                    return s.getTextures().indexOf(search);
                }
            }

            return 0;
        }

        return 0;
    }

    public final static boolean canParse(String i) {
        try {
            Integer.valueOf(Integer.parseInt(i));
            return true;
        } catch (NumberFormatException ne) {

        }
        return false;
    }
}
