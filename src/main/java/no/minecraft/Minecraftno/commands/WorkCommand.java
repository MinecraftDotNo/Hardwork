package no.minecraft.Minecraftno.commands;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.SavedObject;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.player.HultbergInventory;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SuppressWarnings("deprecation")
public class WorkCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private static File dataFolder;

    public WorkCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.plugin = instance;
        WorkCommand.dataFolder = instance.getDataFolder();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length != 0) {
            return false;
        }

        if (isInWork(player)) {
            player.getInventory().clear(); // Clear because items in empty slots when giving inventory tend to still be there.

            if (this.loadInventory(player)) {
                player.sendMessage(getOkChatColor() + "Du har nå fått inventory tilbake.");
            }
        } else {
            if (this.storeInventory(player)) {
                player.getInventory().clear();

                player.getInventory().setItem(0, new ItemStack(Material.WATCH, 1));
                player.getInventory().setItem(7, new ItemStack(Material.SPONGE, 1));

                if (this.userHandler.getAccess(player) > 2) {
                    player.getInventory().setItem(1, new ItemStack(Material.COMPASS, 1));
                    player.getInventory().setItem(2, new ItemStack(Material.STICK, 1));
                    player.getInventory().setItem(3, new ItemStack(Material.BOOK, 1));
                    player.getInventory().setItem(4, new ItemStack(Material.WOOD_AXE, 1));
                    player.getInventory().setItem(5, new ItemStack(Material.SLIME_BALL, 1));
                    player.getInventory().setItem(6, new ItemStack(Material.PAPER, 1));
                    player.getInventory().setItem(8, new ItemStack(Material.BEDROCK, -1));
                    player.getInventory().setItem(9, new ItemStack(Material.WATER, -1));
                    player.getInventory().setItem(10, new ItemStack(Material.LAVA, -1));
                    player.getInventory().setItem(11, new ItemStack(Material.FIRE, -1));
                }

                player.sendMessage(getOkChatColor() + "Inventory er nå lagret. Du har fått tildelt arbeidsutstyr!");
            }
        }

        return true;
    }

    public boolean storeInventory(Player p) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }      
        
        File workFile = getWorkFileNew(p);
        YamlConfiguration workYaml = YamlConfiguration.loadConfiguration(workFile);
        workYaml.set("inventory.content", p.getInventory().getContents());

        try {
            workYaml.save(workFile);           
            userHandler.getPlayerData(p).setInWork(true);
            return true;
        } catch (Exception e) {
            p.sendMessage(this.getErrorChatColor() + "Kunne ikke lagre inventorien, kontakt en utvikler/stab.");
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke lagre inventory.", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean loadInventory(Player p) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        PlayerInventory pinv = p.getInventory(); 
        boolean legacyMode = false;

        File get = getWorkFileNew(p);
        if (!get.exists()) {
            get = getWorkFileLegacy(p);
            
            if (get.exists() == false)
                return false;
            
            legacyMode = true;
        }

        try {
            /* Must support legacy as some players still have their inventory saved the old way. */
            if (legacyMode == true) {
                HultbergInventory contents = (HultbergInventory) SavedObject.load(get);
                if (contents != null) {
                    contents.setContents(pinv);
                } else {
                    p.sendMessage(this.getErrorChatColor() + "Inventorien var null, som kan bety at inventorien din var tom. Kontakt en stab/utvikler om du trur dette er en feil.");
                }
            } else {
                YamlConfiguration workYaml = YamlConfiguration.loadConfiguration(get);
                
                List<ItemStack> list = (List<ItemStack>) workYaml.get("inventory.content");
                ItemStack[] content = list.toArray(new ItemStack[0]);
                p.getInventory().setContents(content);
            }
            
            get.delete();            
            userHandler.getPlayerData(p).setInWork(false);
            
            return true;
        } catch (Exception e) {
            p.sendMessage(this.getErrorChatColor() + "Kunne ikke hente inventorien, kontakt en utvikler/stab.");
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Kunne ikke hente inventory.", e);
            return false;
        }
    }

    /**
     * Indicates if player is in work. Currently the method PlayerData.isInWork is checked.
     * Legacy is checking if work inventory file exists.
     * 
     * @param p
     * @return
     */
    public static boolean isInWork(Player p) {
        PlayerData pd = Minecraftno.getInstance().getUserHandler().getPlayerData(p);
        
        if (pd.isInWork() == false) {
            File f2 = getWorkFileLegacy(p);
            return f2.exists();
        }

        return true;
    }
    
    public static File getWorkFileLegacy(Player p)
    {
    	return new File(dataFolder + "/workInventories/", p.getName() + ".dat");
    }
    
    public static File getWorkFileNew(Player p)
    {
        return new File(dataFolder + "/workInventories/", p.getUniqueId().toString() + ".dat");
    }
}