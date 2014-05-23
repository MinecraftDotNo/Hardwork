package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class HomeCommand extends MinecraftnoCommand {

    private final UserHandler userHandler;

    public HomeCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(2);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Location homeLoc = this.userHandler.getOnlineUsers().get(player).getHome();
            if (homeLoc != null) {
                this.userHandler.setTeleportBackLocation(player, player.getLocation());
                player.teleport(homeLoc);
            } else {
                player.sendMessage(getErrorChatColor() + "Du har ikke satt et home enda. Bruk /sethome for å sette.");
            }
        } else {
            return false;
        }
        return true;
    }
}