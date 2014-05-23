package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WorldHandler;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Random;

public class MultiworldCommand extends MinecraftnoCommand {

    private WorldHandler worldHandler;

    public MultiworldCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
        this.worldHandler = instance.getWorldHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if ((args.length > 0) && player.isOp()) {
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
            }
            if (args[0].equalsIgnoreCase("del")) {
                World w = this.plugin.getServer().getWorld(args[1]);
                if (w == null) {
                    player.sendMessage(getErrorChatColor() + "Verden du valgte eksisterer ikke.");
                } else {
                    this.worldHandler.delWorld(w);
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (this.plugin.getServer().getWorld("world_" + args[1]) != null) {
                    player.sendMessage(getErrorChatColor() + ("Verden finnes allerede"));
                    return true;
                }
                long seed = new Random().nextLong();
                if (args.length == 2) {
                    this.worldHandler.makeWorld("world_" + args[1].toLowerCase(), seed, player);
                    //this.plugin.getServer().createWorld(WorldCreator.name("world_" + args[1].toLowerCase()).seed(seed).environment(World.Environment.NORMAL));
                } else if (args.length == 3) {
                    this.worldHandler.makeWorld("world_" + args[1].toLowerCase(), seed, args[2], null, player);
                } else if (args.length == 4) {
                    this.worldHandler.makeWorld("world_" + args[1].toLowerCase(), seed, args[2], args[3], player);
                } else {
                    return false;
                }
                player.sendMessage(getInfoChatColor() + "Du opprettet " + getVarChatColor() + args[1]);
            }
        } else {
            return false;
        }
        return true;
    }
}