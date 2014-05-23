package no.minecraft.Minecraftno.handlers;

import org.bukkit.World;

import java.util.HashMap;

public class SpleefArena {

    // SpleefArena
    private HashMap<String, SpleefArena> spleefArenas;

    private String name;
    private int fromX;
    private int fromY;
    private int fromZ;
    private int toX;
    private int toY;
    private int toZ;
    private World world;

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromX() {
        return fromX;
    }

    private void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getFromY() {
        return fromY;
    }

    private void setFromZ(int fromZ) {
        this.fromZ = fromZ;
    }

    public int getFromZ() {
        return fromZ;
    }

    private void setToX(int toX) {
        this.toX = toX;
    }

    public int getToX() {
        return toX;
    }

    private void setToY(int toY) {
        this.toY = toY;
    }

    public int getToY() {
        return toY;
    }

    private void setToZ(int toZ) {
        this.toZ = toZ;
    }

    public int getToZ() {
        return toZ;
    }

    private void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void set(String name, int fx, int fy, int fz, int tx, int ty, int tz, World world) {
        if (!this.spleefArenas.containsKey(name)) {
            SpleefArena sa = new SpleefArena();
            sa.setName(name);
            sa.setFromX(fx);
            sa.setFromY(fy);
            sa.setFromZ(fz);
            sa.setToX(tx);
            sa.setToY(ty);
            sa.setToZ(tz);
            sa.setWorld(world);
            this.spleefArenas.put(name, sa);
        }
    }

    public void remove(String name) {
        if (this.spleefArenas.containsKey(name)) {
            this.spleefArenas.remove(name);
        }
    }

    public HashMap<String, SpleefArena> getSpleefArenas() {
        return this.spleefArenas;
    }
}
