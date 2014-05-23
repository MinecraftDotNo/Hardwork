package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemArmor extends MinecraftnoCommand {

    public ItemArmor(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length >= 2) {
            ItemStack itemStack = null;
            Material material = null;
            material = Material.matchMaterial(args[0]);

            if (material == null) {
                player.sendMessage(getErrorChatColor() + "Ugyldig item (" + getOkChatColor() + args[0] + getErrorChatColor() + ").");
                return true;
            }

            itemStack = new ItemStack(Material.matchMaterial(args[0]));

            PlayerInventory inv = player.getInventory();

            if (args[1].equalsIgnoreCase("helmet")) {
                inv.setHelmet(itemStack);
            } else if (args[1].equalsIgnoreCase("chestplate")) {
                inv.setChestplate(itemStack);
            } else if (args[1].equalsIgnoreCase("leggings")) {
                inv.setLeggings(itemStack);
            } else if (args[1].equalsIgnoreCase("boots")) {
                inv.setBoots(itemStack);
            } else {
                player.sendMessage(getErrorChatColor() + "Ukjent slot");
            }

            return true;
        } else {
            return false;
        }
    }
}
