package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ModCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    public final HashMap<String, Integer> map = new HashMap<String, Integer>();

    public ModCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(4);
        this.map.put("bruker", 1);
        this.map.put("pensjonist", 2);
        this.map.put("vakt", 3);
        this.map.put("stab", 4);
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player victim = this.plugin.playerMatch(args[0]);
            String promoteTo = args[1].toLowerCase();
            if (victim != null) {
                if (this.map.containsKey(promoteTo)) {
                    int accesslevel = this.map.get(promoteTo);
                    if ((userHandler.getAccess(player) >= accesslevel) || (player.isOp())) {
                        if (userHandler.getAccess(victim) >= 3) {
                            this.userHandler.removePermissions(victim);
                        }
                        if (userHandler.changeAccessLevel(victim, accesslevel)) {
                            this.plugin.getServer().broadcastMessage(getDefaultChatColor() + "Brukerstatusen til " + getVarChatColor() + victim.getName() + getDefaultChatColor() + " ble endret til " + getVarChatColor() + promoteTo + getDefaultChatColor() + " av " + getVarChatColor() + player.getName() + getDefaultChatColor() + ".");

                            return true;
                        } else {
                            player.sendMessage(getErrorChatColor() + "En feil oppstod. Fikk ikke endret brukerstatus.");
                            return true;
                        }
                    } else {
                        player.sendMessage(getErrorChatColor() + "Du kan ikke endre brukerstatusen til en med høyere status enn deg.");
                        return true;
                    }
                } else {
                    player.sendMessage(getVarChatColor() + promoteTo + getErrorChatColor() + " er ikke en gyldig brukerstatus.");
                    return true;
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Spilleren må være online.");
                return true;
            }
        } else {
            return false;
        }
    }
}