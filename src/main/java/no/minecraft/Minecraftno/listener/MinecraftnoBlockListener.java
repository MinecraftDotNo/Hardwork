package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.blocks.PrivateProtectionHandler;
import no.minecraft.Minecraftno.handlers.enums.BlockLogReason;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.List;

public class MinecraftnoBlockListener implements Listener {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final GroupHandler groupHandler;
    private final BlockHandler blockHandler;
    private final BlockInfoHandler blockinfoHandler;
    private PrivateProtectionHandler privateHandler;

    public MinecraftnoBlockListener(Minecraftno instance) {
        this.plugin = instance;

        this.userHandler = instance.getUserHandler();
        this.blockHandler = instance.getBlockHandler();
        this.groupHandler = instance.getGroupHandler();
        this.blockinfoHandler = instance.getBlockInfoHandler();
        this.privateHandler = instance.getPrivateProtectionHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockMelt(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();

		/*if ((getx >= -63) && (getx <= -48) && (getz >= -27) && (getz <= -12)) {
            if ((wcfg.MagicMachine)) {
				MagicMachineHandler.magicMachin(blockFrom);
			}
		}
		
		else if ((getx >= 455) && (getx <= 478) && (getz <= -210) && (getz >= -234)) {
			if ((wcfg.MagicMachine)) {
				MagicMachineHandler.magicMachin(blockFrom);
			}
		}*/

        if (this.blockinfoHandler.getOwnerId(event.getToBlock()) != 0) {
            event.setCancelled(true);
        }

        if (cfg.noRemoveFromWaterLava.contains(event.getToBlock().getTypeId())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.MONSTER_EGG) {
            event.setCancelled(true);
        }

        if (item.getType() == Material.EGG) {
            event.setCancelled(true);
        }

        if (item.getType() == Material.FIREBALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());
        Block block = event.getBlock();

        if ((block.getType() == Material.SAND) || (block.getType() == Material.GRAVEL) && (!wcfg.FysikkSandGravel)) {
            event.setCancelled(true);
            return;
        }

        if (event.getChangedType() != Material.AIR) {
            if ((block.getType() == Material.CACTUS) && (!wcfg.CactusCanGrow)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getChangedType() == Material.GLASS) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.RAILS || block.getType() == Material.TORCH) {
            if (block.getRelative(BlockFace.DOWN).getType() == Material.GLASS) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());
        Player player = event.getPlayer();
        Block block = event.getBlock();

        int OwnerId = this.blockinfoHandler.getOwnerId(block);

        if (player.getWorld().getEnvironment() == Environment.NETHER) {
            if (OwnerId == 0) {
                if (block.getType() == Material.NETHER_BRICK || block.getType() == Material.NETHER_FENCE || block.getType() == Material.NETHER_BRICK_STAIRS || block.getType() == Material.MOB_SPAWNER) {
                    player.sendMessage(ChatColor.RED + "Denne blokken er beskyttet av serveren. Kontakt en vakt/stab dersom blokken skal fjernes.");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        Block down = block.getRelative(BlockFace.DOWN);
        Block up = block.getRelative(BlockFace.UP);
        int groupId = this.groupHandler.getGroupID(player);

        if (wcfg.protectBlocks) {
            if (this.userHandler.getAccess(player) == 0) {
                event.setCancelled(true);
                return;
            } else {
                if (down != null) {
                    int downOwnerId = this.blockinfoHandler.getOwnerId(down);
                    if ((down.getType() == Material.WOODEN_DOOR) || (down.getType() == Material.IRON_DOOR_BLOCK)) {
                        // Private item?
                        if (this.privateHandler.handlePrivateBlockBreak(down, player)) {
                            event.setCancelled(true);
                            return;
                        } else {
                            if ((downOwnerId != 0) && (downOwnerId != this.userHandler.getUserId(player))) {
                                int groupIdBlock = this.groupHandler.getGroupIDFromUserId(downOwnerId);
                                if ((groupId != 0) && groupId != groupIdBlock) {
                                    event.setCancelled(true);
                                    player.sendMessage(ChatColor.RED + this.userHandler.getNameFromId(downOwnerId) + " eier blokken over som er beskyttet.");
                                    return;
                                }
                            }
                        }
                    } else if (down.getState() instanceof Sign) {
                        if (block.getType() == Material.CHEST) {
                            Sign signBlock = (Sign) block.getRelative(BlockFace.DOWN).getState();
                            if ((downOwnerId != 0) && (downOwnerId != OwnerId)) {
                                for (String lines : signBlock.getLines()) {
                                    if (lines.equalsIgnoreCase("[privat]")) {
                                        player.sendMessage(ChatColor.RED + "Denne kisten er privat, du kan ikke fjerne den.");
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                if (up != null) {
                    int upOwnerId = this.blockinfoHandler.getOwnerId(up);
                    if (cfg.protectBlockUp.contains(up.getTypeId())) {
                        if ((upOwnerId != 0) && (upOwnerId != this.userHandler.getUserId(player))) {
                            if ((groupId != 0) && (groupId != groupHandler.getGroupIDFromUserId(upOwnerId))) {
                                player.sendMessage(ChatColor.RED + this.userHandler.getNameFromId(upOwnerId) + " eier blokken over som er beskyttet.");
                                event.setCancelled(true);
                                return;
                            }
                        }
                    } else if ((block.getState() instanceof Sign) && (up.getState() instanceof Chest)) {
                        if ((upOwnerId != 0) && (upOwnerId != this.userHandler.getUserId(player))) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + this.userHandler.getNameFromId(upOwnerId) + " eier blokken under.");
                            return;
                        }
                    } else if (block.getType() == Material.NETHERRACK && block.getRelative(BlockFace.UP).getType() == Material.FIRE) {
                        String owner = this.blockinfoHandler.getOwner(block);
                        if (owner != null) {
                            player.sendMessage(ChatColor.RED + owner + " eier flammen over så derfor er blokken beskyttet.");
                            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                            event.setCancelled(true);
                            return;
                        } else {
                            this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block.getRelative(BlockFace.UP), BlockLogReason.FJERNET);
                        }
                    }
                }

                if (block.getType() == Material.GLOWSTONE && player.getWorld().getEnvironment() != Environment.NETHER) {
                    if (OwnerId == 0) {
                        player.sendMessage(ChatColor.RED + "Denne blokken er beskyttet av serveren. Kontakt en vakt/stab dersom blokken skal fjernes.");
                        event.setCancelled(true);
                        return;
                    }
                } else if (block.getType() == Material.ICE && !player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                    // prevent users from mining ice blocks without silk touch. This prevents water when breaking a block!
                    player.sendMessage(ChatColor.RED + "Denne blokken kan kun fjernes med silk touch.");
                    event.setCancelled(true);
                    return;
                } else if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) {
                    // Private item?
                    if (this.privateHandler.handlePrivateBlockBreak(block, player)) {
                        event.setCancelled(true);
                        return;
                    }
                } else if ((block.getType() == Material.PISTON_EXTENSION) || (block.getType() == Material.PISTON_MOVING_PIECE)) {
                    event.setCancelled(true);
                    return;
                }

                if (block.getType() == Material.CHEST) {
                    // Private item? (Both part of the chests are in DB.)
                    if (this.privateHandler.handlePrivateBlockBreak(block, player)) {
                        event.setCancelled(true);
                        return;
                    }

                    final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
                    for (BlockFace face : FACES) {
                        Block other = block.getRelative(face);
                        if (other.getType() == Material.CHEST) {
                            if (other.getRelative(BlockFace.DOWN).getState() instanceof Sign) {
                                Sign signBlock = (Sign) other.getRelative(BlockFace.DOWN).getState();
                                int SignOwner = this.blockinfoHandler.getOwnerId(other.getRelative(BlockFace.DOWN));
                                if (SignOwner != 0 && SignOwner != OwnerId) {
                                    for (String lines : signBlock.getLines()) {
                                        if (lines.equalsIgnoreCase("[privat]")) {
                                            player.sendMessage(ChatColor.RED + "Denne kisten er privat, du kan ikke fjerne den.");
                                            event.setCancelled(true);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (OwnerId != 0) {
                    if (OwnerId == this.userHandler.getUserId(player)) {
                        if (handleBlockRemoved(player, block, block.getWorld(), true)) {
                            event.setCancelled(true);
                        }
                    } else if (groupId != 0 && groupId == this.groupHandler.getGroupIDFromUserId(OwnerId)) {
                        if (handleBlockRemoved(player, block, block.getWorld(), true)) {
                            event.setCancelled(true);
                        }
                    } else {
                        if (handleBlockNotOwned(player, block, OwnerId)) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    if (cfg.protectUnprotected.contains(block.getTypeId())) {
                        player.sendMessage(ChatColor.RED + "Denne blokken blir ansett som verdifull og er beskyttet.");
                        player.sendMessage(ChatColor.RED + "Kontakt en vakt/stab dersom blokken skal fjernes.");
                        event.setCancelled(true);
                        return;
                    }
                    // Remove the block
                    if (handleBlockRemoved(player, block, block.getWorld(), true)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (wcfg.logBlocks) {
                this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.FJERNET);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.userHandler.getAccess(player) == 0) {
            player.sendMessage(Minecraftno.notRegistredMessage());
            event.setBuild(false);
            event.setCancelled(true);
            return;
        } else if (cfg.illegalItems.contains(event.getBlockPlaced().getTypeId())) {
            if (this.userHandler.getAccess(player) < 3) {
                player.getInventory().remove(player.getItemInHand());
                this.plugin.getLogHandler().log(this.userHandler.getUserId(player), 0, 0, event.getBlockPlaced().getTypeId(), player.getLocation().toString(), MinecraftnoLog.ILLEGAL);
                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
        } else if (event.getBlockReplacedState().getType() == Material.FIRE) {
            int ownerId = this.blockinfoHandler.getOwnerId(block);
            if ((ownerId != 0) && (ownerId != this.userHandler.getUserId(player))) {
                event.setCancelled(true);
                return;
            }
        } else if (block.getType() == Material.ICE) {
            if (player.getWorld().getEnvironment() == Environment.NETHER) { // Prevent players from placing ice blocks in nether
                event.setCancelled(true);
                return;
            }
        } else if (block.getType() == Material.CHEST) {
            final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : FACES) {
                Block other = block.getRelative(face);
                if (other.getType() == Material.CHEST) {
                    int ownerId = this.blockinfoHandler.getOwnerId(other);
                    if ((ownerId != 0) && (ownerId != this.userHandler.getUserId(player))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        } else if (block.getType() == Material.HOPPER) {
            final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
            for (BlockFace face : FACES) {
                Block other = block.getRelative(face);
                if (other.getType() == Material.CHEST || other.getType() == Material.FURNACE || other.getType() == Material.DISPENSER || other.getType() == Material.TRAPPED_CHEST || other.getType() == Material.BREWING_STAND || other.getType() == Material.HOPPER) {
                    int ownerId = this.blockinfoHandler.getOwnerId(other);
                    if ((ownerId != 0) && (ownerId != this.userHandler.getUserId(player))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        } else if (block.getType() == Material.SPONGE && this.userHandler.getAccess(player) >= 2) {
            for (String logg : this.blockinfoHandler.getBlockLog(block, true)) {
                player.sendMessage(logg);
            }
            player.sendMessage("------------------ SLUTT -------------------");
            event.setBuild(false);
            event.setCancelled(true);
            return;
        } else if (event.getBlockReplacedState().getType() == Material.STEP) {
            Block getBlock = event.getBlockReplacedState().getBlock();
            int ownerId = this.blockinfoHandler.getOwnerId(getBlock);
            if ((ownerId != 0) && (ownerId != this.userHandler.getUserId(player))) {
                event.setCancelled(true);
                return;
            }
        } else if (block.getType() == Material.BED_BLOCK) {
            this.blockHandler.setBlockProtection(this.userHandler.getUserId(player), block.getRelative(new Bed(Material.BED, block.getData()).getFacing()));
        }

        if (block.getType() == Material.ANVIL) {
            if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
        } else if (event.getBlockReplacedState().getType() == Material.FIRE) {
            Block down = event.getBlockReplacedState().getBlock().getRelative(BlockFace.DOWN);
            if (down.getType() == Material.NETHERRACK) {
                String owner = this.blockinfoHandler.getOwner(block);
                if (owner != null) {
                    player.sendMessage(ChatColor.RED + owner + " eier blokken under så derfor er flammen beskyttet.");
                    player.sendBlockChange(block.getLocation(), Material.FIRE, new ItemStack(Material.FIRE, 1).getData().getData());
                    event.setCancelled(true);
                    return;
                } else {
                    this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.FJERNET);
                }
            }
        }

        // New Feature: http://bugs.minecraft.no/view.php?id=136
        // Player riding a minecart?
        if (player.isInsideVehicle()) {
            // What type?
            Entity vehicle = player.getVehicle();
            if (vehicle.getType() == EntityType.MINECART) {
                player.sendMessage(ChatColor.RED + "Du har ikke lov å plassere blokker imens du kjører minecart.");
                event.setCancelled(true);
            }
        }

        Block down = block.getRelative(BlockFace.DOWN);
        if (down != null && down.getType() == Material.RAILS) {
            // Owner of rail plz.
            String owner = this.blockinfoHandler.getOwner(down);
            if (owner != null && !owner.equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatColor.RED + owner + " eier railen under, så du har ikke lov å plassere en blokk over den.");
                event.setCancelled(true);
                return;
            }
        }

        this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.PLASSERTE);

        // Temp; Don't protect netherrack in nether.
        if (block.getWorld().getEnvironment() == Environment.NETHER && block.getType() == Material.NETHERRACK) {
            return;
        }

        if (cfg.nonProtectBlocks.contains(block.getTypeId()) && block.getData() == (byte) 0) {
            this.blockHandler.deleteBlockProtection(block);
        } else {
            this.blockHandler.setBlockProtection(this.userHandler.getUserId(player), block);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getNewState().getWorld());

        if ((event.getNewState().getType() == Material.ICE) || (event.getNewState().getType() == Material.SNOW) || (event.getNewState().getType() == Material.SNOW_BLOCK)) {
            if (!wcfg.regenIceSnow) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        World world = event.getBlock().getWorld();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        if (wcfg.protectBlocks) {
            if (this.blockinfoHandler.isProtected(event.getBlock())) {
                event.setCancelled(true);
                byte data = event.getBlock().getData();
                byte decayBit = 0x4;
                if ((data & 0x4) != 0) {
                    // Set the decay bit to avoid decay updates until an
                    // adjacent block is placed.
                    data &= ~decayBit;
                }
            }
        }

        if (!(wcfg.NoLeavesDecay)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());

        if ((event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) {
            if (wcfg.Lighter) {
                if (wcfg.Lighternetherrack && block.getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK) {
                    if (this.blockinfoHandler.getOwnerId(block.getRelative(BlockFace.DOWN)) != this.userHandler.getUserId(player)) {
                        player.sendMessage(ChatColor.RED + "Kun eier av blokken kan tenne på denne blokken.");
                        event.setCancelled(true);
                    } else {
                        return;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Du har ikke lov til å tenne på denne blokken." + (wcfg.Lighternetherrack ? " Du kan tenne på netherrack" : null));
                    event.setCancelled(true);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Du kan ikke bruker lighter i denne verden.");
                event.setCancelled(true);
            }
        } else if ((event.getCause() == BlockIgniteEvent.IgniteCause.LAVA) && (!wcfg.LavaFire)) {
            event.setCancelled(true);
        } else if ((event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) && (!wcfg.LightningFire)) {
            event.setCancelled(true);
        } else if ((event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) && (!wcfg.FireSpread)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());

        if (!wcfg.FireSpread) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //TODO
    //REMAKE IT!
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        ArrayList<Block> add = new ArrayList<Block>();
        ArrayList<Block> delete = new ArrayList<Block>();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());
        if (wcfg.protectBlocks) {
            List<Block> blocks = event.getBlocks();
            BlockFace forwardFace = (event.getDirection());
            BlockFace backFace = (event.getDirection().getOppositeFace());
            Block firstBlock = event.getBlock().getRelative(event.getDirection());
            int getBlockOwnerId = this.blockinfoHandler.getOwnerId(event.getBlock());
            int firstOwnerId = this.blockinfoHandler.getOwnerId(firstBlock);
            if (firstOwnerId != 0) {
                if (firstOwnerId == getBlockOwnerId) {
                    if ((firstBlock.getType() == Material.REDSTONE_WIRE) || (firstBlock.getType() == Material.REDSTONE) || (firstBlock.getType() == Material.DIODE_BLOCK_ON) || (firstBlock.getType() == Material.DIODE_BLOCK_OFF)) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            for (Block block : blocks) {
                int id = block.getTypeId();
                Block forwardBlock = block.getRelative(forwardFace);
                Block backBlock = block.getRelative(backFace);
                int forwardblockOwnerId = this.blockinfoHandler.getOwnerId(forwardBlock);
                if (forwardblockOwnerId != 0) {
                    if (forwardblockOwnerId == getBlockOwnerId) {
                        if ((forwardBlock.getType() == Material.REDSTONE_WIRE) || (forwardBlock.getType() == Material.REDSTONE) || (forwardBlock.getType() == Material.DIODE_BLOCK_ON) || (forwardBlock.getType() == Material.DIODE_BLOCK_OFF)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
                int blockOwnerId = this.blockinfoHandler.getOwnerId(block);
                if (blockOwnerId != 0) {
                    if (blockOwnerId == getBlockOwnerId) {
                        if (forwardBlock.getType() == Material.AIR) {
                            add.add(forwardBlock);
                        }
                        if ((backBlock.getType() == Material.PISTON_BASE)) {
                            delete.add(block);
                        }
                        if (!(this.blockinfoHandler.isProtected(backBlock))) {
                            delete.add(block);
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if ((id == 19) || (id == 20) || (id == 35) || (id == 41) || (id == 42) || (id == 45) || (id == 47) || (id == 57) || (id == 54) || (id == 55) || (id == 85) || (id == 89)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (forwardBlock.getType() == Material.AIR) {
                        delete.add(forwardBlock);
                    }
                    if ((this.blockinfoHandler.isProtected(backBlock))) {
                        add.add(block);
                    }
                }
            }
            for (Block block : delete) {
                this.blockHandler.deleteBlockProtection(block);
            }
            for (Block block : add) {
                this.blockHandler.setBlockProtection(getBlockOwnerId, block);
            }
            delete.clear();
            add.clear();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //TODO
    //REMAKE IT!
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        ArrayList<Block> add = new ArrayList<Block>();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getBlock().getWorld());
        if (wcfg.protectBlocks) {
            if (event.isSticky()) {
                Block block = event.getBlock().getRelative(event.getDirection()).getRelative(event.getDirection());
                Block firstblock = event.getBlock().getRelative(event.getDirection());
                int blockOwnerId = this.blockinfoHandler.getOwnerId(block);
                int getBlockOwnerId = this.blockinfoHandler.getOwnerId(event.getBlock());
                int id = block.getTypeId();
                if (blockOwnerId != 0) {
                    if (blockOwnerId == getBlockOwnerId) {
                        if (event.isSticky()) {
                            //this.delete.add(block);
                            add.add(firstblock);
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if ((id == 19) || (id == 20) || (id == 35) || (id == 41) || (id == 42) || (id == 45) || (id == 47) || (id == 57) || (id == 54) || (id == 55) || (id == 85) || (id == 89)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                for (Block addblock : add) {
                    this.blockHandler.setBlockProtection(getBlockOwnerId, addblock);
                }
                add.clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        /*
         * Handle private doors, don't let em' be affected by redstone.
		 */
        Block block = event.getBlock();
        if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) {
            if (this.privateHandler.isPrivateItem(block)) {
                event.setNewCurrent(0);
                return;
            }
        }
    }

    protected boolean handleBlockNotOwned(final Player player, final Block block, int ownerId) {
        player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        String owner = this.userHandler.getNameFromId(ownerId);
        if (!owner.equalsIgnoreCase(UserHandler.SERVER_USERNAME) && !owner.equalsIgnoreCase("hardwork")) {
            player.sendMessage(ChatColor.RED + owner + " eier denne blokken. Om dere samarbeider kan");
            player.sendMessage(ChatColor.RED + "dere lage en gruppe, se /help for mer informasjon.");
            player.sendMessage(ChatColor.RED + "Dersom blokken ikke skulle være her, kontakt en vakt eller stab.");
        } else {
            player.sendMessage(ChatColor.RED + "En vakt eller stab har beskyttet denne blokken.");
            player.sendMessage(ChatColor.RED + "Om du mener den er din, eller den skal fjernes, kontakt en vakt eller stab.");
        }
        return true;
    }

    protected boolean handleBlockRemoved(final Player player, final Block block, final World world, boolean isProtected) {
        boolean truefalse = false;
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.FJERNET);

        if (isProtected) {
            this.blockHandler.deleteBlockProtection(block);
        }

		/*if (!wasLogged || !protectionUpdated) {
			player.sendMessage(ChatColor.RED + "En feil oppsto i blokkfjerningsfunksjonen, kontakt stab.");
			event.setCancelled(true);
			return;
		}*/

        if (!(wcfg.NoDrop)) {
            if (block.getType() == Material.GLASS) {
                Location loc = block.getLocation();
                block.getWorld().dropItemNaturally(loc, new ItemStack(Material.GLASS, 1));
                block.setType(Material.AIR);
                truefalse = true;
            } else if (block.getType() == Material.STAINED_GLASS) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STAINED_GLASS, 1, block.getData()));
                block.setType(Material.AIR);
                truefalse = true;
            } else if (block.getType() == Material.THIN_GLASS) {
                Location loc = block.getLocation();
                block.getWorld().dropItemNaturally(loc, new ItemStack(Material.THIN_GLASS, 1));
                block.setType(Material.AIR);
                truefalse = true;
            } else if (block.getType() == Material.STAINED_GLASS_PANE) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STAINED_GLASS_PANE, 1, block.getData()));
                block.setType(Material.AIR);
                truefalse = true;
            } else if (block.getType() == Material.DOUBLE_STEP) {
                Location loc = block.getLocation();
                block.getWorld().dropItemNaturally(loc, new ItemStack(Material.STEP, 2, block.getData()));
                block.setType(Material.AIR);
                truefalse = true;
            } else if (block.getType() == Material.GLOWSTONE) {
                if (!player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                    Location loc = block.getLocation();
                    block.getWorld().dropItemNaturally(loc, new ItemStack(Material.GLOWSTONE, 1));
                    block.setType(Material.AIR);
                    truefalse = true;
                }
            }
        } else {
            block.setTypeId(0);
        }
        return truefalse;
    }

    public static boolean isBlockWaterAndLava(World world, int ox, int oy, int oz) {
        Block block = world.getBlockAt(ox, oy, oz);
        int id = block.getTypeId();
        return (id == 8) || (id == 9) || (id == 10) || (id == 11);
    }
}
