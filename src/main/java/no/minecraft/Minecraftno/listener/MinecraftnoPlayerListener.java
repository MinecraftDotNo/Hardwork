package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.GroupHandler;
import no.minecraft.Minecraftno.handlers.WarningHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockHandler;
import no.minecraft.Minecraftno.handlers.blocks.BlockInfoHandler;
import no.minecraft.Minecraftno.handlers.blocks.PrivateProtectionHandler;
import no.minecraft.Minecraftno.handlers.data.BanData;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.enums.BlockLogReason;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.player.ChatHandler;
import no.minecraft.Minecraftno.handlers.player.NisseHandler;
import no.minecraft.Minecraftno.handlers.player.Teleporter;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.BlockIterator;
import org.jibble.pircbot.Colors;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MinecraftnoPlayerListener implements Listener {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final BlockInfoHandler blockinfoHandler;
    private final BlockHandler blockHandler;
    private final GroupHandler groupHandler;
    private final WarningHandler warningHandler;
    private final ChatHandler chatHandler;
    private final PrivateProtectionHandler privateHandler;

    private ConcurrentHashMap<String, ArrayDeque<Long>> quittime = new ConcurrentHashMap<String, ArrayDeque<Long>>();
    private List<String> kickedplayers = Collections.synchronizedList(new ArrayList<String>());

    private final Map<String, Integer> flyCount = new HashMap<>();
    private final Map<String, Long> flyCooldown = new HashMap<>();
    private final Map<String, Integer> flyKickCount = new HashMap<>();

    public MinecraftnoPlayerListener(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.blockinfoHandler = instance.getBlockInfoHandler();
        this.blockHandler = instance.getBlockHandler();
        this.groupHandler = instance.getGroupHandler();
        this.warningHandler = instance.getWarningHandler();
        this.chatHandler = instance.getChatHandler();
        this.privateHandler = instance.getPrivateProtectionHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.userHandler.getFreeze(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getPlayer().getWorld());

        if ((wcfg.bucket) || (this.userHandler.getAccess(player) >= 1)) {
            this.blockHandler.setBlocklog(this.userHandler.getUserId(player), event.getBlockClicked(), BlockLogReason.BUCKETTAKE);
        } else {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + " Kun brukere kan fylle bøtter i denne verdenen.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(event.getPlayer().getWorld());

        if ((wcfg.NoDrop)) {
            event.setCancelled(true);
            return;
        }

        int access = this.userHandler.getAccess(player);

        if (access == 0) {
            player.sendMessage(Minecraftno.notRegistredMessage());
            event.setCancelled(true);
            return;
        } else if (access <= 2) {
            if (cfg.illegalItems.contains(event.getItemDrop().getItemStack().getTypeId())) {
                player.sendMessage(Minecraftno.notAllowedItemMessage());
                event.getItemDrop().remove();
                event.setCancelled(true);
                this.plugin.getLogHandler().log(this.userHandler.getUserId(player), 0, 0, event.getItemDrop().getItemStack().getTypeId(), player.getLocation().toString(), MinecraftnoLog.ILLEGAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        Player player = event.getPlayer();
        int access = this.userHandler.getAccess(player);
        if (access == 0) {
            event.setCancelled(true);
            return;
        } else if (this.userHandler.getInvisible(player)) {
            event.setCancelled(true);
            return;
        } else if (cfg.illegalItems.contains(Integer.valueOf(event.getItem().getItemStack().getTypeId()))) {
            if (access <= 2) {
                player.sendMessage(Minecraftno.notAllowedItemMessage());
                event.getItem().remove();
                this.plugin.getLogHandler().log(this.userHandler.getUserId(player), 0, 0, event.getItem().getItemStack().getTypeId(), player.getLocation().toString(), MinecraftnoLog.ILLEGAL);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity e = event.getRightClicked();

        World world = player.getWorld();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(world);

        if (this.userHandler.getAccess(player) == 0) {
            if (!(e.getType() == EntityType.MINECART || e.getType() == EntityType.BOAT)) {
                event.setCancelled(true); // BugFix: http://bugs.minecraft.no/view.php?id=131
                return;
            }
        }

        if (e.getType() == EntityType.PAINTING || e.getType() == EntityType.ITEM_FRAME) {
            if (player.getItemInHand().getType() == Material.STICK) {
                if (this.userHandler.getAccess(player) > 2 && wcfg.adminStick) {
                    event.setCancelled(true);
                    this.blockHandler.setBlocklog(this.userHandler.getUserId(player), e.getLocation(), (e.getType() == EntityType.PAINTING ? Material.PAINTING.getId() : Material.ITEM_FRAME.getId()), BlockLogReason.ADMINSTICKED);
                    this.blockHandler.deleteBlockProtection(e.getLocation());
                    e.remove();
                    return;
                }
            } else if (player.getItemInHand().getType() == Material.WATCH) {
                if (this.userHandler.getAccess(player) >= 2 && !player.isSneaking()) {
                    ArrayList<String> blockLog = this.blockinfoHandler.getBlockLog(e.getLocation(), true);
                    if (blockLog != null) {
                        for (String logg : blockLog) {
                            player.sendMessage(logg);
                        }
                    }
                    if (wcfg.protectBlocks) {
                        String owner = this.blockinfoHandler.getOwner(e.getLocation());
                        String owner_string = "ingen eier";
                        if (owner != null) {
                            owner_string = owner;
                        }
                        player.sendMessage("Eier av blokken: " + ChatColor.BLUE + owner_string);
                        player.sendMessage("------------------ SLUTT -------------------");
                    } else {
                        player.sendMessage("------------------ SLUTT -------------------");
                    }
                    event.setCancelled(true);
                    return;
                }
            } else {
                String owner = this.blockinfoHandler.getOwner(e.getLocation());
                if (owner != null) {
                    if (!owner.equalsIgnoreCase(player.getName())) {
                        int gid = this.groupHandler.getGroupID(player);
                        if (gid == 0 || gid != this.groupHandler.getGroupIDFromName(owner)) {
                            // if not in group or not in same group
                            player.sendMessage(ChatColor.RED + "Du kan ikke endre på denne item framen fordi den eies ikke av deg.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        } else if (e.getType() == EntityType.HORSE) {   
            Horse h = (Horse) e;
            String owner = "ingen";
            if (h.isTamed() && h.getOwner() != null) {
                owner = h.getOwner().getName();
            }
            
        	if (player.getItemInHand().getType() == Material.WATCH) {
                player.sendMessage("Eier av hesten: " + ChatColor.BLUE + owner);
        		event.setCancelled(true);
                return;
        	}
        	
        	if (!owner.equalsIgnoreCase("ingen") && !owner.equalsIgnoreCase(player.getName())) {
        		player.sendMessage(ChatColor.RED + "Denne hesten er eid av " + ChatColor.WHITE + owner + ChatColor.RED + " så du kan ikke interakte med den.");
        		event.setCancelled(true);
        		return;
        	}
        }

        int wolves = 0;
        boolean hastoomany = false;

        if (player.getItemInHand().getDurability() >= 20) {
            player.getItemInHand().setDurability((short) -200);
        }

        if (!wcfg.restrictNumberOfWolfs) {
            if (e instanceof Wolf) {
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity instanceof Wolf) {
                        Wolf wolf = (Wolf) entity;
                        if (wolf.getOwner() == player) {
                            if (wolves > wcfg.maximumNumberOfWolfs) {
                                wolf.setTamed(false);
                                wolf.setSitting(false);
                                hastoomany = true;
                            }
                        } else if (!wolf.isTamed()) {
                            wolf.setSitting(false);
                        }
                        wolves++;
                    }
                }
                if (hastoomany) {
                    player.sendMessage(ChatColor.RED + "Det er en grense på to ulver per bruker på denne serveren.");
                    player.sendMessage(ChatColor.RED + "Hvis du har fler, blir disse ville nå.");
                }
            }
        }
    }

    private void adduser(Player player) {
        String msg = "Gratulerer med fullført registrering! Du er nå bruker på Hardwork!";
        String msg1 = "Som bruker på Hardwork så kan du samle materialer og bygge. Det finnes mange flotte byer og bli en del av også. For nye brukere har vi også nybegynnerbyen Poli som er perfekt for alle som er ferske til Hardwork.";
        String msg2 = "Be en vakt om å få en tomt her. I Poli har du tilgang på fine tomter, farmer og togbanesystem til andre plasser. Poli har også warp";
        String msg3 = "Husk at vi forventer at alle nye brukere skal kunne reglene på serveren, så ta en ekstra titt i brukerguiden hvis du er usikker på noe. Spør du veldig mye i chat om ting fra brukerguiden så kan du fort bli demoted til gjestestatus igjen";
        String msg4 = "Lykke til på Hardwork og hjertelig velkommen!";

        if (this.userHandler.getAccess(player.getName()) == 0) {
            if (this.userHandler.changeAccessLevel(player, 1)) {
                player.sendMessage(ChatColor.GOLD + msg);
                player.sendMessage(ChatColor.GREEN + msg1);
                player.sendMessage(ChatColor.GREEN + msg2);
                player.sendMessage(ChatColor.GREEN + msg3);
                player.sendMessage(ChatColor.BLUE + msg4);
                for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                    if (entry.getValue().getAnnonseringer()) {
                        entry.getKey().sendMessage("Velkommen til Hardwork, " + ChatColor.DARK_GREEN + player.getName() + "!");
                    }
                }
                if (this.userHandler.isRegPlayer(player)) {
                    this.userHandler.removeRegPlayer(player);
                    for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                        target.showPlayer(player);
                    }
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        Block block = event.getClickedBlock();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(player.getWorld());

        if (itemInHand.getType() == Material.FIREBALL || itemInHand.getType() == Material.TNT) {
            // Mideltidlig.
            event.setCancelled(true);
            return;
        }

        if (!player.isSneaking() && event.getAction() != Action.PHYSICAL && wcfg.allowUseCompass) {
            if (itemInHand.getType() == Material.COMPASS) {
                useCompass(player);
                event.setCancelled(true);
                return;
            }
        }

        if (!wcfg.itemDurability) {
            if (cfg.noDamageTools.contains(itemInHand.getTypeId())) {
                itemInHand.setDurability((short) -200);
            }
        }

        // Hm, vi gjør det litt diffrent. En rask sjekk!!!
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block fire = block.getRelative(event.getBlockFace());
            if (fire.getType() == Material.FIRE && block.getType() == Material.NETHERRACK) {
                String owner = this.blockinfoHandler.getOwner(block);
                if (owner != null) {
                    player.sendMessage(ChatColor.RED + owner + " eier blokken under så derfor er flammen beskyttet.");
                    player.sendBlockChange(fire.getLocation(), fire.getType(), fire.getData());
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                    return;
                } else {
                    this.blockHandler.setBlocklog(this.userHandler.getUserId(player), fire, BlockLogReason.FJERNET);
                }
            }
        }

        if (block != null && event.getAction() == Action.PHYSICAL) {
            final Material mat = block.getType();
            if ((mat != null) && (mat == Material.STONE_PLATE || mat == Material.STONE_BUTTON || mat == Material.WOOD_PLATE || mat == Material.WOOD_BUTTON)) {
                BlockFace[] bf = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
                for (BlockFace face : bf) {
                    Block at = block.getRelative(face);
                    if (at.getType() == Material.WOODEN_DOOR || at.getType() == Material.IRON_DOOR_BLOCK) {
                        Block toCheck = null;
                        if (at.getRelative(BlockFace.UP).getType() != Material.WOODEN_DOOR && at.getRelative(BlockFace.UP).getType() != Material.IRON_DOOR_BLOCK) {
                            toCheck = at.getRelative(BlockFace.DOWN);
                        } else if (at.getRelative(BlockFace.DOWN).getType() != Material.WOODEN_DOOR && at.getRelative(BlockFace.DOWN).getType() != Material.IRON_DOOR_BLOCK) {
                            toCheck = at;
                        }
                        if (toCheck != null) {
                            if (this.privateHandler.allowedInteraction(toCheck, player)) {
                                player.sendMessage(ChatColor.RED + "Døren er privat. Den kan kun åpnes av eieren.");
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }
            }
        }

        ItemStack itemHand = event.getPlayer().getItemInHand();

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            if (player.isSneaking() && player.getItemInHand().getType() == Material.WATCH && wcfg.canChangeTime) {
                player.setPlayerTime(player.getPlayerTimeOffset() - 1000, true);
                event.setCancelled(true);
                return;
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.isSneaking() && player.getItemInHand().getType() == Material.WATCH && wcfg.canChangeTime) {
                player.setPlayerTime(player.getPlayerTimeOffset() + 1000, true);
                event.setCancelled(true);
                return;
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            switch (itemHand.getType()) {
                case SLIME_BALL: {
                    if (this.userHandler.getAccess(player) > 2) {
                        if (wcfg.protectBlocks) {
                            this.blockHandler.setBlockProtection(this.userHandler.getUserId(UserHandler.SERVER_USERNAME), block);
                            this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.PROTECTED);
                            player.sendMessage(ChatColor.DARK_GREEN + "Blokken er nå beskyttet.");
                            event.setCancelled(true);
                        } else {
                            player.sendMessage(ChatColor.RED + "Blokkbeskyttelsen er ikke på.");
                        }
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case PAPER: {
                    if (this.userHandler.getAccess(player) > 2) {
                        if (wcfg.protectBlocks) {
                            event.setCancelled(true);
                            String toolData = this.userHandler.getToolData(player);
                            if (toolData != null) {
                                int playerId = 0;
                                Player victim = plugin.playerMatch(toolData);
                                if (victim != null) {
                                    playerId = this.userHandler.getUserId(victim);
                                } else {
                                    playerId = this.userHandler.getUserId(toolData);
                                }
                                if (playerId != -1) {
                                    this.blockHandler.setBlockProtection(this.userHandler.getUserId(victim), block);
                                    this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.CHANGEOWNER);
                                    player.sendMessage(ChatColor.DARK_GREEN + "Blokken eies nå av: " + ChatColor.WHITE + toolData);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Brukeren du prøver å sette eierskap til eksisterer ikke.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Blokkbeskyttelsen er ikke på.");
                        }
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                default: {
                    break;
                }
            }

            // Doors, handle private item.
            Block down = block.getRelative(BlockFace.DOWN);
            if ((down != null) && (down.getType() == Material.WOODEN_DOOR) || (down.getType() == Material.IRON_DOOR_BLOCK)) {
                if (!this.privateHandler.allowedInteraction(down, player)) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            } else if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) {
                if (!this.privateHandler.allowedInteraction(block, player)) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (itemHand.getType()) {
                case STICK: {
                    if (this.userHandler.getAccess(player) > 2 && wcfg.adminStick) {
                        event.setCancelled(true);
                        this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.ADMINSTICKED);
                        this.blockHandler.deleteBlockProtection(block);
                        block.setType(Material.AIR);
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case WATCH: {
                    if (this.userHandler.getAccess(event.getPlayer()) >= 2 && !player.isSneaking()) {
                        // TODO:
                        // Gjøre dette i egen thread, slik serveren sin thread blir
                        // brukt til mer nyttig
                        // BlockLog
                        ArrayList<String> blockLog = this.blockinfoHandler.getBlockLog(block, true);
                        if (blockLog != null) {
                            for (String logg : blockLog) {
                                player.sendMessage(logg);
                            }
                        }

                        // Block Owner
                        String owner = this.blockinfoHandler.getOwner(block);
                        if (owner == null) {
                            owner = "ingen eier";
                        }
                        player.sendMessage("Eier av blokken: " + ChatColor.BLUE + owner);

                        // PrivatItem
                        if (this.privateHandler.isPrivateItem(block)) {
                            ArrayList<Integer> priv = this.privateHandler.getOwnerPrivateItem(block);
                            player.sendMessage(ChatColor.GRAY + "Privatist av blokken: " + ChatColor.BLUE + (priv.size() > 0 && priv.get(1) == 1 ? this.groupHandler.getGroupNameFromID(priv.get(0)) + " (Gruppe)" : owner));
                        }

                        player.sendMessage("------------------ SLUTT -------------------");
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case SLIME_BALL: {
                    if (this.userHandler.getAccess(player) > 2) {
                        if (wcfg.protectBlocks) {
                            if (this.blockinfoHandler.isProtected(block)) {
                                this.blockHandler.deleteBlockProtection(block);
                                this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.UNPROTECTED);
                                player.sendMessage(ChatColor.DARK_GREEN + "Blokken har ikke lengre beskyttelse.");
                            } else {
                                player.sendMessage(ChatColor.RED + "Blokkbeskyttelsen er ikke på.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Blokkbeskyttelsen er ikke på.");
                        }
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case PAPER: {
                    if (this.userHandler.getAccess(player) > 2) {
                        if (wcfg.protectBlocks) {
                            this.blockHandler.setBlockProtection(this.userHandler.getUserId(player), block);
                            this.blockHandler.setBlocklog(this.userHandler.getUserId(player), block, BlockLogReason.CHANGEOWNER);
                            player.sendMessage(ChatColor.DARK_GREEN + "Blokken eies nå av deg.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Blokkbeskyttelsen er ikke på.");
                        }
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FENCE: {
                    Block space = block.getRelative(event.getBlockFace());
                    Block down = space.getRelative(BlockFace.DOWN);
                    if (space.isEmpty() && (down.isEmpty() || down.isLiquid())) {
                        space.setType(Material.FENCE);
                        this.blockHandler.setBlocklog(this.userHandler.getUserId(player), space, BlockLogReason.PLASSERTE);
                        this.blockHandler.setBlockProtection(this.userHandler.getUserId(player), space);
                        if (player.getItemInHand().getAmount() > 1) {
                            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                        } else {
                            event.getPlayer().getInventory().remove(player.getItemInHand());
                        }
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                default: {
                    break;
                }
            }

            // Doors, handle private item.
            Block down = block.getRelative(BlockFace.DOWN);
            if ((down != null) && (down.getType() == Material.WOODEN_DOOR) || (down.getType() == Material.IRON_DOOR_BLOCK)) {
                if (!this.privateHandler.allowedInteraction(down, player)) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            } else if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) {
                if (!this.privateHandler.allowedInteraction(block, player)) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            }

            if (block.getState() instanceof Chest || block.getState() instanceof Furnace || block.getState() instanceof Hopper || block.getState() instanceof Dropper || block.getState() instanceof BrewingStand || block.getState() instanceof Beacon || block.getState() instanceof Dispenser) {
                if (this.userHandler.getAccess(player) == 0) {
                    player.sendMessage(ChatColor.RED + "Gjester kan ikke åpne kister eller furnace.");
                    event.setCancelled(true);
                    return;
                } else {
                    if (wcfg.protectBlocks) {
                        String owner = this.blockinfoHandler.getOwner(block);
                        if ((owner != null) && (owner.equalsIgnoreCase(player.getName()))) {
                            return;
                        } else if ((owner != null) && (!owner.equalsIgnoreCase(player.getName()))) {
                            if (this.plugin.getUserHandler().getAccess(player) > 2) {
                                return;
                            }

                            // Private item?
                            if (block.getType() == Material.CHEST && this.privateHandler.isPrivateItem(block)) {
                                player.sendMessage(ChatColor.RED + "Denne kisten er satt som privat av " + ChatColor.WHITE + owner + ChatColor.RED + " og kan derfor ikke åpnes.");
                                event.setCancelled(true);
                                return;
                            }

                            if (block.getRelative(BlockFace.DOWN).getState() instanceof Sign) {
                                Sign signBlock = (Sign) block.getRelative(BlockFace.DOWN).getState();
                                String SignOwner = this.blockinfoHandler.getOwner(block.getRelative(BlockFace.DOWN));
                                if (SignOwner != null && SignOwner.equalsIgnoreCase(owner)) {
                                    for (String lines : signBlock.getLines()) {
                                        if (lines.equalsIgnoreCase("[privat]")) {
                                            player.sendMessage(ChatColor.RED + "Du kan ikke åpne denne kisten eller furnace. Den er privat");
                                            event.setCancelled(true);
                                            return;
                                        } else if (lines.equalsIgnoreCase("[public]")) {
                                            return;
                                        } else if (lines.equalsIgnoreCase("[doner]")) {
                                            return;
                                        }
                                    }
                                }
                            } else {
                                final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
                                for (BlockFace face : FACES) {
                                    Block other = block.getRelative(face);
                                    if (other.getType() == Material.CHEST) {
                                        if (other.getRelative(BlockFace.DOWN).getState() instanceof Sign) {
                                            Sign signBlock = (Sign) other.getRelative(BlockFace.DOWN).getState();
                                            String SignOwner = this.blockinfoHandler.getOwner(other.getRelative(BlockFace.DOWN));
                                            if (SignOwner != null && SignOwner.equalsIgnoreCase(owner)) {
                                                for (String lines : signBlock.getLines()) {
                                                    if (lines.equalsIgnoreCase("[privat]")) {
                                                        player.sendMessage(ChatColor.RED + "Du kan ikke åpne denne kisten eller furnace. Den er privat");
                                                        event.setCancelled(true);
                                                        return;
                                                    } else if (lines.equalsIgnoreCase("[public]")) {
                                                        return;
                                                    } else if (lines.equalsIgnoreCase("[doner]")) {
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            int groupId = this.groupHandler.getGroupID(player);
                            int groupIdBlock = this.groupHandler.getGroupIDFromName(owner);
                            if ((groupId > 0) && (groupId == groupIdBlock)) {
                                return;
                            }
                            player.sendMessage(ChatColor.RED + "Du kan ikke åpne denne kisten eller furnace.");
                            player.sendMessage(ChatColor.RED + "Den eies ikke av deg og du ikke er i gruppe med eieren.");
                            event.setCancelled(true);
                            return;
                        } else {
                            if (this.plugin.getUserHandler().getAccess(player) > 2) {
                                return;
                            }
                            player.sendMessage(ChatColor.RED + "Kisten eller furnace har ingen eier.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            if ((block.getType() == Material.NOTE_BLOCK) || (block.getType() == Material.DIODE) || (block.getType() == Material.DIODE_BLOCK_OFF) || (block.getType() == Material.DIODE_BLOCK_ON)) {
                String owner = this.blockinfoHandler.getOwner(block);
                if ((owner != null) && (owner.equalsIgnoreCase(player.getName())) || (!wcfg.protectBlocks)) {
                    return;
                } else {
                    event.setCancelled(true);
                }
            }
        }

        if (block != null) {
            if (block.getState() instanceof Sign) {
                if (block.getLocation().equals(new Location(this.plugin.getServer().getWorlds().get(0), 5441, 16, 1783))) {
                    adduser(player);
                    player.teleport(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
                }

                if (block.getLocation().equals(new Location(this.plugin.getServer().getWorlds().get(0), 5468, 16, 1783))) {
                    adduser(player);
                    player.teleport(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
                }

                if (block.getLocation().equals(new Location(this.plugin.getServer().getWorlds().get(0), 5495, 16, 1783))) {
                    adduser(player);
                    player.teleport(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
                }

                if (block.getLocation().equals(new Location(this.plugin.getServer().getWorlds().get(0), 5530, 16, 1783))) {
                    adduser(player);
                    player.teleport(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
                }
            }
        }
    }

    public void useCompass(Player player) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        if (this.userHandler.getAccess(player) > 2) {
            ArrayList<Block> blocks = new ArrayList<Block>();
            Iterator<Block> itr = new BlockIterator(player, 120);
            while (itr.hasNext()) {
                Block block = itr.next();
                blocks.add(block);
                if (blocks.size() > 1) {
                    blocks.remove(0);
                }
                int id = block.getTypeId();
                if (!cfg.defaultThroughBlock.contains(id)) {
                    break;
                }
            }
            if (!blocks.isEmpty()) {
                Block block = blocks.get(0);
                Teleporter tp = new Teleporter(new Location(block.getWorld(), block.getX() + 0.5D, block.getY() + 1, block.getZ() + 0.5D, player.getLocation().getYaw(), player.getLocation().getPitch()));
                if (!tp.teleportplayer(player)) {
                    player.sendMessage(ChatColor.RED + "Ingen ledig plass for teleportering.");
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!this.userHandler.isRegPlayer(event.getPlayer())) {
            this.chatHandler.sendMainChatMessage(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
        } else {
            event.getPlayer().sendMessage("Du kan ikke prate. Ønske du å avslutte testen. Skriv /spawn");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String name = event.getPlayer().getName();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        int Maxplayers = cfg.maxplayers;
        int OnlinePlayers = plugin.getServer().getOnlinePlayers().size() - 1;

        BanData banData = this.userHandler.getBanData(player.getName());
        if (banData != null) {
            if (banData.isWeekBan()) {
                if (this.userHandler.compareWeekBan(player.getName())) {
                    this.userHandler.unBanUser(name, true);
                    event.allow();
                    return;
                } else {
                    List<Integer> data = this.plugin.formatTime(banData);
                    int d = data.get(0);
                    int h = data.get(1);
                    int m = data.get(2);
                    int s = data.get(3);

                    String msg = (d > 0 ? d + "d, " : "") + (h > 0 ? h + "t, " : "") + (m > 0 ? m + "m, " : "") + s + "sek igjen.";

                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "Ukesbannet " + msg + " - " + banData.getReason());
                    return;
                }
            } else {
                if (this.userHandler.isBanned(player.getName())) {
                    String banReason = banData.getReason();
                    event.getResult();
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "Permbannet: " + banReason);
                    return;
                }
            }
        }
        if (cfg.maintaince) {
            if (!player.isOp() && this.userHandler.getAccess(player) < 3) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server er under vedlikehold.");
                return;
            }
        }
        if ((OnlinePlayers >= Maxplayers) && (!event.getPlayer().isOp()) && (!(this.userHandler.getAccess(player) < 3))) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ("Serveren er full."));
            return;
        }

        if (quittime.containsKey(player.getName())) {
            if (!(quittime.get(player.getName()).isEmpty())) {
                if ((System.currentTimeMillis() - quittime.get(player.getName()).getLast()) < (5000 * quittime.get(player.getName()).size())) {
                    quittime.get(player.getName()).add(System.currentTimeMillis());
                    if (quittime.get(player.getName()).size() > 2) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Du koblet til serveren før tiden gikk ut. Du må nå vente " + ((5000 * quittime.get(player.getName()).size() - (System.currentTimeMillis() - quittime.get(player.getName()).getLast())) / 1000) + " sekunder før du kobler til serveren på nytt.");
                    } else {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Du må vente " + ((5000 * quittime.get(player.getName()).size() - (System.currentTimeMillis() - quittime.get(player.getName()).getLast())) / 1000) + " sekunder før du kobler til serveren på nytt.");
                    }
                } else {
                    quittime.get(player.getName()).clear();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();

        this.userHandler.addPlayer(player);
        int access = this.userHandler.getAccess(player);
        
        System.out.println(this.userHandler.hasAdminChatActivated(player));

        for (String msg : cfg.srvLogin) {
            player.sendMessage(msg);
        }

        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getInvisible()) {
                player.hidePlayer(entry.getKey());
            }
        }

        if (!player.hasPlayedBefore()) {
            if (!(player.getInventory().contains(Material.MINECART))) {
                player.getInventory().addItem(new ItemStack(Material.MINECART, 1));
            }
            if (!(player.getInventory().contains(Material.WRITTEN_BOOK))) {
                player.getInventory().addItem(this.getBeginnersBook());
                player.sendMessage(ChatColor.GOLD + "Du har fått tildelt en bok med nyttig informasjon for serveren.");
                player.sendMessage(ChatColor.GOLD + "Sjekk innventaret ditt for å finne boken.");
            }
        } else {
            // TODO - should make a function for this.
            int warnings = this.warningHandler.countWarnings(player.getName());
            if (warnings > 0) {
                player.sendMessage(ChatColor.DARK_GREEN + "Du har " + ChatColor.WHITE + warnings + ChatColor.DARK_GREEN + " advarsel" + (warnings > 1 ? "er." : "."));
            }
        }

        NisseHandler nh = this.plugin.getNisseHandler();

        if (nh.isNisse(player)) {
            nh.addNisse(player);

            event.getPlayer().sendMessage(ChatColor.GRAY + "Du er i nissemodus!");
        } else {
            this.plugin.getUserHandler().tagPlayer(player);
        }

        if (!cfg.showloginonguest) {
            if (access != 0) {
                for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                    if (this.userHandler.getAnnonseringer(reciever)) {
                        reciever.sendMessage(player.getDisplayName() +
                            ChatColor.GREEN + " logget på.");
                    }
                }
            }
        } else {
            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                if (this.userHandler.getAnnonseringer(reciever)) {
                    reciever.sendMessage(player.getDisplayName() +
                        ChatColor.GREEN + " logget på" + (!player.hasPlayedBefore() ? " for første gang! :)" : "."));
                }
            }
        }

        event.setJoinMessage(null);

        if (cfg.showGroupInvitesJoin) {
            int groupInvites = this.groupHandler.getInvitesForLogin(player);
            if (groupInvites > 0) {
                player.sendMessage(ChatColor.DARK_GREEN + "Du har " + ChatColor.WHITE + groupInvites + ChatColor.DARK_GREEN + " gruppeinvitasjon" + (groupInvites > 1 ? "er." : "."));
            }
        }

        player.addAttachment(this.plugin, "hardwork.access" + access, true);

        if (this.userHandler.getInvisible(player)) {
            for (Player target : this.plugin.getServer().getOnlinePlayers()) {
                target.hidePlayer(player);
            }
        }

        this.userHandler.updatePlayer(event.getPlayer(), 0);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        // Reset fly counter on quit.
        this.flyCount.remove(player.getName());

        synchronized (kickedplayers) {
            if (!kickedplayers.contains(player.getName())) {
                for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                    if (player != reciever) {
                        if (this.userHandler.getAnnonseringer(reciever)) {
                            reciever.sendMessage(event.getPlayer().getDisplayName() +
                                ChatColor.RED + " logget av.");
                        }
                    }
                }
            } else {
                kickedplayers.remove(player.getName());
            }
        }

        this.userHandler.updatePlayer(player, 1);

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        if (this.userHandler.getAccess(player) >= 1) {
            this.userHandler.removePermissions(player);
        }

        if (this.userHandler.getAccess(player) < 1) {
            if (quittime.containsKey(player.getName())) {
                quittime.get(player.getName()).add(System.currentTimeMillis());
            } else {
                quittime.put(player.getName(), new ArrayDeque<Long>());
                quittime.get(player.getName()).add(System.currentTimeMillis());
            }
        }
        this.userHandler.delPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        event.setLeaveMessage(null);

        if (event.getReason().equalsIgnoreCase("Serveren er full.")) {
            event.setLeaveMessage(null);
            return;
        } else if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
            event.setCancelled(true);
            return;
        } else if ((event.getReason().toLowerCase().contains("flying")) || (event.getReason().toLowerCase().contains("floating"))) {
            this.handleFlymod(event);
        } else if (event.getReason().equalsIgnoreCase("You have been idle for too long!")) {               
            if (this.userHandler.getAccess(player) < 2) {
                event.setCancelled(true);
                return;
            }
            
            event.setReason("Du ble kicket for å være AFK alt for lenge. >=(");
            event.setLeaveMessage(ChatColor.GRAY + player.getName() + " ble kicket for å være afk for lenge.");
        }

        if (player.isInsideVehicle()) {
            event.getPlayer().leaveVehicle();
        }
        synchronized (kickedplayers) {
            kickedplayers.add(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getIrcBot().sendMessage("#hardwork.iplogg", player.getName() + " (" + player.getAddress() + ") koblet til.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getIrcBot().sendMessage("#hardwork.iplogg", player.getName() + " (" + player.getAddress() + ") koblet fra.");
    }

    /**
     * Returns the information book for guests
     *
     * @return ItemStack
     */
    public ItemStack getBeginnersBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setAuthor("Staben");
        bm.setTitle("Informasjon");
        ArrayList<String> pages = new ArrayList<String>();
        pages.add("Hei, gjest, og velkommen til Hardwork!\n\nDette er en helnorsk byggeserver, drevet av www.minecraft.no.\n\nInfo: side 2\nRegistrering: side 3");
        pages.add("Du er nå i /spawn. For se steder du kan bruke /warp for å komme til skriver du /warps.\n\n/warp info vil gi deg en oversikt over ting du bør vite om Hardwork og Minecraft generelt.");
        pages.add("For å bli bruker må du søke om byggetillatelse på forumet på www.minecraft.no.\n\nHusk å les brukerguide og regler for serveren først. Linker til dette vil du finne på forumsiden vår.");
        bm.setPages(pages);
        book.setItemMeta(bm);

        return book;
    }

    public void handleFlymod(PlayerKickEvent event) {
        // Prevent Minecraft's default handling of these events.
        event.setCancelled(true);

        Player player = event.getPlayer();

        // We trust players ranked 2 (pensjonist) or above.
        if (this.plugin.getUserHandler().getAccess(player) >= 2) {
            return;
        }

        // Check if the player is in a cooldown period.
        if (this.flyCooldown.containsKey(player.getName()) && (System.currentTimeMillis() - this.flyCooldown.get(player.getName())) / 1000L > 20) {
            return;
        }

        // Notify online players with a rank of 2 (pensjonist) or higher, and log to IRC.
        this.plugin.getChatHandler().rankBroadcastMessage(2, ChatColor.RED + player.getName() + " bruker kanskje flymod!");
        this.plugin.getIrcBot().sendMessage("#hardwork.logg", Colors.RED + player.getName() + " bruker kanskje flymod!");

        // Set/update the cooldown period.
        this.flyCooldown.put(player.getName(), System.currentTimeMillis());

        // Keep track of how many times this event has triggered for this player.
        if (!this.flyCount.containsKey(player.getName())) {
            // First time.
            this.flyCount.put(player.getName(), 1);
        } else {
            // Increment.
            this.flyCount.put(player.getName(), this.flyCount.get(player.getName()) + 1);

            // Does the player keep doing it?
            if (this.flyCount.get(player.getName()) >= 3) {
                // Let 'em have it!
                event.setCancelled(false);

                // Kicked for flying before?
                if (!this.flyKickCount.containsKey(player.getName())) {
                    // Nope.
                    this.flyKickCount.put(player.getName(), 1);
                } else {
                    // Yes. Increment.
                    this.flyKickCount.put(player.getName(), this.flyKickCount.get(player.getName()));

                    // Kicked many times before?
                    if (this.flyKickCount.get(player.getName()) >= 3) {
                        // Yup. Git rid of him!
                        this.plugin.getUserHandler().banUser(player, "Automatisk bannet for flymod.", "FlymodCheck");

                        // Clear this counter. We won't see him any more =)
                        this.flyKickCount.remove(player.getName());

                        // Notify and log, like above.
                        this.plugin.getChatHandler().rankBroadcastMessage(2, ChatColor.RED + player.getName() + " ble automatisk bannet for bruk av flymod!");
                        this.plugin.getIrcBot().sendMessage("#hardwork.logg", Colors.RED + player.getName() + " ble automatisk bannet for bruk av flymod!");
                    }
                }

                // Set the kick message, and reset the counter.
                event.setReason("Flymod er ikke tillatt!");
                this.flyCount.remove(player.getName());

                // Notify and log, like above.
                this.plugin.getChatHandler().rankBroadcastMessage(2, ChatColor.RED + player.getName() + " ble automatisk kicket for bruk av flymod!");
                this.plugin.getIrcBot().sendMessage("#hardwork.logg", Colors.RED + player.getName() + " ble automatisk kicket for bruk av flymod!");
            }
        }
    }
}
