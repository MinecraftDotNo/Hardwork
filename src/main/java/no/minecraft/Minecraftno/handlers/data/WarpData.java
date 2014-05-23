package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

public class WarpData implements Serializable {

    private static final long serialVersionUID = -6774021403264985465L;
    private String warpName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;
    private int warpTime = -1;

	/*
     * Warp-metoder
	 */

    public WarpData(String name, String world, double x, double y, double z, float yaw, float pitch) {
        /*
         * Konstruktør
		 */
        this.warpName = name;
        this.worldName = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
	
	/*
	 * Gwarp-metoder
	 */

    public WarpData(String name, String world, double x, double y, double z, float yaw, float pitch, int time) {
		/*
		 * Konstruktør
		 */
        this.warpName = name;
        this.worldName = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.warpTime = (int) (System.currentTimeMillis() / 1000L);
    }

    public int getTime() {
        return this.warpTime;
    }

    public String getTimeLeftToString() {
        int timeleft = getTimeLeft();

        if (timeleft >= 86400) {
            int timeInDays = ((((timeleft / 60) / 60)) / 24);
            int timeInHours = (((timeleft - (86400 * timeInDays)) / 60) / 60);
            return timeInDays + " d " + timeInHours + " t";
        } else if (timeleft <= 86399 && timeleft >= 3600) {
            int timeInHours = ((timeleft / 60) / 60);
            int timeInSeconds = ((timeleft - (3600 * timeInHours)) / 60);
            return timeInHours + " t" + timeInSeconds + " s";
        } else {
            int timeInSeconds = (timeleft / 60);
            return timeInSeconds + " s";
        }
    }

    public int getTimeLeft() {
        int currentTime = (int) (System.currentTimeMillis() / 1000L);
        int timeUsed = currentTime - this.warpTime;
        int validTime = 345600;

        return validTime - timeUsed;
    }
	
	/*
	 * Felles-metoder
	 */

    public String getName() {
        return this.warpName;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public String getWorld() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
