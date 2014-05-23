package no.minecraft.Minecraftno.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.WEBridge;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SetAreaBPCommand extends MinecraftnoCommand {
    private final WEBridge weBridge;
    private final UserHandler userHandler;
    private final static int maxSelection = 500000;

    public SetAreaBPCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.weBridge = instance.getWeBridge();
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        Selection sel = this.weBridge.getWePlugin().getSelection(player);
        if (sel == null) {
            player.sendMessage(getErrorChatColor() + "Du har ikke valgt et område.");
            return true;
        }
        if (sel.getArea() > maxSelection) {
            player.sendMessage(getErrorChatColor() + "Kan ikke beskytte mer enn " + maxSelection + " blokker om gangen. Du prøvde å beskytte: " + getVarChatColor() + sel.getArea());
            return true;
        }
        player.sendMessage(getOkChatColor() + "Prøver å beskytte område: " + getVarChatColor() + sel.getNativeMaximumPoint().toString() + "  " + sel.getNativeMinimumPoint().toString());
        // printSelection(sel,player);
        String newOwner = UserHandler.SERVER_USERNAME;
        if (args.length > 0) {
            newOwner = args[0];
            int exists = this.userHandler.getUserId(newOwner);
            if (exists == -1) {
                player.sendMessage(getErrorChatColor() + "Spilleren eksisterer ikke!");
                return true;
            }
            if (args.length > 1) {
                Set<Integer> ids = getId(args[1]);
                if (ids == null) {
                    player.sendMessage(getErrorChatColor() + "'" + args[1] + "' er ikke et tall!");
                    return false;
                }
                for (Integer id : ids) {
                    if (id == 0) {
                        player.sendMessage(getErrorChatColor() + "Luft kan ikke bli beskyttet.");
                        return false;
                    }
                }
                this.weBridge.setArea(sel, this.userHandler.getUserId(player), this.userHandler.getUserId(newOwner), 0, ids);
                return true;
            }
        }
        this.weBridge.setArea(sel, this.userHandler.getUserId(player), this.userHandler.getUserId(newOwner), 0, null);
        return true;
    }

    public Set<Integer> getId(String list) {
        String[] items = list.split(",");
        Set<Integer> getId = new HashSet<Integer>();
        for (String id : items) {
            try {
                getId.add(Integer.parseInt(id));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return getId;
    }
}
