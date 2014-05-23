package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class NoDropCommand extends MinecraftnoCommand {

    public NoDropCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            for (Entity e : player.getWorld().getEntities()) {
                if ((e instanceof Item)) {
                    e.remove();
                }
            }
            player.sendMessage(getOkChatColor() + "Alle item drops fjernet");
            return true;
        } else {
            return false;
        }
    }
}