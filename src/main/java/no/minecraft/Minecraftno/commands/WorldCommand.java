package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class WorldCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;

    public WorldCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        Server server = this.plugin.getServer();

        if (!this.userHandler.isRegPlayer(player)) {
            if (args.length == 0) {
                player.teleport(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder build = new StringBuilder();
                    for (World world : this.plugin.getServer().getWorlds()) {
                        if (world.getName().equalsIgnoreCase("world")) {
                            build.append(world.getName() + ", ");
                        } else {
                            build.append(world.getName().substring(6) + ", ");
                        }
                    }
                    player.sendMessage(build.toString().substring(0, build.toString().length() - 2) + ".");
                } else {
                    World w = null;
                    if (args[0].equalsIgnoreCase("world")) {
                        w = server.getWorld(args[0]);
                    } else {
                        w = server.getWorld("world_" + args[0]);
                    }
                    if (w == null) {
                        player.sendMessage(getErrorChatColor() + "Verden du valgte eksisterer ikke.");
                    } else {
                        player.teleport(w.getSpawnLocation());
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
