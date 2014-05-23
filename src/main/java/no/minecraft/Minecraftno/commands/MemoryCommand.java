package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class MemoryCommand extends MinecraftnoCommand {

    public MemoryCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {

        if (args.length == 0) {
            player.sendMessage(getCommandChatColor() + "Listen er i MB's");
            player.sendMessage(getOkChatColor() + "I bruk / Totalt: " + usedMem() + "/" + maxMem());
            player.sendMessage(getErrorChatColor() + "Maks minne: " + maxMem());
            player.sendMessage(getOkChatColor() + "Ledig minne: " + freeMem());
        } else {
            return false;
        }
        return true;
    }

    public long usedMem() {
        return ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L);
    }

    public long freeMem() {
        return ((Runtime.getRuntime().freeMemory() / 1024L) / 1024L);
    }

    public long maxMem() {
        return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
    }

    public long totalMem() {
        return Runtime.getRuntime().totalMemory() / 1024L / 1024L;
    }
}