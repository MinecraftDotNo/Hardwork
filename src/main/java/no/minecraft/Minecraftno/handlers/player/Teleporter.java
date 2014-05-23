package no.minecraft.Minecraftno.handlers.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Teleporter {
    private Location destination;

    public Teleporter(Location location) {
        this.destination = location;
    }

    public boolean teleportplayer(Player player) {
        World world = this.destination.getWorld();
        double x = this.destination.getX();
        double y = this.destination.getY();
        double z = this.destination.getZ();

        if (y < 1.0D) {
            y = 1.0D;
        }

        while (blockIsAboveAir(world, x, y, z)) {
            y -= 1.0D;

            if (y < -256) {
                return false;
            }
        }
        while (!blockIsSafe(world, x, y, z)) {
            y += 1.0D;

            if (y > 256) {
                return false;
            }
        }

        player.teleport(new Location(world, x, y, z, this.destination.getYaw(), this.destination.getPitch()));
        return true;
    }

    private boolean blockIsAboveAir(World world, double x, double y, double z) {
        return world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1.0D), (int) Math.floor(z)).getType() == Material.AIR;
    }

    public boolean blockIsSafe(World world, double x, double y, double z) {
        return (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType() == Material.AIR) && (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + 1.0D), (int) Math.floor(z)).getType() == Material.AIR);
    }

    public static double horDistance2(Location loc1, Location loc2) {
        double xRange = loc1.getX() - loc2.getX();
        double zRange = loc1.getZ() - loc2.getZ();
        return (xRange * xRange) + (zRange * zRange);
    }
}