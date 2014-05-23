package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.enums.BlockLogReason;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class MinecraftnoHangingListener implements Listener {

    private final Minecraftno plugin;
    private GroupHandler groupHandler;
    private UserHandler userHandler;
    private final BlockInfoHandler blockInfo;
    private BlockHandler blockHandler;

    public MinecraftnoHangingListener(Minecraftno instance) {
        this.plugin = instance;
        this.groupHandler = instance.getGroupHandler();
        this.userHandler = instance.getUserHandler();
        this.blockInfo = instance.getBlockInfoHandler();
        this.blockHandler = instance.getBlockHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock(); // Returns the block that the hanging
        // entity was placed on
        Entity entity = event.getEntity();
        boolean allowed = false;

        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(player.getWorld());

        if (wcfg.protectBlocks) {
            int gid = this.groupHandler.getGroupID(player);
            String owner = this.blockInfo.getOwner(block);
            if (owner == null) {
                allowed = true;
            } else {
                if (owner.equalsIgnoreCase(player.getName())) {
                    allowed = true;
                } else if (gid != 0 && (gid == this.groupHandler.getGroupIDFromName(owner))) {
                    allowed = true;
                }
            }

            if (allowed) {
                if (!addHanging(entity, player)) {
                    event.setCancelled(true);
                    return;
                }
            } else {
                player.sendMessage(ChatColor.RED + owner + " eier blokken du prøvde plassere et painting/itemframe på.");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        RemoveCause rc = event.getCause();
        Entity e = event.getEntity();

        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(e.getWorld());

        if (rc == RemoveCause.PHYSICS) {
            if (wcfg.protectBlocks) {
                String owner = this.blockInfo.getOwner(e.getLocation());
                if (owner != null) {
                    event.setCancelled(true);
                }
            }
        } else if (rc == RemoveCause.EXPLOSION) {
            event.setCancelled(true);
        } else if (rc == RemoveCause.OBSTRUCTION) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Hanging hang = event.getEntity();
        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();
            Location loc = hang.getLocation();

            ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
            ConfigurationWorld wcfg = cfg.get(player.getWorld());

            if (wcfg.protectBlocks) {
                String owner = this.blockInfo.getOwner(loc);
                if (owner != null) {
                    int gid = this.groupHandler.getGroupID(player);
                    int owner_gid = this.groupHandler.getGroupIDFromName(owner);
                    if (owner.equalsIgnoreCase(player.getName())) {
                        handleRemovedHanging(player, hang, true);
                    } else if (gid != 0 && gid == owner_gid) {
                        handleRemovedHanging(player, hang, true);
                    } else {
                        handleNotOwnedHanging(player, hang, owner);
                        event.setCancelled(true);
                    }
                } else {
                    handleRemovedHanging(player, hang, false);
                }
            } else {
                handleRemovedHanging(player, hang, false);
            }
        } else {
            // Don't let other entities remove paintings/itemframes.
            event.setCancelled(true);
        }
    }

    private boolean handleNotOwnedHanging(Player player, Hanging hang, String owner) {
        String what = hang.getType().toString();
        String grama1 = "";
        String grama2 = "";
        if (what.equalsIgnoreCase(Material.PAINTING.toString())) {
            grama1 = "dette maleriet";
            grama2 = "maleriet";
        } else if (what.equalsIgnoreCase(Material.ITEM_FRAME.toString())) {
            grama1 = "denne item framen";
            grama2 = "item framen";
        } else if (what.equalsIgnoreCase(Material.ITEM_FRAME.toString())) {
            grama1 = "denne leaden";
            grama2 = "leaden";
        }
        if (!owner.equalsIgnoreCase(UserHandler.SERVER_USERNAME) && !owner.equalsIgnoreCase("hardwork")) {
            player.sendMessage(ChatColor.RED + owner + " eier " + grama1 + ". Om dere samarbeider kan");
            player.sendMessage(ChatColor.RED + "dere lage en gruppe, se /help for mer informasjon.");
            player.sendMessage(ChatColor.RED + "Dersom " + grama2 + " ikke skulle være her, kontakt en vakt eller stab.");
        } else {
            player.sendMessage(ChatColor.RED + "En vakt eller stab har beskyttet " + grama1 + ".");
            player.sendMessage(ChatColor.RED + "Om du mener den er din, eller den skal fjernes, kontakt en vakt eller stab.");
        }
        return true;
    }

    private boolean handleRemovedHanging(Player player, Hanging hang, boolean isProtected) {
        int typeid = getTypeEntity(hang.getType());
        if (typeid != -1) {
            this.blockHandler.setBlocklog(this.userHandler.getUserId(player), hang.getLocation(), typeid, BlockLogReason.FJERNET);

            if (isProtected) {
                this.blockHandler.deleteBlockProtection(hang.getLocation());
            }
            return true;
        } else {
            // This should not happend since this HangingEvent only apply to
            // Painting and ItemFrame.
            player.sendMessage(ChatColor.RED + "Det du prøve sette ut er ikke støttet i vårt BP system, kontakt en vakt/stab om den burde bli støttet.");
        }
        return false;
    }

    private boolean addHanging(Entity entity, Player player) {
        int typeid = getTypeEntity(entity.getType());
        if (typeid != -1) {
            int id = this.userHandler.getUserId(player);
            this.blockHandler.setBlockProtection(id, entity.getLocation());
            this.blockHandler.setBlocklog(id, entity.getLocation(), typeid, BlockLogReason.PLASSERTE);
            return true;
        } else {
            // This should not happend since this HangingEvent only apply to
            // Painting and ItemFrame.
            player.sendMessage(ChatColor.RED + "Det du prøve sette ut er ikke støttet i vårt BP system, kontakt en vakt/stab om den burde bli støttet.");
            return false;
        }
    }

    /**
     * Returns the material ID of this entity.
     *
     * @param en EntityType
     *
     * @return -1 if not supported by Hanging BP.
     */
    private int getTypeEntity(EntityType en) {
        if (en == EntityType.PAINTING) {
            return Material.PAINTING.getId();
        } else if (en == EntityType.ITEM_FRAME) {
            return Material.ITEM_FRAME.getId();
        } else if (en == EntityType.LEASH_HITCH) {
            return Material.LEASH.getId();
        } else {
            return -1;
        }
    }
}