package no.minecraft.Minecraftno.conf;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationServer {

    private Minecraftno plugin;
    public ArrayList<Location> locations;
    private Map<String, ConfigurationWorld> worlds;
    private HashMap<String, Object> configDefaults;
    private YamlConfiguration config;
    private File configFile;

    public ConfigurationServer(Minecraftno plugin) {
        this.plugin = plugin;
        this.worlds = new HashMap<String, ConfigurationWorld>();
        this.configDefaults = new HashMap<String, Object>();
        this.locations = new ArrayList<Location>();
    }

    // Database
    public String dbuser;
    public String dbpass;
    public String dbname;
    public String dbhost;
    public String Maindbname;
    public int dbport;

    // Chars for caps
    public double percentage;

    // Server
    public boolean showloginonguest = true;
    public boolean maintaince;
    public boolean stop;
    public boolean backup;
    public String backupFolder;

    // irc
    public boolean irc;
    public String irchostname;
    public String ircname;
    public int ircport;

    // Antal spillere
    public int maxplayers;
    public boolean showWaringsJoin;
    public boolean warpInfoJoin;
    public boolean showGroupInvitesJoin;

    // Fill
    public List<Integer> illegalItems = new ArrayList<Integer>();
    public List<Integer> defaultThroughBlock = new ArrayList<Integer>();
    public List<Integer> noCraft = new ArrayList<Integer>();
    public List<Integer> noRemoveFromWaterLava = new ArrayList<Integer>();
    public List<Integer> noDamageTools = new ArrayList<Integer>();
    public List<Integer> nonProtectBlocks = new ArrayList<Integer>();
    public List<Integer> protectBlockUp = new ArrayList<Integer>();
    public List<Integer> protectUnprotected = new ArrayList<Integer>();

    // Login og reg
    public List<String> srvReg = new ArrayList<String>();
    public List<String> srvLogin = new ArrayList<String>();

    // BadWords
    public List<String> advertise = new ArrayList<String>();

    public void load() throws Exception {

        // Location
        locations.add(new Location(this.plugin.getServer().getWorlds().get(0), 5449, 63, 1860));
        locations.add(new Location(this.plugin.getServer().getWorlds().get(0), 5476, 63, 1860));
        locations.add(new Location(this.plugin.getServer().getWorlds().get(0), 5503, 63, 1860));
        locations.add(new Location(this.plugin.getServer().getWorlds().get(0), 5530, 63, 1860));

        this.config = new YamlConfiguration();
        this.configFile = new File(plugin.getDataFolder(), "Minecraftno.yml");

        // DB
        this.configDefaults.put("SQL.db-user", "username");
        this.configDefaults.put("SQL.db-pass", "password");
        this.configDefaults.put("SQL.db-name", "database");
        this.configDefaults.put("SQL.db-main-name", "database");
        this.configDefaults.put("SQL.db-host", "localhost");
        this.configDefaults.put("SQL.db-port", 3306);

        // Caps
        this.configDefaults.put("Caps.prosent", 40);

        // Server
        this.configDefaults.put("Server.vedlikehold", false);
        this.configDefaults.put("Server.stop", false);
        this.configDefaults.put("Server.backup", true);
        this.configDefaults.put("Server.backupFolder", "/home/minecraft/backups/");

        // IRC
        this.configDefaults.put("IRC.irc-on", true);
        this.configDefaults.put("IRC.server", "irc.hostname.com");
        this.configDefaults.put("IRC.port", 6697);
        this.configDefaults.put("IRC.nick", "H");

        // Player
        this.configDefaults.put("Player.max-players", 200);
        this.configDefaults.put("Player.ShowWaringsOnJoin", true);
        this.configDefaults.put("Player.WarpToInfoOnJoinGuest", true);
        this.configDefaults.put("Player.ShowGroupInvitesOnJoin", true);

        // Fill
        this.configDefaults.put("Fill.blokker-kompasset-ignorer", "0,6,8,9,10,11,27,28,30,31,32,37,38,39,40,50,55,59,63,65,66,68,69,70,72,75,76,77,78,83");
        this.configDefaults.put("Fill.blokker-som-ikke-blir-beskyttet", "2,3,6,7,8,9,10,11,12,13,39,40,51,59,60,83,104,105,106,115,141,142,355");
        this.configDefaults.put("Fill.blokker-som-beskytter-blokken-under", "37,38,40,55,63,66,93,94");
        this.configDefaults.put("Fill.blokker-som-blir-sett-som-verdifulle", "19,20,35,41,42,45,47,54,57,89");
        this.configDefaults.put("Fill.blokker-stengt-ute-for-normale-brukere", "7,8,9,10,11,19,46,51,52,79,90,302,303,304,305");
        this.configDefaults.put("Fill.blokker-du-ikke-kan-crafte", "46,259");
        this.configDefaults.put("Fill.blokker-som-er-imune-mot-vann", "27,28,50,55,66,69,75,76,77,93,94");

        this.configDefaults.put("Fill.verktoy-som-ikke-tar-skade", "256,257,258,259,261,267,268,269,270,271,272,273,274,275,276,277,278,279,283,284,285,286,290,291,292,293,294,346,359");

        // Login
        this.configDefaults.put("Reg.Melding", "1. G/a/ til www.minecraft.no /::/ 2. Trykk p/a/ byggetillatelse i menyen. /::/ 3. Trykk p/a/ tr/a/den som heter LES HER F/o/R DU SØKER /::/ 4. Opprett ny tr/a/d på samme plass og f/o/lg punktene som st/a/r i tr/a/den. /::/ 5. N/a/r s/o/knaden din er godkjent vil du bli lagt til på serveren.");
        this.configDefaults.put("Login.Melding", "&6========[ &9 www.minecraft.no - Hardwork buildserver &6 ]======= /::/ For informasjon bes/o/k www.minecraft.no");

        // WordFilter:
        String[] advertiseshort = {"closeddoors", "closeddoors.org", "mc.closeddoors.org"};
        this.configDefaults.put("BadWords.advertise", advertiseshort);

        // Gifts

        this.configDefaults.put("Server.gifts.item", "1,5,12,45,48,49,86,91,82,115,263,264,265,266,332,334,336,337,341,344,348,351,353,357,359,360,369,370,371,372,375,376,377,378,382");

        if (!this.configFile.exists()) {
            for (String key : this.configDefaults.keySet()) {
                this.config.set(key, this.configDefaults.get(key));
            }
            try {
                this.config.save(this.configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.config.load(this.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                for (String key : this.configDefaults.keySet()) {
                    if (!this.config.contains(key)) {
                        this.config.set(key, this.configDefaults.get(key));
                    }
                }
            }
        }

        this.dbuser = config.getString("SQL.db-user");
        this.dbpass = config.getString("SQL.db-pass");
        this.dbname = config.getString("SQL.db-name");
        this.Maindbname = config.getString("SQL.db-main-name");
        this.dbhost = config.getString("SQL.db-host");
        this.dbport = config.getInt("SQL.db-port");

        this.percentage = config.getDouble("Caps.prosent");

        this.maintaince = config.getBoolean("Server.vedlikehold");
        this.stop = config.getBoolean("Server.stop");
        this.backup = config.getBoolean("Server.backup");
        this.backupFolder = config.getString("Server.backupFolder");

        this.irc = config.getBoolean("IRC.irc-on");
        this.irchostname = config.getString("IRC.server");
        this.ircname = config.getString("IRC.nick");
        this.ircport = config.getInt("IRC.port");

        this.maxplayers = config.getInt("Player.max-players");
        this.showWaringsJoin = config.getBoolean("Player.ShowWaringsOnJoin");
        this.warpInfoJoin = config.getBoolean("Players.WarpToInfoOnJoinGuest");
        this.showGroupInvitesJoin = config.getBoolean("Player.ShowGroupInvitesOnJoin");

        String[] tempillegalItems = config.getString("Fill.blokker-stengt-ute-for-normale-brukere").split(",");
        for (String item : tempillegalItems) {
            illegalItems.add(Integer.parseInt(item));
        }

        String[] tempdefaultThroughBlock = config.getString("Fill.blokker-kompasset-ignorer").split(",");
        for (String item : tempdefaultThroughBlock) {
            defaultThroughBlock.add(Integer.parseInt(item));
        }

        String[] tempNoCraft = config.getString("Fill.blokker-du-ikke-kan-crafte").split(",");
        for (String item : tempNoCraft) {
            noCraft.add(Integer.parseInt(item));
        }

        String[] tempnoRemoveFromWaterLava = config.getString("Fill.blokker-som-er-imune-mot-vann").split(",");
        for (String item : tempnoRemoveFromWaterLava) {
            noRemoveFromWaterLava.add(Integer.parseInt(item));
        }

        String[] tempnoDamageTools = config.getString("Fill.verktoy-som-ikke-tar-skade").split(",");
        for (String item : tempnoDamageTools) {
            noDamageTools.add(Integer.parseInt(item));
        }

        String[] tempnonProtectBlocks = config.getString("Fill.blokker-som-ikke-blir-beskyttet").split(",");
        for (String item : tempnonProtectBlocks) {
            nonProtectBlocks.add(Integer.parseInt(item));
        }

        String[] tempprotectBlockUp = config.getString("Fill.blokker-som-beskytter-blokken-under").split(",");
        for (String item : tempprotectBlockUp) {
            protectBlockUp.add(Integer.parseInt(item));
        }

        String[] tempprotectUnprotected = config.getString("Fill.blokker-som-blir-sett-som-verdifulle").split(",");
        for (String item : tempprotectUnprotected) {
            protectUnprotected.add(Integer.parseInt(item));
        }

        String[] tempsrvReg = config.getString("Reg.Melding").split("/::/");
        for (String item : tempsrvReg) {
            srvReg.add(colorTxt(item.trim().replace("/a/", "å").replace("/o/", "ø")));
        }

        String[] tempsrvLogin = config.getString("Login.Melding").split("/::/");
        for (String item : tempsrvLogin) {
            srvLogin.add(colorTxt(item.trim().replace("/a/", "å").replace("/o/", "ø")));
        }

        this.advertise = this.config.getStringList("BadWords.advertise");
        Collections.sort(advertise, new StringLengthComparator());

        for (World world : this.plugin.getServer().getWorlds()) {
            get(world);
        }
    }

    public void cleanup() throws Exception {
        this.configDefaults.clear();

        for (World world : this.plugin.getServer().getWorlds()) {
            get(world).cleanup();
        }

        this.worlds.clear();
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationWorld get(World world) {
        String worldName = world.getName();
        ConfigurationWorld config = this.worlds.get(worldName);

        if (config == null) {
            config = new ConfigurationWorld(this.plugin, worldName);
            this.worlds.put(worldName, config);
        }

        return config;
    }

    public String colorTxt(String string) {
        string = string.replaceAll("&0", ChatColor.BLACK + "");
        string = string.replaceAll("&1", ChatColor.DARK_BLUE + "");
        string = string.replaceAll("&2", ChatColor.DARK_GREEN + "");
        string = string.replaceAll("&3", ChatColor.DARK_AQUA + "");
        string = string.replaceAll("&4", ChatColor.DARK_RED + "");
        string = string.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        string = string.replaceAll("&6", ChatColor.GOLD + "");
        string = string.replaceAll("&7", ChatColor.GRAY + "");
        string = string.replaceAll("&8", ChatColor.DARK_GRAY + "");
        string = string.replaceAll("&9", ChatColor.BLUE + "");
        string = string.replaceAll("&a", ChatColor.GREEN + "");
        string = string.replaceAll("&b", ChatColor.AQUA + "");
        string = string.replaceAll("&c", ChatColor.RED + "");
        string = string.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        string = string.replaceAll("&e", ChatColor.YELLOW + "");
        string = string.replaceAll("&f", ChatColor.WHITE + "");
        return string;
    }

    public boolean addAdvertiseWord(String badWord) {
        if (!advertise.contains(badWord)) {
            if (advertise.add(badWord)) {
                Collections.sort(advertise, new StringLengthComparator());
                config.set("BadWords.advertise", advertise);
                return true;
            }
        }

        return false;
    }

    public boolean delAdvertiseWord(String badWord) {
        if (advertise.contains(badWord)) {
            if (advertise.remove(badWord)) {
                config.set("BadWords.advertise", advertise);
                return true;
            }
        }

        return false;
    }

    public class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (o1.length() < o2.length()) {
                return -1;
            } else if (o1.length() > o2.length()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
