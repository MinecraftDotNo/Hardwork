package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MobSpawnCommand extends MinecraftnoCommand {

    HashMap<String, EntityType> mobs = new HashMap<String, EntityType>();

    public MobSpawnCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
        mobs.put("cavespider", EntityType.CAVE_SPIDER);
        mobs.put("chicken", EntityType.CHICKEN);
        mobs.put("cow", EntityType.COW);
        mobs.put("creeper", EntityType.CREEPER);
        mobs.put("enderman", EntityType.ENDERMAN);
        mobs.put("ghast", EntityType.GHAST);
        mobs.put("giant", EntityType.GIANT);
        mobs.put("pig", EntityType.PIG);
        mobs.put("pigzombie", EntityType.PIG_ZOMBIE);
        mobs.put("sheep", EntityType.SHEEP);
        mobs.put("silverfish", EntityType.SILVERFISH);
        mobs.put("skeleton", EntityType.SKELETON);
        mobs.put("slime", EntityType.SLIME);
        mobs.put("spider", EntityType.SPIDER);
        mobs.put("squid", EntityType.SQUID);
        mobs.put("wolf", EntityType.WOLF);
        mobs.put("zombie", EntityType.ZOMBIE);
        mobs.put("enderdragon", EntityType.ENDER_DRAGON);
        mobs.put("blaze", EntityType.BLAZE);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (player.getTargetBlock(null, 50) != null) {
                if (args[0].equalsIgnoreCase("liste")) {
                    StringBuilder moblist = new StringBuilder();
                    for (String type : mobs.keySet()) {
                        moblist.append(getVarChatColor() + type + getDefaultChatColor() + ", ");
                    }
                    String utskrift = moblist.substring(0, moblist.length() - 2) + ".";
                    player.sendMessage(getDefaultChatColor() + "Gyldige mobs er: ");
                    player.sendMessage(utskrift);
                    return true;
                } else {
                    if (args.length == 1) {
                        Block block = player.getTargetBlock(null, 50);
                        if (this.getMob(args[0]) != null) {
                            block.getWorld().spawn(block.getLocation().add(0.0D, 2.0D, 0.0D), getMob(args[0]).getEntityClass());
                            player.sendMessage(getOkChatColor() + "1 " + args[0]);
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "Ukjent mob");
                            return true;
                        }
                    } else if (args.length == 2) {
                        if (!canParse(args[1])) {
                            this.invalidInt(player, args[1]);
                            return false;
                        }
                        Block b = player.getTargetBlock(null, 50);
                        int i = 0;
                        if (getMob(args[0]) != null) {
                            while (i < Integer.parseInt(args[1])) {
                                b.getWorld().spawn(b.getLocation().add(0.0D, 2.0D, 0.0D), getMob(args[0]).getEntityClass());
                                i++;
                            }
                            player.sendMessage(getOkChatColor() + args[1] + " " + args[0]);
                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "Ukjent mob");
                            return true;
                        }
                    }
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Blokken er utenfor rekkevidde.");
                return false;
            }
        }
        return false;
    }

    public final EntityType getMob(String str) {
        EntityType type = null;
        if (mobs.containsKey(str.toLowerCase())) {
            type = mobs.get(str.toLowerCase());
        } else {
            return null;
        }
        return type;
    }

    public final void invalidInt(Player p, String i) {
        p.sendMessage(getErrorChatColor() + "Ikke et tall: " + i);
    }

    public final static boolean canParse(String i) {
        try {
            Integer.valueOf(Integer.parseInt(i));
            return true;
        } catch (NumberFormatException ne) {

        }
        return false;
    }
}
