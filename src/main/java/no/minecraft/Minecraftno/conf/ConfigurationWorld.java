package no.minecraft.Minecraftno.conf;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigurationWorld {

    private Minecraftno plugin;
    private String worldName;
    private HashMap<String, Object> configDefaults = new HashMap<String, Object>();
    private YamlConfiguration config;
    private File configFile;

    public boolean makePortal;
    public boolean protectBlocks;
    public boolean protectPaintings;
    public boolean logPaintings;
    public boolean NoDrop;
    public boolean NoLeavesDecay;
    public boolean blockInstantBreak;
    public boolean BlockNoDrop;
    public boolean BlockNoLeavesDecay;
    public boolean Damage;
    public boolean FallDamage;
    public boolean PVPDamage;
    public boolean CreatureDamage;
    public boolean LavaDamage;
    public boolean FireDamage;
    public boolean LightningDamage;
    public boolean DrowningDamage;
    public boolean SuffocationDamage;
    public boolean ContactDamage;
    public boolean VoidDamage;
    public boolean CreeperBlockDamage;
    public boolean CreeperExplosions;
    public boolean FireSpread;
    public boolean Thunder;
    public boolean Weather;
    public boolean CreeperPower;
    public boolean LightningFire;
    public boolean TNT;
    public boolean Lighter;
    public boolean Lighternetherrack;
    public boolean itemDurability;
    public boolean logBlocks;
    public boolean teleportOnVoid;
    public boolean LavaFire;
    public boolean simulateSponge;
    public int spongeRadius;
    public boolean alwaysDay;
    public boolean MagicMachine;
    public boolean Skylands;
    public boolean Nether;
    public boolean CactusCanGrow;
    public boolean FysikkSandGravel;
    public boolean bucket;
    public boolean regenIceSnow;
    public boolean canChangeTime;
    public boolean PVPgroups;
    public boolean pvpWorld;
    public boolean adminStick;
    public boolean flyBoots;
    public boolean restrictNumberOfWolfs;
    public int maximumNumberOfWolfs;
    public boolean allowUseCompass;
    public boolean healthRegen;
    public int MaxEntities;
    public boolean unlimitedFoodBar;

    public boolean restrictWorldXYZ;
    public int restrictWorldXminus;
    public int restrictWorldXpluss;
    public int restrictWorldZminus;
    public int restrictWorldZpluss;

    //Animals && Mobs
    public boolean chicken;
    public boolean cow;
    public boolean creeper;
    public boolean ghast;
    public boolean giant;
    public boolean pig;
    public boolean pig_zombie;
    public boolean sheep;
    public boolean skeleton;
    public boolean slime;
    public boolean spider;
    public boolean squid;
    public boolean zombie;
    public boolean wolf;
    public boolean cave_spider;
    public boolean enderman;
    public boolean silverfish;
    public boolean ender_dragon;
    public boolean villager;
    public boolean blaze;
    public boolean mushroom_cow;
    public boolean snowman;
    public boolean wither;
    public boolean witch;
    public boolean bats;

    public boolean supercreepers;

    public ConfigurationWorld(Minecraftno plugin, String worldName) {
        this.plugin = plugin;
        this.worldName = worldName;

        loadConfiguration();
    }

    private void loadConfiguration() {
        this.config = new YamlConfiguration();
        this.configFile = new File(plugin.getDataFolder() + "/worlds/" + this.worldName, "config.yml");

        // Portal
        this.configDefaults.put("Portal.lage-nether-portal", false);

        // Items
        this.configDefaults.put("Items.Items-tar-skade", false);
        this.configDefaults.put("Items.lighter", false);
        this.configDefaults.put("Items.lighter-netherrack", false);
        this.configDefaults.put("Items.Boette", false);

        // Beskyttelse
        this.configDefaults.put("Beskyttelse.Beskytt-blokker", true);
        this.configDefaults.put("Beskyttelse.Beskytt-bilder", true);

        // Log
        // TODO implimenter dette over hele koden for å sjekke om loggføring er på
        this.configDefaults.put("Logging.Blokker", true);

        //Blocks
        this.configDefaults.put("Blokker.Ingen-dropp", false);
        this.configDefaults.put("Blokker.Loev-forsvinner", true);
        this.configDefaults.put("Blokker.TNT", false);

        // Damage
        this.configDefaults.put("Spiller.Skade.Spilleren-kan-bli-skadet", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-fall", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-spiller-mot-spiller", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-spiller-mot-monster", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-kontakt-med-lava", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-kontakt-med-brann", false);
        this.configDefaults.put("Spiller.Miste-liv-ved-kontakt-med-lyn", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-drukning", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-inni-blokk", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-ved-kontakt-med-blokk", false);
        this.configDefaults.put("Spiller.Skade.Miste-liv-i-void", false);

        // Player
        this.configDefaults.put("Spiller.Teleporter-ut-av-void", true);
        this.configDefaults.put("Spiller.Kan-endre-tid-i-verden", true);
        this.configDefaults.put("Spiller.Admin-Stick", true);
        this.configDefaults.put("Spiller.Kan-fly-med-gull-sko-(access-level-2-og-opp)", true);
        this.configDefaults.put("Spiller.Begrens-antall-ulver", true);
        this.configDefaults.put("Spiller.Max-antall-ulver-spiller-kan-ha", 5);
        this.configDefaults.put("Spiller.Kan-bruke-kompass-for-hurtig-flytting(access-level-2-og-opp)", true);
        this.configDefaults.put("Spiller.Liv-regenerer", true);
        this.configDefaults.put("Spiller.Evig-Food-Bar", true);

        // Animals
        this.configDefaults.put("Dyr.Kyllinger", true);
        this.configDefaults.put("Dyr.Kyr", true);
        this.configDefaults.put("Dyr.Griser", false);
        this.configDefaults.put("Dyr.Sauer", false);
        this.configDefaults.put("Dyr.Blekkspruter", false);
        this.configDefaults.put("Dyr.Ulver", false);
        this.configDefaults.put("Dyr.Mushroom_Cow", false);
        this.configDefaults.put("Dyr.Snowman", false);

        // Monsters
        this.configDefaults.put("Mobs.Creeper", false);
        this.configDefaults.put("Mobs.Ghast", false);
        this.configDefaults.put("Mobs.Giant", false);
        this.configDefaults.put("Mobs.Pig_Zombie", false);
        this.configDefaults.put("Mobs.Spider", false);
        this.configDefaults.put("Mobs.Zombie", false);
        this.configDefaults.put("Mobs.Cave_Spider", false);
        this.configDefaults.put("Mobs.Slime", false);
        this.configDefaults.put("Mobs.Enderman", false);
        this.configDefaults.put("Mobs.Ender_Dragon", false);
        this.configDefaults.put("Mobs.Blaze", false);
        this.configDefaults.put("Mobs.Villager", false);
        this.configDefaults.put("Mobs.SuperCreepers", false);
        this.configDefaults.put("Mobs.Skeleton", false);
        this.configDefaults.put("Mobs.Silverfish", false);
        this.configDefaults.put("Mobs.Wither", false);
        this.configDefaults.put("Mobs.Witch", false);
        this.configDefaults.put("Mobs.Bats", false);

        //Entities
        this.configDefaults.put("Entities.MaxEntities", 200);

        //Fire
        this.configDefaults.put("Brann.Lava-kan-tenne-pae-blokker", false);
        this.configDefaults.put("Brann.Brann-kan-spre-seg", false);

        // Simulate
        this.configDefaults.put("Simulasjon.Spong.Spong-kan-brukes", true);
        this.configDefaults.put("Simulasjon.Spong.Spong-radius", 3);

        // Time
        this.configDefaults.put("Tid.Alltid-dag", true);

        // World
        this.configDefaults.put("Verden.Blokker-blir-eksplodert-av-Creepere", false);
        this.configDefaults.put("Verden.Tordenvaer", false);
        this.configDefaults.put("Verden.Vaer", false);
        this.configDefaults.put("Verden.Kaktus-kan-gro", false);
        this.configDefaults.put("Verden.Fysikk-pae-kaktus-og-sand", false);
        this.configDefaults.put("Verden.Regenere-snoe-og-is", false);

        this.configDefaults.put("Verden.Grense-pae-kartet", true);
        this.configDefaults.put("Verden.Grense-X-Minus", -4000);
        this.configDefaults.put("Verden.Grense-X-Pluss", 3500);
        this.configDefaults.put("Verden.Grense-Z-Minus", -4000);
        this.configDefaults.put("Verden.Grense-Z-Pluss", 4000);

        // Hardwork Byggeserver
        this.configDefaults.put("Hardwork.MagiskkMaksin", true);
        this.configDefaults.put("Hardwork.SkyLands", true);
        this.configDefaults.put("Hardwork.Nether", true);

        // PVP
        this.configDefaults.put("PVP.Grupper", false);
        this.configDefaults.put("PVP.PVP-Verden", false);

        // Kommandoer
        for (Command c : PluginCommandYamlParser.parse(plugin)) {
            PluginCommand comm = plugin.getCommand(c.getName());
            if (comm != null) {
                if (comm.getName().equalsIgnoreCase("kjøp")) {
                    this.configDefaults.put("Kommandoer.kjop", true);
                } else {
                    this.configDefaults.put("Kommandoer." + comm.getName(), true);
                }
            }
        }

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
                this.config.load(configFile);
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

        //portal
        this.makePortal = config.getBoolean("Portal.lage-nether-portal");

        // Items
        this.itemDurability = config.getBoolean("Items.Items-tar-skade");
        this.Lighter = config.getBoolean("Items.lighter");
        this.Lighternetherrack = config.getBoolean("Items.lighter-netherrack");
        this.bucket = config.getBoolean("Items.Boette");

        // Beskyttelse
        this.protectBlocks = config.getBoolean("Beskyttelse.Beskytt-blokker");
        this.protectPaintings = config.getBoolean("Beskyttelse.Beskytt-bilder");

        // Log
        // TODO implimenter dette over hele koden for å sjekke om loggføring er på
        this.logBlocks = config.getBoolean("Logging.Blokker");

        //Blocks
        this.NoDrop = config.getBoolean("Blokker.Ingen-dropp");
        this.NoLeavesDecay = config.getBoolean("Blokker.Loev-forsvinner");
        this.TNT = config.getBoolean("Blokker.TNT");

        // Damage
        this.Damage = config.getBoolean("Spiller.Skade.Spilleren-kan-bli-skadet");
        this.FallDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-fall");
        this.PVPDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-spiller-mot-spiller");
        this.CreatureDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-spiller-mot-monster");
        this.LavaDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-kontakt-med-lava");
        this.FireDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-kontakt-med-brann");
        this.LightningDamage = config.getBoolean("Spiller.Miste-liv-ved-kontakt-med-lyn");
        this.DrowningDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-drukning");
        this.SuffocationDamage = config.getBoolean("Spiller.Skade.Miste-liv-inni-blokk");
        this.ContactDamage = config.getBoolean("Spiller.Skade.Miste-liv-ved-kontakt-med-blokk");
        this.VoidDamage = config.getBoolean("Spiller.Skade.Miste-liv-i-void");

        // Player
        this.teleportOnVoid = config.getBoolean("Spiller.Teleporter-ut-av-void");
        this.canChangeTime = config.getBoolean("Spiller.Kan-endre-tid-i-verden");
        this.adminStick = config.getBoolean("Spiller.Admin-Stick");
        this.flyBoots = config.getBoolean("Spiller.Kan-fly-med-gull-sko-(access-level-2-og-opp)");
        this.restrictNumberOfWolfs = config.getBoolean("Spiller.Begrens-antall-ulver");
        this.maximumNumberOfWolfs = config.getInt("Spiller.Max-antall-ulver-spiller-kan-ha", 5);
        this.allowUseCompass = config.getBoolean("Spiller.Kan-bruke-kompass-for-hurtig-flytting(access-level-2-og-opp)");
        this.healthRegen = config.getBoolean("Spiller.Liv-regenerer");
        this.unlimitedFoodBar = config.getBoolean("Spiller.Evig-Food-Bar");

        // Dyr
        this.chicken = config.getBoolean("Dyr.Kyllinger");
        this.cow = config.getBoolean("Dyr.Kyr");
        this.pig = config.getBoolean("Dyr.Griser");
        this.sheep = config.getBoolean("Dyr.Sauer");
        this.squid = config.getBoolean("Dyr.Blekkspruter");
        this.wolf = config.getBoolean("Dyr.Ulver");
        this.mushroom_cow = config.getBoolean("Dyr.Mushroom_Cow");
        this.snowman = config.getBoolean("Dyr.Snowman");

        // Mobs
        this.creeper = config.getBoolean("Mobs.Creeper");
        this.ghast = config.getBoolean("Mobs.Ghast");
        this.giant = config.getBoolean("Mobs.Giant");
        this.pig_zombie = config.getBoolean("Mobs.Pig_Zombie");
        this.skeleton = config.getBoolean("Mobs.Skeleton");
        this.spider = config.getBoolean("Mobs.Spider");
        this.zombie = config.getBoolean("Mobs.Zombie");
        this.cave_spider = config.getBoolean("Mobs.Cave_Spider");
        this.slime = config.getBoolean("Mobs.Slime");
        this.enderman = config.getBoolean("Mobs.Enderman");
        this.ender_dragon = config.getBoolean("Mobs.Ender_Dragon");
        this.blaze = config.getBoolean("Mobs.Blaze");
        this.villager = config.getBoolean("Mobs.Villager");
        this.supercreepers = config.getBoolean("Mobs.SuperCreepers");
        this.silverfish = config.getBoolean("Mobs.Silverfish");
        this.wither = config.getBoolean("Mobs.Wither");
        this.witch = config.getBoolean("Mobs.Witch");
        this.bats = config.getBoolean("Mobs.Bats");

        //Entities
        this.MaxEntities = config.getInt("Entities.MaxEntities");

        //Fire
        this.LavaFire = config.getBoolean("Brann.Lava-kan-tenne-pae-blokker");
        this.FireSpread = config.getBoolean("Brann.Brann-kan-spre-seg");

        // Simulate
        this.simulateSponge = config.getBoolean("Simulasjon.Spong.Spong-kan-brukes");
        this.spongeRadius = Math.max(1, config.getInt("Simulasjon.Spong.Spong-radius", 3)) - 1;

        // Time
        this.alwaysDay = config.getBoolean("Tid.Alltid-dag");

        // World
        this.CreeperBlockDamage = config.getBoolean("Verden.Blokker-blir-eksplodert-av-Creepere");
        this.Thunder = config.getBoolean("Verden.Tordenvaer");
        this.Weather = config.getBoolean("Verden.Vaer");
        this.CactusCanGrow = config.getBoolean("Verden.Kaktus-kan-gro");
        this.FysikkSandGravel = config.getBoolean("Verden.Fysikk-pae-kaktus-og-sand");
        this.regenIceSnow = config.getBoolean("Verden.Regenere-snoe-og-is");

        this.restrictWorldXYZ = config.getBoolean("Verden.Grense-pae-kartet");
        this.restrictWorldXminus = config.getInt("Verden.Grense-X-Minus");
        this.restrictWorldXpluss = config.getInt("Verden.Grense-X-Pluss");
        this.restrictWorldZminus = config.getInt("Verden.Grense-Z-Minus");
        this.restrictWorldZpluss = config.getInt("Verden.Grense-Z-Pluss");

        // Hardwork Byggeserver
        this.MagicMachine = config.getBoolean("Hardwork.MagiskkMaksin");
        this.Skylands = config.getBoolean("Hardwork.SkyLands");
        this.Nether = config.getBoolean("Hardwork.Nether");

        // PVP
        this.PVPgroups = config.getBoolean("PVP.Grupper");
        this.pvpWorld = config.getBoolean("PVP.PVP-Verden");
    }

    public void cleanup() throws Exception {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getWorldName() {
        return this.worldName;
    }

    public boolean isCommandEnabled(String cmd) {
        boolean tof = false;
        if (cmd.equalsIgnoreCase("kjøp")) {
            tof = config.getBoolean("Kommandoer.kjop");
        } else {
            tof = config.getBoolean("Kommandoer." + cmd);
        }
        return tof;
    }
}