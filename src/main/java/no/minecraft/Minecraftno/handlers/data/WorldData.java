package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.io.Serializable;

public class WorldData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -473183496023764734L;

    protected final String worldName;
    protected final long worldSeed;
    protected final String worldType;
    protected final String worldGen;
    protected final String madeBy;

    public WorldData(String name, long seed, String env, String gen, String by) {
        this.worldName = name;
        this.worldSeed = seed;
        this.worldType = env;
        this.worldGen = gen;
        this.madeBy = by;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public String getName() {
        return this.worldName;
    }

    public World.Environment getEnv() {
        if (this.worldType != null) {
            if (this.worldType.equalsIgnoreCase("NETHER")) {
                return World.Environment.NETHER;
            } else if (this.worldType.equalsIgnoreCase("THE_END")) {
                return World.Environment.THE_END;
            } else {
                return World.Environment.NORMAL;
            }
        } else {
            return null;
        }
    }

    public long getSeed() {
        return this.worldSeed;
    }

    public ChunkGenerator getGen() {
        return null;
    }

    public String getmadeBY() {
        return this.madeBy;
    }
}
