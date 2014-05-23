package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;

public class ChangeMSCommand extends MinecraftnoCommand {

    HashMap<String, EntityType> mobs = new HashMap<String, EntityType>();

    public ChangeMSCommand(Minecraftno instance) {
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
        mobs.put("wither", EntityType.WITHER);
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 0) {
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder moblist = new StringBuilder();
                Iterator<String> nextmob = this.mobs.keySet().iterator();

                while (nextmob.hasNext()) {
                    moblist.append(getVarChatColor() + nextmob.next() + getDefaultChatColor() + ", ");
                }
                String utskrift = moblist.substring(0, moblist.length() - 2) + ".";
                player.sendMessage(getDefaultChatColor() + "Gyldige spawners er: ");
                player.sendMessage(utskrift);
                return true;
            } else {
                Block b = player.getTargetBlock(null, 5);
                if (b != null && b.getState() instanceof CreatureSpawner) {
                    if (this.getSpawner(args[0]) != null) {
                        CreatureSpawner mobSpawner = (CreatureSpawner) b.getState();
                        mobSpawner.setSpawnedType(getSpawner(args[0]));
                        player.sendMessage(getOkChatColor() + "Den er nå endret til: " + args[0]);
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Ukjent spawner type");
                        return true;
                    }
                } else {
                    player.sendMessage(getErrorChatColor() + "Det er ikke en mobspawnere");
                    return true;
                }
            }
        }
        return false;
    }

    public final EntityType getSpawner(String str) {
        EntityType type = null;
        if (mobs.containsKey(str.toLowerCase())) {
            type = mobs.get(str.toLowerCase());
        } else {
            return null;
        }
        return type;
    }
}
