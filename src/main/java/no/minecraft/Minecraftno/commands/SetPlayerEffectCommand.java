package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class SetPlayerEffectCommand extends MinecraftnoCommand {

    HashMap<String, PotionEffectType> effects = new HashMap<String, PotionEffectType>();

    public SetPlayerEffectCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);

        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null) {
                this.effects.put(type.getName().replaceAll("_", "").toLowerCase(), type);
            }
        }
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("hjelp"))) {
            player.sendMessage("Kommandoer:");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "seteff hjelp" + getVarChatColor() + " - Viser denne listen.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "seteff list" + getVarChatColor() + " - Viser listen over gyldige potions.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "seteff " + getArgChatColor() + "[spiller] [effekt] [tid] [styrke]" + getVarChatColor() + " - Setter effekten beskrevet på spilleren.");

            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "seteff remove " + getArgChatColor() + "[spiller] [effekt/alle]" + getVarChatColor() + " - Fjern beskrevet effekt på spiller, for alle skriv 'alle'.");
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            StringBuilder output = new StringBuilder();
            for (String eff : this.effects.keySet()) {
                output.append(getVarChatColor() + eff + getDefaultChatColor() + ", ");
            }
            String utskrift = output.substring(0, output.length() - 2) + ".";
            player.sendMessage(getDefaultChatColor() + "Gyldige potions er: ");
            player.sendMessage(utskrift);
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            Player thePlayer = this.plugin.playerMatch(args[1]);
            if (thePlayer != null) {
                if (args[2].equalsIgnoreCase("alle")) {
                    for (PotionEffectType type : this.effects.values()) {
                        if (thePlayer.hasPotionEffect(type)) {
                            thePlayer.removePotionEffect(type);
                        }
                    }
                    player.sendMessage(getOkChatColor() + "Alle aktive potions ble fjernet fra spilleren " + getArgChatColor() + thePlayer.getName() + getOkChatColor() + ".");
                    thePlayer.sendMessage(getDefaultChatColor() + "Alle aktive potions ble fjernet fra deg.");
                    return true;
                } else {
                    String arg = args[2].toLowerCase();
                    if (this.effects.containsKey(arg)) {
                        thePlayer.removePotionEffect(this.effects.get(arg));
                        player.sendMessage(getOkChatColor() + "Potion " + getArgChatColor() + arg + getOkChatColor() + " ble fjernet fra spilleren " + getArgChatColor() + thePlayer.getName() + getOkChatColor() + ".");
                        thePlayer.sendMessage(getDefaultChatColor() + "Potion " + getArgChatColor() + arg + getDefaultChatColor() + " ble fjernet fra deg.");
                        return true;
                    } else {
                        player.sendMessage(getErrorChatColor() + "Effekten " + getArgChatColor() + args[2].toLowerCase() + getErrorChatColor() + " eksisterer ikke.");
                        return true;
                    }
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Spilleren " + getArgChatColor() + args[1] + getErrorChatColor() + " ble ikke funnet.");
                return true;
            }
        } else if (args.length >= 2) {
            Player thePlayer = this.plugin.playerMatch(args[0]);
            if (thePlayer != null) {

                Integer time = 0;
                Integer strength = 0;
                if (args.length >= 3) {
                    if (Minecraftno.canParse(args[2])) {
                        time = Integer.parseInt(args[2]);
                    } else {
                        invalidInt(player, args[2]);
                        return true;
                    }
                }
                if (args.length >= 4) {
                    if (Minecraftno.canParse(args[3])) {
                        strength = Integer.parseInt(args[3]);
                    } else {
                        invalidInt(player, args[3]);
                        return true;
                    }
                }
                String arg = args[1].toLowerCase();
                if (this.effects.containsKey(arg)) {
                    PotionEffect effect = new PotionEffect(this.effects.get(arg), (time > 0 ? (time * 20) : (5 * 20)), strength);
                    thePlayer.addPotionEffect(effect, false); // Reason for not force of the effect is if the player has this effect already.
                    player.sendMessage(getOkChatColor() + "Spilleren: " + getArgChatColor() + thePlayer.getPlayer().getName() + getOkChatColor() + " fikk potion: " + getVarChatColor() + arg);
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor() + "Effekten " + getArgChatColor() + arg + getErrorChatColor() + " eksisterer ikke.");
                    return true;
                }
            } else {
                player.sendMessage(getErrorChatColor() + "Spilleren " + getArgChatColor() + args[0] + getErrorChatColor() + " ble ikke funnet.");
                return true;
            }
        }
        return false;
    }

    public final void invalidInt(Player p, String i) {
        p.sendMessage(getErrorChatColor() + "Ikke et tall: " + i);
    }
}
