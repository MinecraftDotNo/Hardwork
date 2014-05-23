package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

public class MinecraftnoInventoryListener implements Listener {

    private final Minecraftno plugin;

    public MinecraftnoInventoryListener(Minecraftno instance) {
        this.plugin = instance;
    }

	/*@EventHandler(priority=EventPriority.NORMAL)
    public void onInventoryCraft(InventoryCraftEvent event) {
		ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
		ItemStack input = event.getResult();
	    if (input != null) {
	    	Player player = event.getPlayer();
	    	if (cfg.NoCraft.contains(event.getResult().getTypeId())) {
	    		if (this.userHandler.getAccess(player) <= 3) {
	    			event.setCancelled(true);
	    		}
	    	}
	    }
	}*/

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (event.getFuel().getType() == Material.LAVA_BUCKET) {
            final Block fu = event.getBlock();
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    Furnace f = (Furnace) fu.getState();
                    f.getInventory().setItem(1, new ItemStack(Material.BUCKET, 1));
                }
            }, 0L);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        ItemStack result = event.getResult();
        final Block block = event.getBlock();

        if (block.getType() == Material.BURNING_FURNACE && result.getType() == Material.BONE && source.getType() == Material.MILK_BUCKET) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUCKET, 1));
        }
    }

    /*
     * Disabling minecart hoppers until we can check the owner of both the hopper and the chest
     *
     * Reason: Users being able to steal from chests
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getInitiator().getHolder() instanceof HopperMinecart) {
            event.setCancelled(true);
        }
    }
}