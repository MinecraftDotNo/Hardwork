package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class MinecraftnoWorldListener implements Listener {
    private final Minecraftno plugin;

    public MinecraftnoWorldListener(Minecraftno instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPortalCreateEvent(PortalCreateEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getWorld());

        if (!wcfg.makePortal) {
            event.setCancelled(true);
        }
    }

	/*
    @EventHandler(priority=EventPriority.NORMAL)
	public void onWorldInit(WorldInitEvent event) {
		event.getWorld().setKeepSpawnInMemory(true);
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!isSpawnChunk(event.getChunk()))
			plugin.Chunks.put(event.getChunk(),Long.valueOf(new Date().getTime()));
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (isSpawnChunk(event.getChunk())) {
			event.setCancelled(true);
			return;
		}

		if (!plugin.Chunks.containsKey(event.getChunk())) {
			plugin.Chunks.put(event.getChunk(),
					Long.valueOf(new Date().getTime()));
		}

		long age = new Date().getTime() - ((Long) plugin.Chunks.get(event.getChunk())).longValue();

		if (age < plugin.LifeTime)
			event.setCancelled(true);
		else
			plugin.Chunks.remove(event.getChunk());
	}*/
}
