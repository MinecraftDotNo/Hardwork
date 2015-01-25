package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.commands.WorkCommand;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinecraftnoEntityListener implements Listener {

    private final Minecraftno plugin;
    private final GroupHandler groupHandler;
    private final UserHandler userHandler;
    private final BlockInfoHandler blockInfo;
    private final BlockHandler blockHandler;

    public MinecraftnoEntityListener(Minecraftno instance) {
        this.plugin = instance;
        this.groupHandler = instance.getGroupHandler();
        this.userHandler = instance.getUserHandler();
        this.blockInfo = instance.getBlockInfoHandler();
        this.blockHandler = instance.getBlockHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) {
            PlayerDeathEvent e = (PlayerDeathEvent) event;
            e.setDeathMessage(null);
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        /* Remove BP on first block of tree */
        List<BlockState> blocks = event.getBlocks();
        if (blocks.size() >= 1) {
            if (blocks.get(0).getBlock() instanceof Block) {
                Block bottomBlock = blocks.get(0).getBlock();
                if (bottomBlock.getType() == Material.LOG) {
                    this.blockHandler.deleteBlockProtection(bottomBlock);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        World world = event.getEntity().getWorld();
        Entity attacker = null;
        Entity defender = event.getEntity();
        DamageCause type = event.getCause();

        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        /*
         * - Player gets first priority.
         * - Do not just return inside event as we might need to check
         *   more damage stuff. Only time to use return is when event is
         *   cancelled.
         */

        if (event.getEntity().getWorld().getSpawnLocation().distance(event.getEntity().getLocation()) < 10) {
            event.setCancelled(true);
            return;
        }

        if (type == DamageCause.STARVATION) {
            event.setCancelled(true);
        }
        
        // Player as defender must get first priority!
        if (defender instanceof Player) {            
            Player player = (Player) defender;
            if (this.userHandler.getAccess(player) > 2) {
                if (WorkCommand.isInWork(player)) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            if (!wcfg.pvpWorld && !wcfg.PVPgroups) {
                if (world.getEnvironment() != Environment.NETHER) {
                    if (defender instanceof Player) {
                        if (type == DamageCause.VOID) {
                            Location loc = defender.getLocation();
                            loc.setWorld(Bukkit.getServer().getWorlds().get(0));
                            loc.setY(320D);
                            event.setCancelled(true);
                            defender.teleport(loc);
                            return;
                        }
                        event.setCancelled(true);
                        return;
                    }
                    
                    if (event instanceof EntityDamageByEntityEvent) {
                        event.setCancelled(true);
                        return;
                    }
                }          
            }
        }

        if (defender instanceof Entity) {
            Entity entity = defender;

            if (attacker instanceof Player) {
                Player player = (Player) attacker;
                if (entity.getType() == EntityType.ITEM_FRAME) {
                    String ownerofFrame = this.blockInfo.getOwner(entity.getLocation());
                    if (ownerofFrame != null) {
                        int grOwner = this.groupHandler.getGroupIDFromName(ownerofFrame);
                        if (!((ownerofFrame.equalsIgnoreCase(player.getName())) || (grOwner != 0 && grOwner == this.groupHandler.getGroupID(player)))) {
                            player.sendMessage(ChatColor.RED + ownerofFrame + " eier denne itemframen og er derfor beskyttet.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }    
            }

            // Handle horse damage.
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                attacker = subEvent.getDamager();
                
                if (attacker.getType() == EntityType.HORSE) {
                    Horse horsy = (Horse) entity;
                    
                    if (this._resolveHorseDamage(horsy, type, attacker)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                if (entity.getType() == EntityType.HORSE) {
                    Horse horsy = (Horse) entity;
                    if (this._resolveHorseDamage(horsy, type, attacker)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        // PvP world
        if (!wcfg.pvpWorld) {
            if ((!wcfg.Damage) || (!wcfg.PVPDamage)) {
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                    attacker = subEvent.getDamager();
                    if ((defender instanceof Player) && (!wcfg.PVPDamage) && (attacker instanceof Player) || (defender instanceof Player) && (attacker instanceof Creature || attacker instanceof Monster) && (!wcfg.CreatureDamage)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        } else if ((wcfg.pvpWorld) && (wcfg.PVPgroups)) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                attacker = subEvent.getDamager();

                Player pDefender = (Player) defender;
                Player pAttacker = (Player) attacker;

                if ((defender instanceof Player) && (attacker instanceof Player)) {
                    int defGroupID = this.groupHandler.getGroupID(pDefender);
                    int attGroupID = this.groupHandler.getGroupID(pAttacker);

                    if (defGroupID == attGroupID) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Resolve horse damage, will return true if event should be cancelled.
     * @param horsy
     * @param cause
     * @param attacker
     * @return true if cancel event, false don't.
     */
    protected boolean _resolveHorseDamage(Horse horsy, DamageCause cause, Entity attacker)
    {
    	if (!horsy.isTamed() || horsy.getOwner() == null) {
    		return false;
    	}
    	
    	switch (cause) {
    	case FALL:
    	case FALLING_BLOCK:
    	case CONTACT:
    	case FIRE:
    	case FIRE_TICK:
    	case LAVA:
    	case PROJECTILE:
    	case DROWNING:        	
        	return true;
    	case ENTITY_ATTACK:    		
    		if (!(attacker instanceof Player)) return true;
        	
        	Player pl = (Player) attacker;
        	
        	String owner = horsy.getOwner().getName();
        	if (!owner.equalsIgnoreCase(pl.getName())) {
        		pl.sendMessage(ChatColor.RED + "Denne hesten er eid av " + ChatColor.WHITE + owner + ChatColor.RED + " så du kan ikke drepe den.");
        		return true;
        	}
        	
    		return false;
    	default:
    		return false;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Location l = event.getLocation();
        Entity e = event.getEntity();
        World world = l.getWorld();

        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        if (e instanceof LivingEntity) {
            if ((e instanceof Creeper) && (!wcfg.CreeperBlockDamage)) {
                event.setCancelled(true);
            }
        } else {
            if (!wcfg.TNT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity e = event.getEntity();
        Block b = event.getBlock();

        if (e.getType() == EntityType.BOAT) {
            if (this.blockInfo.isProtected(b)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        if (getCreatureAmount(event.getEntity().getWorld()) >= wcfg.MaxEntities) {
            event.setCancelled(true);
        }

        if (event.getEntity().getWorld().getSpawnLocation().distance(event.getEntity().getLocation()) < 20) {
            event.setCancelled(true);
        }

        /**if (wcfg.MaxEntities > -1 && (getCreatureAmount(event.getEntity().getWorld()) > wcfg.MaxEntities + 10)) {
         List<LivingEntity> creature = getCreatureList(event.getEntity().getWorld());
         for (int i=0; ((i < creature.size()) && (creature.size() > wcfg.MaxEntities)); i++) {
         if (Creature.class.isInstance(creature.get(i))) {
         creature.get(i).remove();
         }
         }
         event.setCancelled(true);
         return;
         }**/

        EntityType type = event.getEntityType();
        if (type == EntityType.CHICKEN && !wcfg.chicken) {
            event.setCancelled(true);
        } else if (type == EntityType.COW && !wcfg.cow) {
            event.setCancelled(true);
        } else if (type == EntityType.CREEPER) {
            if (!wcfg.creeper) {
                event.setCancelled(true);
            }
            if (wcfg.supercreepers) {
                Entity entity = event.getEntity();
                ((Creeper) entity).setPowered(true);
            }
        } else if (type == EntityType.GHAST && !wcfg.ghast) {
            event.setCancelled(true);
        } else if (type == EntityType.GIANT && !wcfg.giant) {
            event.setCancelled(true);
        } else if (type == EntityType.PIG && !wcfg.pig) {
            event.setCancelled(true);
        } else if (type == EntityType.PIG_ZOMBIE && !wcfg.pig_zombie) {
            event.setCancelled(true);
        } else if (type == EntityType.SHEEP && !wcfg.sheep) {
            event.setCancelled(true);
        } else if (type == EntityType.SKELETON && !wcfg.skeleton) {
            event.setCancelled(true);
        } else if (type == EntityType.SLIME && !wcfg.slime) {
            event.setCancelled(true);
        } else if (type == EntityType.SPIDER && !wcfg.spider) {
            event.setCancelled(true);
        } else if (type == EntityType.SQUID && !wcfg.squid && (event.getEntity().getLocation().getBlockY() < 60)) {
            event.setCancelled(true);
        } else if (type == EntityType.ZOMBIE && !wcfg.zombie) {
            event.setCancelled(true);
        } else if (type == EntityType.WOLF && !wcfg.wolf) {
            event.setCancelled(true);
        } else if (type == EntityType.CAVE_SPIDER && !wcfg.cave_spider) {
            event.setCancelled(true);
        } else if (type == EntityType.ENDERMAN && !wcfg.enderman) {
            event.setCancelled(true);
        } else if (type == EntityType.SILVERFISH && !wcfg.silverfish) {
            event.setCancelled(true);
        } else if (type == EntityType.ENDER_DRAGON && !wcfg.ender_dragon) {
            event.setCancelled(true);
        } else if (type == EntityType.VILLAGER && !wcfg.villager) {
            event.setCancelled(true);
        } else if (type == EntityType.BLAZE && !wcfg.blaze) {
            event.setCancelled(true);
        } else if (type == EntityType.MUSHROOM_COW && !wcfg.mushroom_cow) {
            event.setCancelled(true);
        } else if (type == EntityType.SNOWMAN && !wcfg.snowman) {
            event.setCancelled(true);
        } else if (type == EntityType.WITHER && !wcfg.wither) {
            event.setCancelled(true);
        } else if (type == EntityType.WITCH && !wcfg.witch) {
            event.setCancelled(true);
        } else if (type == EntityType.BAT && !wcfg.bats) {
            event.setCancelled(true);
        } else if (type == EntityType.MAGMA_CUBE && !wcfg.magmaCube) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
            ConfigurationWorld wcfg = cfg.get(player.getWorld());
            if (!wcfg.itemDurability) {
                if (cfg.noDamageTools.contains(event.getBow().getTypeId())) {
                    if (event.getBow().getDurability() >= 20) {
                        event.getBow().setDurability((short) -200);
                    }
                }
            }
        }
    }

    public int getCreatureAmount(World world) {
        int amount = 0;

        Iterator<LivingEntity> creature = world.getLivingEntities().iterator();
        while (creature.hasNext()) {
            if (Creature.class.isInstance(creature.next())) {
                amount++;
            }
        }
        return amount;
    }

    public synchronized List<LivingEntity> getCreatureList(World world) {
        List<LivingEntity> cr = new ArrayList<LivingEntity>();
        Iterator<LivingEntity> creature = world.getLivingEntities().iterator();
        while (creature.hasNext()) {
            if (Creature.class.isInstance(creature.next())) {
                cr.add(creature.next());
            }
        }
        return cr;
    }
}