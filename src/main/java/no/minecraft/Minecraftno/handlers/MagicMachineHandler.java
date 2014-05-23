package no.minecraft.Minecraftno.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MagicMachineHandler {

    public static void magicMachin(Block block) {

        Random r = new Random();
        if (block.getWorld().getName().equalsIgnoreCase("world")) {
            Block[] Undervann = {block.getRelative(BlockFace.DOWN), block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.WEST)};
            for (int i = 0; i < Undervann.length; i++) {
                //BlockProtectHandler.delete(Undervann[i]);

                // gravel + stein == clay

                if (Undervann[i].getTypeId() == 1) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if ((13 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(4);
                        if (choice == 0) {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(82);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 13) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if ((1 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(4);
                        if (choice == 0) {
                            Undervann[i].setTypeId(82);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                // stein == obsidian

                if (Undervann[i].getTypeId() == 1) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if (89 == Undervannunder.getTypeId() || (1 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(4);
                        if (choice == 0) {
                            Undervann[i].setTypeId(49);
                        } else {
                            Undervann[i].setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 89) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if (1 == Undervannunder.getTypeId() || (1 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(4);
                        if (choice == 0) {
                            Undervannunder.setTypeId(49);
                        } else {
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 3) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if (89 == Undervannunder.getTypeId() || (3 == Undervannunder.getTypeId())) {
                        int choice = (r.nextInt(100) < 25) ? 1 : 0;
                        if (choice == 0) {
                            Undervann[i].setTypeId(12);
                        } else {
                            Undervann[i].setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 89) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if (3 == Undervannunder.getTypeId() || (3 == Undervannunder.getTypeId())) {
                        int choice = (r.nextInt(100) < 25) ? 1 : 0;
                        if (choice == 0) {
                            Undervannunder.setTypeId(12);
                        } else {
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 3) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if ((5 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(2);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.LEAVES, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 5) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if ((3 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(2);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.LEAVES, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 1) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if ((35 == Undervannunder.getTypeId() && (4 == Undervannunder.getData()))) {
                        int choice = r.nextInt(3);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.GLOWSTONE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 35) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if ((1 == Undervannunder.getTypeId() && (4 == Undervannunder.getData()))) {
                        int choice = r.nextInt(3);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.GLOWSTONE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 49) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if (87 == Undervannunder.getTypeId()) {
                        int choice = r.nextInt(5);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.RED_ROSE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 87) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if (49 == Undervannunder.getTypeId()) {
                        int choice = r.nextInt(5);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.RED_ROSE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 42) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if (89 == Undervannunder.getTypeId() || (42 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(1);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.IRON_DOOR_BLOCK, 1));
                            Undervann[i].setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 89) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if (42 == Undervannunder.getTypeId() || (42 == Undervannunder.getTypeId())) {
                        int choice = r.nextInt(1);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervannunder.getWorld().dropItem(loc, new ItemStack(Material.IRON_DOOR_BLOCK, 1));
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 49) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.DOWN);
                    if (35 == Undervannunder.getTypeId()) {
                        int choice = r.nextInt(5);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.BONE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }

                if (Undervann[i].getTypeId() == 35) {
                    Block Undervannunder = Undervann[i].getRelative(BlockFace.UP);
                    if (49 == Undervannunder.getTypeId()) {
                        int choice = r.nextInt(5);
                        if (choice == 0) {
                            Location loc = Undervann[i].getLocation();
                            Undervann[i].getWorld().dropItem(loc, new ItemStack(Material.BONE, 1));
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        } else {
                            Undervann[i].setTypeId(0);
                            Undervannunder.setTypeId(0);
                        }
                    }
                }
            }
        }
    }
}
