package no.minecraft.hardwork.listeners;

import no.minecraft.hardwork.Hardwork;
import no.minecraft.hardwork.handlers.BlockHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockListener implements Listener {
    private final Hardwork hardwork;

    public BlockListener(Hardwork hardwork) {
        this.hardwork = hardwork;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.SAPLING) {
            this.hardwork.getBlockHandler().deleteBlockOwner(block);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        BlockHandler blockHandler = this.hardwork.getBlockHandler();

        for (BlockState state : event.getBlocks()) {
            blockHandler.deleteBlockOwner(state.getBlock());
        }
    }
}
