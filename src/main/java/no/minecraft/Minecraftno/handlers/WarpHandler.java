package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.data.WarpData;
import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WarpHandler {

    private final Minecraftno plugin;
    private HashMap<String, WarpData> warps = new HashMap<String, WarpData>();
    private HashMap<String, WarpData> gwarps = new HashMap<String, WarpData>();
    private final String warpFile = "warps";
    private final String gwarpFile = "gwarps";

    public WarpHandler(Minecraftno instance) {
        this.plugin = instance;
    }

    public boolean initialise() {
        return loadWarpsFromFile();
    }

    /**
     * Warps
     * <p/>
     * Returnerer WarpData fra Warp-Hash med navn som samsvarer med String name
     *
     * @param String navn på warp
     *
     * @return String fullnavn om navnet somsvare med @param
     */

    public WarpData getWarp(String name) {
        if (this.stringToWarpAC(name) != null) {
            return this.warps.get(this.stringToWarpAC(name));
        } else {
            return null;
        }
    }

    /**
     * Forsøker å opprette en WarpData, sette den inn i Warp-hash, og lagre til Warp-fil.
     *
     * @param String navn, Location lokasjon
     *
     * @return boolean om den det lagret warp eller ikke.
     */

    public boolean setWarp(String name, Location loc) {
        try {
            WarpData warp = new WarpData(name, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            this.warps.put(name, warp);
            SavedObject.save(warps, new File(this.plugin.getDataFolder(), warpFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Forsøker å slette Warp fra Warp-Hash og lagrer det nye mappet i Warp-filen
     *
     * @param String navn
     *
     * @return boolean om den vart slettet eller ikke
     */

    public boolean delWarp(String name) {
        if (this.stringToWarpAC(name) != null) {
            try {
                this.warps.remove(this.stringToWarpAC(name));
                SavedObject.save(warps, new File(this.plugin.getDataFolder(), warpFile));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Lager en string med alle warps fra Warps-hash. Om den er tom, returnerer man et svar om at den er tom.
     *
     * @return String over alle warps.
     */

    public String getWarpListString() {
        if (this.warps.isEmpty()) {
            return "Det er ingen warps";
        }
        StringBuilder build = new StringBuilder();
        Iterator<String> i = this.warps.keySet().iterator();

        while (i.hasNext()) {
            build.append(i.next() + ", ");
        }

        return build.toString().substring(0, build.toString().length() - 2) + ".";
    }

    public Set<String> getWarpList() {
        return this.warps.keySet();
    }

    private String stringToWarpAC(String name) {
        String warpName = null;
        if (this.warps.isEmpty()) {
            return null;
        }
        Set<String> var = this.warps.keySet();
        String[] warps = var.toArray(new String[var.size()]);
        for (String warp : warps) {
            if (warp.equalsIgnoreCase(name)) {
                warpName = warp;
                break;
            }
        }
        if (warpName == null) {
            for (String warp : warps) {
                if (warp.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                    warpName = warp;
                    break;
                }
            }
        }
        return warpName;
    }

    /**
     * Metoder for Gwarps
     * <p/>
     * Returnerer WarpData fra Gwarp-hash med navn som samsvarer med String name
     */

    public WarpData getGwarp(String name) {
        if (this.stringToGwarpAC(name) != null) {
            return this.gwarps.get(this.stringToGwarpAC(name));
        } else {
            return null;
        }
    }

    /**
     * Forsøker å opprette en WarpData, sette den inn i Gwarp-hash, og lagre til Gwarp-fil.
     */

    public boolean setGwarp(String name, Location loc) {
        try {
            WarpData gwarp = new WarpData(name, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), 345600);
            this.gwarps.put(name, gwarp);
            SavedObject.save(gwarps, new File(this.plugin.getDataFolder(), gwarpFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Forsøker å slette Gwarp fra Gwarp-Hash og lagrer det nye mappet i Gwarp-filen
     */

    public boolean delGwarp(String name) {
        if (this.stringToGwarpAC(name) != null) {
            try {
                this.gwarps.remove(this.stringToGwarpAC(name));
                SavedObject.save(gwarps, new File(this.plugin.getDataFolder(), gwarpFile));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Lager en string med alle gwarps fra Gwarps-hash. Om den er tom, returnerer man et svar om at den er tom.
     */

    public String getGwarpListString() {
        if (this.gwarps.isEmpty()) {
            return "Det er ingen graveområder";
        }
        StringBuilder build = new StringBuilder();
        Iterator<String> i = this.gwarps.keySet().iterator();

        while (i.hasNext()) {
            String warpName = i.next();

            if (checkForValidGwarp(this.gwarps.get(warpName))) {
                build.append(warpName + " (" + this.gwarps.get(warpName).getTimeLeftToString() + "), ");
            } else {
                delGwarp(warpName);
            }
        }

        return build.toString().substring(0, build.toString().length() - 2) + ".";
    }

    private String stringToGwarpAC(String name) {
        String warpName = null;
        if (this.gwarps.isEmpty()) {
            return null;
        }
        Set<String> var = this.gwarps.keySet();
        String[] warps = var.toArray(new String[var.size()]);

        for (String warp : warps) {
            if (warp.equalsIgnoreCase(name)) {
                warpName = warp;
                break;
            }

            if (warp.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                warpName = warp;
                break;
            }
        }
        return warpName;
    }

    public boolean checkForValidGwarp(WarpData warp) {
        return (warp.getTimeLeft() > 0);
    }

    /**
     * Felles metoder
     */

    //LOADERE
    @SuppressWarnings("unchecked")
    public boolean loadWarpsFromFile() {
        try {
            if (new File(this.plugin.getDataFolder(), warpFile).exists()) {
                this.warps = (HashMap<String, WarpData>) SavedObject.load(new File(this.plugin.getDataFolder(), warpFile));
            }

            if (new File(this.plugin.getDataFolder(), gwarpFile).exists()) {
                this.gwarps = (HashMap<String, WarpData>) SavedObject.load(new File(this.plugin.getDataFolder(), gwarpFile));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}