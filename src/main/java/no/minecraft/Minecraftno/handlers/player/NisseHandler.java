package no.minecraft.Minecraftno.handlers.player;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NisseHandler {
    private Minecraftno plugin;

    private List<String> nisser = new ArrayList<String>();

    public NisseHandler(Minecraftno plugin) {
        this.plugin = plugin;
    }

    public void addNisse(Player player) {
        player.setDisplayName(ChatColor.RED + "Nissen");

        this.nisser.add(player.getName());

        this.plugin.getLogger().info("Added nisse: " + player.getName());
    }

    public void removeNisse(Player player) {
        UserHandler uh = this.plugin.getUserHandler();

        // Set the player's access level to whatever it already is. This forces a refresh of the display name, and thus restores the original tag.
        uh.changeAccessLevel(player, uh.getAccess(player));

        this.nisser.remove(player.getName());

        this.plugin.getLogger().info("Removed nisse: " + player.getName());
    }

    public boolean isNisse(Player player) {
        return this.nisser.contains(player.getName());
    }
}
