package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GwarpsCommand extends MinecraftnoCommand {

    private final WarpHandler wh;

    public GwarpsCommand(Minecraftno instance, WarpHandler wh) {
        super(instance);
        this.wh = wh;
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            String warpList = this.wh.getGwarpListString();
            player.sendMessage(getDefaultChatColor() + "Tilgjengelige graveområder:");
            player.sendMessage(warpList);
            return true;
        } else {
            return false;
        }
    }
}
