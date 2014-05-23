package no.minecraft.Minecraftno.listener;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MinecraftnoVehicleListener implements Listener {

    private final Minecraftno plugin;
    double maxSpeed = 0;

    public MinecraftnoVehicleListener(Minecraftno instance) {
        this.plugin = instance;
        maxSpeed = 0.65;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!event.getVehicle().isEmpty()) {
            if (event.getVehicle() instanceof Boat) {
                event.setCancelled(true);
            }

            if ((event.getVehicle() instanceof Minecart)) {
                if (!(event.getVehicle().getPassenger() == event.getAttacker())) {
                    event.setCancelled(true);
                } else if ((event.getVehicle().getPassenger() != null) && (event.getAttacker() != null) && (event.getAttacker().getEntityId() == event.getVehicle().getPassenger().getEntityId())) {
                    event.setDamage(0);
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getAttacker() instanceof Player && event.getVehicle().getType() == EntityType.MINECART) {
                Player p = (Player) event.getAttacker();
                p.getInventory().addItem(new ItemStack(Material.MINECART));
                event.getVehicle().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if ((event.getVehicle() instanceof Minecart)) {
            Entity collisioner = event.getEntity();

            if ((collisioner instanceof LivingEntity)) {
                LivingEntity victim = (LivingEntity) collisioner;
                if ((!(victim instanceof Player)) && (!(victim instanceof Wolf))) {
                    victim.remove();
                    event.setCancelled(true);
                    event.setCollisionCancelled(true);
                    event.setPickupCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if ((!event.getVehicle().isEmpty()) && ((event.getBlock().getTypeId() != 8) || (event.getBlock().getTypeId() != 9))) {
                Player localPlayer = (Player) event.getVehicle().getPassenger();
                event.getVehicle().teleport(localPlayer.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();

        // Kun minecarts!
        if (!(vehicle instanceof Minecart || vehicle instanceof StorageMinecart)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        Minecart cart = (Minecart) (event.getVehicle());
        Player player = (Player) event.getVehicle().getPassenger();
        Block rail = cart.getWorld().getBlockAt(cart.getLocation());
        Block under = to.getBlock().getRelative(BlockFace.DOWN);
        Block signBlock = under.getRelative(BlockFace.DOWN);
        byte color = under.getData();

        if (vehicle instanceof Minecart) {
            if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                if (under.getType() == Material.WOOL) {
                    if (color == DyeColor.BLUE.getWoolData()) {
                        Block skilt = under.getRelative(BlockFace.DOWN);
                        if (skilt != null && skilt.getType() == Material.SIGN_POST) {
                            Sign sign = (Sign) skilt.getState();
                            if (player != null) {
                                String message = ChatColor.GREEN + sign.getLine(0) + sign.getLine(1) + sign.getLine(2) + sign.getLine(3);
                                player.sendMessage(message);
                                return;
                            }
                        }
                    } else if (color == DyeColor.GRAY.getWoolData()) {
                        // BREMSE 50%
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() / 4);
                        speed.setZ(speed.getZ() / 4);
                        cart.setVelocity(speed);
                        cart.setMaxSpeed(maxSpeed / 4);
                    } else if (color == DyeColor.SILVER.getWoolData()) {
                        // BREMSE 25%
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() / 2);
                        speed.setZ(speed.getZ() / 2);
                        cart.setVelocity(speed);
                        cart.setMaxSpeed(maxSpeed / 2);
                    } else if (color == DyeColor.PINK.getWoolData()) {
                        cart.eject();
                        return;
                    } else if (color == DyeColor.YELLOW.getWoolData() && !under.isBlockIndirectlyPowered()) {
                        Vector speed = cart.getVelocity();
                        speed.setX(-speed.getX());
                        speed.setZ(-speed.getZ());
                        cart.setVelocity(speed);
                    } else if (color == DyeColor.RED.getWoolData()) {
                        cart.setVelocity(cart.getVelocity().multiply(0.0));
                    } else if (color == DyeColor.BLACK.getWoolData() && !under.isBlockIndirectlyPowered() && signBlock.getType().equals(Material.SIGN_POST)) {
                        Sign sign = (Sign) signBlock.getState();
                        for (String lines : sign.getLines()) {
                            if (lines.equalsIgnoreCase("[Stasjon]")) {
                                Vector speed = new Vector();
                                speed.setX(0);
                                speed.setY(0);
                                speed.setZ(0);
                                cart.setVelocity(speed);
                                return;
                            }
                        }
                    } else if (color == DyeColor.BROWN.getWoolData() && (signBlock.getState() instanceof Sign)) {
                        if (event.getVehicle().getType() != EntityType.MINECART_CHEST) {
                            return;
                        }
                        Sign signe = (Sign) signBlock.getState();
                        for (String lines : signe.getLines()) {
                            if (lines.equalsIgnoreCase("[Mottak]")) {
                                Block track = to.getBlock();
                                if (track.getData() == 0x0) {
                                    Block b_l = track.getRelative(-2, 0, 0);
                                    Block b_r = track.getRelative(+2, 0, 0);
                                    if (b_l.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_l.getState();
                                        depositAll(chest, event.getVehicle());
                                    } else if (b_r.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_r.getState();
                                        depositAll(chest, event.getVehicle());
                                    }
                                } else if (track.getData() == 0x1) {
                                    Block b_l = track.getRelative(0, 0, +2);
                                    Block b_r = track.getRelative(0, 0, -2);
                                    if (b_l.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_l.getState();
                                        depositAll(chest, event.getVehicle());
                                    } else if (b_r.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_r.getState();
                                        depositAll(chest, event.getVehicle());
                                    }
                                }
                            }
                        }
                    } else if (color == DyeColor.CYAN.getWoolData() && (signBlock.getState() instanceof Sign)) {
                        if (event.getVehicle().getType() != EntityType.MINECART_CHEST) {
                            return;
                        }
                        Sign signe = (Sign) signBlock.getState();
                        for (String lines : signe.getLines()) {
                            if (lines.equalsIgnoreCase("[Mottak]")) {
                                ItemStack item = null;
                                try {
                                    item = this.plugin.matchItem(signe.getLines()[2]);
                                } catch (Exception e) {
                                    return;
                                }
                                Block track = to.getBlock();
                                if (track.getData() == 0x0) {
                                    Block b_l = track.getRelative(-2, 0, 0);
                                    Block b_r = track.getRelative(+2, 0, 0);
                                    if (b_l.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_l.getState();
                                        depositID(chest, event.getVehicle(), item);
                                    } else if (b_r.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_r.getState();
                                        depositID(chest, event.getVehicle(), item);
                                    }
                                } else if (track.getData() == 0x1) {
                                    Block b_l = track.getRelative(0, 0, +2);
                                    Block b_r = track.getRelative(0, 0, -2);
                                    if (b_l.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_l.getState();
                                        depositID(chest, event.getVehicle(), item);
                                    } else if (b_r.getState() instanceof Chest) {
                                        Chest chest = (Chest) b_r.getState();
                                        depositID(chest, event.getVehicle(), item);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
                if ((event.getVehicle().getPassenger() != null) && (event.getVehicle().getPassenger() instanceof Player)) {
                    if (rail.getData() != 0x2 || rail.getData() != 0x3) {
                        if (rail.getData() != 0x5 || rail.getData() != 0x4) {
                            Vector speed = cart.getVelocity();
                            speed.setX(speed.getX() * 2);
                            speed.setZ(speed.getZ() * 2);
                            if (speed.getX() < 0) {
                                if (speed.getX() < -this.maxSpeed) {
                                    speed.setX(-this.maxSpeed);
                                }
                            }
                            if (speed.getX() > 0) {
                                if (speed.getX() > this.maxSpeed) {
                                    speed.setX(this.maxSpeed);
                                }
                            }
                            if (speed.getZ() < 0) {
                                if (speed.getZ() < -this.maxSpeed) {
                                    speed.setZ(-this.maxSpeed);
                                }
                            }
                            if (speed.getZ() > 0) {
                                if (speed.getZ() > this.maxSpeed) {
                                    speed.setZ(this.maxSpeed);
                                }
                            }
                            cart.setMaxSpeed(this.maxSpeed);
                            cart.setVelocity(speed);
                        } else {
                            cart.setMaxSpeed(0.4D);
                        }
                    } else {

                    }
                } else if (vehicle instanceof StorageMinecart) {
                    if (rail.getData() != 0x2 || rail.getData() != 0x3) {
                        if (rail.getData() != 0x5 || rail.getData() != 0x4) {
                            Vector speed = cart.getVelocity();
                            speed.setX(speed.getX() * 2);
                            speed.setZ(speed.getZ() * 2);
                            if (speed.getX() < 0) {
                                if (speed.getX() < -this.maxSpeed / 2) {
                                    speed.setX(-this.maxSpeed / 2);
                                }
                            }
                            if (speed.getX() > 0) {
                                if (speed.getX() > this.maxSpeed / 2) {
                                    speed.setX(this.maxSpeed / 2);
                                }
                            }
                            if (speed.getZ() < 0) {
                                if (speed.getZ() < -this.maxSpeed / 2) {
                                    speed.setZ(-this.maxSpeed / 2);
                                }
                            }
                            if (speed.getZ() > 0) {
                                if (speed.getZ() > this.maxSpeed / 2) {
                                    speed.setZ(this.maxSpeed / 2);
                                }
                            }
                            cart.setMaxSpeed(this.maxSpeed / 2);
                            cart.setVelocity(speed);
                        } else {
                            cart.setMaxSpeed(0.4D);
                        }
                    } else {

                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        if (!(event.getVehicle() instanceof Minecart || event.getVehicle() instanceof PoweredMinecart || event.getVehicle() instanceof StorageMinecart)) {
            return;
        }
        Minecart cart = (Minecart) (event.getVehicle());
        Block under = cart.getWorld().getBlockAt(cart.getLocation()).getRelative(BlockFace.DOWN);
        Block signBlock = under.getRelative(BlockFace.DOWN);
        byte color = under.getData();

        if (under.getType() == Material.WOOL) {
            if (color == DyeColor.BLACK.getWoolData() && under.isBlockIndirectlyPowered() && signBlock.getType().equals(Material.SIGN_POST)) {
                Sign sign = (Sign) signBlock.getState();
                for (String lines : sign.getLines()) {
                    if (lines.equalsIgnoreCase("[Stasjon]")) {
                        Vector speed = new Vector();
                        speed.setX(0);
                        speed.setZ(0);
                        if (sign.getRawData() == 0x0) {
                            // VEST
                            speed.setZ(-0.6);
                            cart.setVelocity(speed);
                            return;
                        } else if (sign.getRawData() == 0x4) {
                            // NORD
                            speed.setX(0.6);
                            cart.setVelocity(speed);
                            return;
                        } else if (sign.getRawData() == 0x8) {
                            // Øst
                            speed.setZ(0.6);
                            cart.setVelocity(speed);
                            return;
                        } else if (sign.getRawData() == 0xC) {
                            // Sør
                            speed.setX(-0.6);
                            cart.setVelocity(speed);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();

        // Kun minecarts!
        if (!(vehicle instanceof Minecart)) {
            return;
        }
        Minecart minecart = (Minecart) vehicle;
        //minecart.setSlowWhenEmpty(true);
        minecart.setMaxSpeed(2.0D);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        Entity entity = event.getEntered();

        if (vehicle instanceof Horse && entity instanceof Player) {
            Player player = (Player) entity;
            Horse horsy = (Horse) vehicle;
            if (horsy.isTamed() && horsy.getOwner() != null) {
                String owner = horsy.getOwner().getName();
                if (!owner.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Du kan ikke ri på denne hesten.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            Vehicle veh = event.getVehicle();
            if (veh.getType() == EntityType.MINECART || veh.getType() == EntityType.BOAT) {
                Material matReturn = Material.getMaterial(veh.getType().toString()); // Name of enums of minecart and boat are the same in Material.
                if (matReturn != null) {
                    ItemStack toReturn = new ItemStack(matReturn);
                    if (toReturn != null) {
                        if (player.getInventory().firstEmpty() != -1) {
                            player.getInventory().addItem(toReturn);
                        } else {
                            player.getWorld().dropItem(player.getLocation(), toReturn);
                        }

                        veh.remove();
                    }
                }
            }
        }
    }

    public void depositID(Chest chest, Entity e, ItemStack checkitem) {
        if (!(e instanceof StorageMinecart)) {
            return;
        }
        StorageMinecart storageMinecart = (StorageMinecart) e;
        ArrayList<ItemStack> rest = new ArrayList<ItemStack>();
        for (ItemStack item : storageMinecart.getInventory().getContents()) {
            if (item != null && item.isSimilar(checkitem)) {
                HashMap<Integer, ItemStack> Failed = chest.getInventory().addItem(item);
                for (Entry<Integer, ItemStack> entry : Failed.entrySet()) {
                    if (entry.getValue() != null) {
                        rest.add(entry.getValue());
                    }
                }
            } else {
                rest.add(item);
            }
        }
        storageMinecart.getInventory().clear();
        for (ItemStack item : rest) {
            if (item != null) {
                storageMinecart.getInventory().addItem(item);
            }
        }
    }

    public void depositAll(Chest chest, Entity e) {
        if (!(e instanceof StorageMinecart)) {
            return;
        }
        StorageMinecart storageMinecart = (StorageMinecart) e;
        ArrayList<ItemStack> rest = new ArrayList<ItemStack>();
        for (ItemStack item : storageMinecart.getInventory().getContents()) {
            if (item != null) {
                HashMap<Integer, ItemStack> Failed = chest.getInventory().addItem(item);
                for (Entry<Integer, ItemStack> entry : Failed.entrySet()) {
                    if (entry.getValue() != null) {
                        rest.add(entry.getValue());
                    }
                }
            }
        }
        storageMinecart.getInventory().clear();
        for (ItemStack item : rest) {
            if (item != null) {
                storageMinecart.getInventory().addItem(item);
            }
        }
    }
}