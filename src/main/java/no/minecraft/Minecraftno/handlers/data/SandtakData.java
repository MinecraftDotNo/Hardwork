package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.Location;

public class SandtakData {

    private int sandtakId;
    private String sandtakName;
    private Location pos1;
    private Location pos2;
    String worldName;
    private int sandtakSize;

    public SandtakData(int id, String name, Location pos1, Location pos2, String worldName) {
        this.sandtakId = id;
        this.sandtakName = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.worldName = worldName;
        this.sandtakSize = this.calculateSize(pos1, pos2);
    }

    /**
     * Calculates the number of double chests the sandtak can be filled with
     *
     * @param pos1 Position 1 of the sandtak
     * @param pos2 Position 2 of the sandtak
     *
     * @return
     */
    private int calculateSize(Location pos1, Location pos2) {
        int x = Math.abs((int) pos1.getX() - (int) pos2.getX()) + 1;
        int y = Math.abs((int) pos1.getY() - (int) pos2.getY()) + 1;
        int z = Math.abs((int) pos1.getZ() - (int) pos2.getZ()) + 1;

        return ((x * y * z) / 3456);
    }

    /**
     * Returns the name of the sandtak
     *
     * @return
     */
    public String getName() {
        return this.sandtakName;
    }

    /**
     * Returns the amount of double chests needed to fill the sandtak.
     *
     * @return
     */
    public int getSize() {
        return this.sandtakSize;
    }

    public Location getPos1() {
        return this.pos1;
    }

    public Location getPos2() {
        return this.pos2;
    }

    /**
     * Returns the world of which the sandtak is positioned in
     *
     * @return
     */
    public String getWorldName() {
        return this.worldName;
    }

    public int getSandtakId() {
        return this.sandtakId;
    }
}
