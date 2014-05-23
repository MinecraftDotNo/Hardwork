package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class InvSearch extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public InvSearch(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        Material material = null;
        int count = 1;

        if (args.length >= 1) {
            String[] gData;
            gData = args[0].split(":");
            material = Material.matchMaterial(gData[0]);
        } else if (args.length >= 2) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                player.sendMessage(getErrorChatColor() + "'" + args[1] + "' er ikke et tall!");
                return false;
            }
        }

        if (material == null) {
            player.sendMessage(getErrorChatColor() + "Ukjent item");
            return false;
        }
        StringBuilder build = new StringBuilder();
        for (Player templayer : this.plugin.getServer().getOnlinePlayers()) {
            if (!templayer.getInventory().contains(material, count)) {
                continue;
            }
            build.append(templayer.getName() + "  ");
        }
        player.sendMessage("Spillere med item " + material.toString() + ":  " + build.toString());
        return true;
    }
}