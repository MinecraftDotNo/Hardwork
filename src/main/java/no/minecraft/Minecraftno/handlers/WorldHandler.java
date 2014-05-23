package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.WorldData;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

public class WorldHandler {

    private final Minecraftno plugin;
    private HashMap<String, WorldData> SrvWorld;

    public WorldHandler(Minecraftno instance) {
        this.plugin = instance;
        this.SrvWorld = new HashMap<String, WorldData>();
    }

    public boolean initialise() {
        return loadWorldsFromFile();
    }

    @SuppressWarnings("unchecked")
    private boolean loadWorldsFromFile() {
        try {
            if (new File(this.plugin.getDataFolder(), "/worlds/" + "worldData").exists()) {
                this.SrvWorld = (HashMap<String, WorldData>) SavedObject.load(new File(this.plugin.getDataFolder(), "/worlds/" + "worldData"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            for (Entry<String, WorldData> entry : this.SrvWorld.entrySet()) {
                this.plugin.getServer().createWorld(WorldCreator.name(entry.getValue().getName()).seed(entry.getValue().getSeed()).environment(entry.getValue().getEnv()).generator(entry.getValue().getGen()));
            }
        }
        return true;
    }

    public boolean delWorld(World w) {
        SrvWorld.remove(w.getName());
        this.plugin.getServer().unloadWorld(w.getName(), true);
        File world_files = new File(w.getName() + "/");
        world_files.delete();
        return true;
    }

    public World makeWorld(String name, long seed, Player player) {
        if (this.plugin.getServer().getWorld(name) != null) {
            return null;
        }

        WorldData worldData = new WorldData(name, seed, "NORMAL", null, player.getName());
        World world = makeWorld(worldData);
        world.setDifficulty(Difficulty.EASY);
        this.SrvWorld.put(name, worldData);
        saveWorldDataToFile();
        return world;
    }

    private World makeWorld(WorldData world) {
        WorldCreator creator = WorldCreator.name(world.getName()).seed(world.getSeed()).environment(world.getEnv());
        if (world.getGen() != null) {
            creator = creator.generator(world.getGen());
        }
        if (new File(this.plugin.getDataFolder() + world.getName()).exists()) {
            return this.plugin.getServer().createWorld(WorldCreator.name(world.getName()));
        } else {
            return this.plugin.getServer().createWorld(creator);
        }
    }

    public World makeWorld(String name, long seed, String env, String gen, Player player) {
        if (this.plugin.getServer().getWorld(name) != null) {
            return null;
        } else {
            WorldData worldData = new WorldData(name, seed, env, gen, player.getName());
            World world = makeWorld(worldData);
            world.setDifficulty(Difficulty.EASY);
            this.SrvWorld.put(name, worldData);
            saveWorldDataToFile();
            return world;
        }
    }

    private void saveWorldDataToFile() {
        for (World world : this.plugin.getServer().getWorlds()) {
            if (!SrvWorld.containsKey(world.getName())) {
                this.SrvWorld.put(world.getName(), new WorldData(world.getName(), world.getSeed(), getEnv(World.Environment.NORMAL), null, null));
            }
        }
        try {
            SavedObject.save(SrvWorld, new File(this.plugin.getDataFolder(), "/worlds/" + "worldData"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEnv(World.Environment env) {
        if (env == World.Environment.NETHER) {
            return "NETHER";
        } else if (env == World.Environment.THE_END) {
            return "THE_END";
        } else {
            return "NORMAL";
        }
    }
}