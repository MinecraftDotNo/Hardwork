package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class RulesCommand extends MinecraftnoCommand {

    public RulesCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(0);
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(getCommandChatColor() + "======================[ " + getInfoChatColor() + "Regler" + getCommandChatColor() + "]===================");
            player.sendMessage(getErrorChatColor() + "Oppfør deg greit, møt Staben/Vakter med respekt.");
            player.sendMessage(" ");
            player.sendMessage(getCommandChatColor() + "1. Sjikane, banning og spam godtas ikke.");
            player.sendMessage(getCommandChatColor() + "2. Vandalisme og stjeling er strengt forbudt.");
            player.sendMessage(getCommandChatColor() + "3. Unødvendig utvidelse av kartet er ikke lov.");
            player.sendMessage(getCommandChatColor() + "4. Det er ulovlig å utnytte bugs.");

            return true;
        } else {
            return false;
        }
    }
}