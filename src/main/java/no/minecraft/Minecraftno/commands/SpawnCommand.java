package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SpawnCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public SpawnCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            String worldName = player.getWorld().getName();
            Location spawn = this.plugin.getServer().getWorld(worldName).getSpawnLocation();
            if (this.userHandler.getFreeze(player)) {
                player.sendMessage(getErrorChatColor() + "Du kan ikke bruke /spawn når du er fryst");
            } else {
                player.teleport(spawn);
            }
            if (this.userHandler.isRegPlayer(player)) {
                this.userHandler.removeRegPlayer(player);
                for (Player target : this.plugin.getServer().getOnlinePlayers()) {
                    target.showPlayer(player);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}