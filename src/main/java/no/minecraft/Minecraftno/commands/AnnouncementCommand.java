package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AnnouncementCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private HashMap<String, Integer> tasks;

    public AnnouncementCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.plugin = instance;
        this.tasks = new HashMap<String, Integer>();
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("fjern")) {
                for (Integer value : tasks.values()) {
                    this.plugin.getServer().getScheduler().cancelTask(value);
                }
                this.tasks.clear();
                player.sendMessage(getInfoChatColor() + "Fjernet annonsering");
                return true;
            }
            if (!canParse(args[0])) {
                this.invalidInt(player, args[0]);
                return false;
            }
            String prefix = ChatColor.WHITE + "(" + getOkChatColor() + "Tips" + getVarChatColor() + ") ";
            StringBuilder build = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                build.append(args[i] + " ");
            }
            String msg = build.toString();
            String sendmsg = prefix + getVarChatColor() + msg;
            Integer min = (Integer.parseInt(args[0]) * 60 * 20);
            int id = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new RunAnnouncement(this.plugin, sendmsg), 0L, min.longValue());
            this.tasks.put(msg, Integer.valueOf(id));
        } else {
            return false;
        }
        return true;
    }

    public final void invalidInt(Player p, String i) {
        p.sendMessage(getErrorChatColor() + "Ikke et tall: " + i);
    }

    public final static boolean canParse(String i) {
        try {
            Integer.valueOf(Integer.parseInt(i));
            return true;
        } catch (NumberFormatException ne) {

        }
        return false;
    }

    public static class RunAnnouncement implements Runnable {
        private final Minecraftno plugin;
        private final UserHandler userHandler;
        private String sendmsg;

        public RunAnnouncement(Minecraftno instance, String sendmsg) {
            this.plugin = instance;
            this.userHandler = instance.getUserHandler();
            this.sendmsg = sendmsg;
        }

        public void run() {
            TipsPlayer();
        }

        private void TipsPlayer() {
            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                if (this.userHandler.getAnnonseringer(reciever)) {
                    reciever.sendMessage(sendmsg);
                }
            }
        }
    }
}
