package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

public class LocationData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -184729978551486973L;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LocationData(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String getworld() {
        return world;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public double getz() {
        return z;
    }

    public float getyaw() {
        return yaw;
    }

    public float getpitch() {
        return pitch;
    }
}
