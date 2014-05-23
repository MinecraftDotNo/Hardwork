package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MinecartCommand extends MinecraftnoCommand {

    private Map<String, Integer> cooldowns;

    public MinecartCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);

        this.cooldowns = new HashMap<String, Integer>();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        String uname = player.getName();

		/* Has cooldown? */
        if (this.cooldowns.containsKey(uname)) {
            int time = (int) (System.currentTimeMillis() / 1000L);
            int left = this.cooldowns.get(uname) + 300;

            if (time < left) {
                int diff = left - time;
                int m = diff / 60 % 60;
                int s = diff % 60;

                player.sendMessage(this.getErrorChatColor() + "Du må vente " + this.getArgChatColor() +
                    (m > 0 ? m + this.getErrorChatColor().toString() : 0) + " minutter og " + this.getArgChatColor() +
                    (s > 0 ? s + this.getErrorChatColor().toString() : 0) + " sekunder " + this.getErrorChatColor().toString() + "før du kan bruke denne kommandoen igjen.");
                return true;
            }

            this.cooldowns.remove(uname);
        }

        // Give player a minecart, then add too cooldown.
        player.getInventory().addItem(new ItemStack(Material.MINECART, 1));
        this.cooldowns.put(uname, ((int) (System.currentTimeMillis() / 1000L)));

        return true;
    }
}
