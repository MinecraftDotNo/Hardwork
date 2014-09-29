package no.minecraft.Minecraftno.handlers.player;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.handlers.GroupLogHandler;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.data.PlayerData;
import no.minecraft.Minecraftno.handlers.enums.MinecraftnoLog;
import no.minecraft.Minecraftno.handlers.scheduler.KickPlayerThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final LogHandler logHandler;
    private final GroupLogHandler groupLogHandler;

    public ChatHandler(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.logHandler = instance.getLogHandler();
        this.groupLogHandler = instance.getGroupLogHandler();
    }

    private final boolean removeAdvertise(Player player, String msg) {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        for (int i = cfg.advertise.size() - 1; i >= 0; i--) {
            if (msg.contains(cfg.advertise.get(i))) {
                sendMessageToAccessLevel(2, ChatColor.RED + player.getName() + " reklamerte: " + msg);
                this.plugin.getIrcBot().sendMessage("#hardwork.logg", player.getName() + " ble kicket for reklamering: " + msg);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du ble kicket for reklamering."));
                for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                    if (entry.getValue().getAnnonseringer()) {
                        entry.getKey().sendMessage(ChatColor.GRAY + player.getName() + " ble kicket for reklamering.");
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean isRepeat(Player player, String msg) {
        if (this.userHandler.getOnlineUsers().get(player).getAccessLevel() < 2) {
            if (msg.length() > 15) {
                if ((this.userHandler.getLastMsgGet(player) != null) && (msg.equalsIgnoreCase(this.userHandler.getLastSendtMsg(player)))) {
                    int ars = this.userHandler.getAmountRepeatString(player);
                    if (ars > 1) {
                        this.userHandler.setAmountRepeatString(player, 0);
                        if (this.userHandler.getLastMessageTime(player) != 0) {
                            if ((System.currentTimeMillis() - this.userHandler.getLastMessageTime(player)) < 30000) {
                                return true;
                            }
                        }
                    } else {
                        this.userHandler.setAmountRepeatString(player, ars + 1);
                    }
                }
            }
        }
        return false;
    }

    private boolean isSlowMode(Player player, String msg) {
        if (this.userHandler.getOnlineUsers().get(player).getAccessLevel() < 2) {
            long curtime = System.currentTimeMillis();
            int playeronline = +Bukkit.getServer().getOnlinePlayers().size();
            if (this.userHandler.getSlowMode(player) != 0) {
                if ((((this.userHandler.getSlowMode(player) + (10000 + (100 * playeronline))) - System.currentTimeMillis()) / 1000) > 0) {
                    player.sendMessage(ChatColor.RED + "Du må vente " + (((this.userHandler.getSlowMode(player) + (10000 + (100 * playeronline))) - System.currentTimeMillis()) / 1000) + " sekunder før du kan prate i hovedchat.");
                    return true;
                }
                this.userHandler.setSlowMode(player, 0);
                return false;
            }

            if (this.userHandler.getLastMessageTime(player) != 0) {
                if ((curtime - this.userHandler.getLastMessageTime(player)) < (1000 + (100 * playeronline))) {
                    this.userHandler.setSlowMode(player, curtime);
                    return false;
                }
            }
            this.userHandler.setLastMessageTime(player, curtime);
            return false;
        }
        return false;
    }

    private String checkChatMessage(Player player, String msg) {
        if (this.userHandler.getOnlineUsers().get(player).getAccessLevel() < 2) {
            // Trim and remove colors.
            msg = trimString(msg);
            msg = removeColor(msg);
            msg = removeEmail(msg);
        }
        return msg;
    }

    private boolean isChatOK(Player player, String msg) {
        if (this.userHandler.getOnlineUsers().get(player).getAccessLevel() < 2) {
            // check for IP
            if (isIP(msg)) {
                //player.kickPlayer("Du vart kicket for bruk av ip-adresser i chat. (Reklamering)");
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du ble kicket for bruk av IP-adresser i chat. (Reklamering)"));
                for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                    if (entry.getValue().getAnnonseringer()) {
                        entry.getKey().sendMessage(ChatColor.GRAY + player.getName() + " ble kicket for bruk av ip-adresse i chat. (Reklamering)");
                    }
                }
                return false;
            }

            if (removeAdvertise(player, msg)) {
                return false;
            }
        }
        return true;
    }

    private String removeEmail(String msg) {
        String pattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(msg);

        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String replacement = m.group().replaceAll(pattern, ChatColor.RED + "Epost er fjernet." + ChatColor.RESET);
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private String trimString(String msg) {
        return msg.replaceAll("\\s+", " ").trim();
    }

    private String removeColor(String msg) {
        return msg.replaceAll(ChatColor.COLOR_CHAR + ".", "");
    }

    private boolean isIP(String msg) {
        String IpAndPort = "\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?";
        String Ip = "\\d{1,3}(?:\\.\\d{1,3}){3}";

        Pattern compiledPattern = Pattern.compile(IpAndPort);
        Matcher matcher = compiledPattern.matcher(msg);
        if (matcher.find()) {
            return true;
        }

        compiledPattern = Pattern.compile(Ip);
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    private boolean isCapsLock(String msg) {
        msg = msg.replaceAll("[^\\p{L}\\p{N}]", "");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            StringBuffer sb = new StringBuffer();
            Pattern compiledPattern = Pattern.compile(player.getName(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = compiledPattern.matcher(msg);
            while (matcher.find()) {
                String replacement = matcher.group().toUpperCase().replace(player.getName().toUpperCase(), "");
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            msg = sb.toString();
        }

        if (msg.length() < 10) {
            return false;
        }

        double uppercaseCharacters = 0.0D;

        for (char c : msg.toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCharacters += 1.0D;
            }
        }
        double percent = uppercaseCharacters / msg.length() * 100.0D;
        return percent > 80.0D;
    }

    private boolean isChatLock(Player player, String msg) {
        if (this.userHandler.getLastMsgLock(player) != null) {
        	Player target = this.plugin.playerMatch(this.userHandler.getLastMsgLock(player));
            if (target != null) {
                sendPrivatMessage(player, target, msg);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Spilleren du har bindet chat til er offline. Fjerne nå dette automatisk");
                this.userHandler.getPlayerData(player).setlastmsgLock(null);
                return true;
            }
        } else if (this.userHandler.getGroupChatBind(player)) {
            if (this.plugin.getGroupHandler().isInGroup(player)) {
                sendGroupMessage(player, msg);
                return true;
            } else {
                this.userHandler.setGroupChatBind(player, false);
                return true;
            }
        }
        return false;
    }

    /**
     * Highlight nick in string.
     *
     * @param player   The player
     * @param msg      The message
     * @param endColor The color to put after highlighting. <code>null</b> if RESET.
     *
     * @return
     */
    public String nameHighlight(Player player, String msg, ChatColor endColor) {
        Pattern p = Pattern.compile(player.getName(), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);

        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String replacement = m.group().toUpperCase().replace(player.getName().toUpperCase(), ChatColor.YELLOW + player.getName() + (endColor == null ? ChatColor.RESET : endColor));
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public final void sendAccessMessage(String target, String msg) {
        if (this.plugin.getIrcBot() == null) {
            Minecraftno.log.severe("IRC bot offline");
        } else {
            this.plugin.getIrcBot().sendMessage(target, ": " + msg);
        }
    }

    public final void sendIRCMessage(Player player, String target, String msg) {
        if (this.plugin.getIrcBot() == null) {
            Minecraftno.log.severe("IRC bot offline");
        } else {
            if (this.plugin.getNisseHandler().isNisse(player)) {
                this.plugin.getIrcBot().sendMessage(target, "Nissen: " + msg);
            } else {
                this.plugin.getIrcBot().sendMessage(target, this.userHandler.getIRCPrefix(this.userHandler.getAccess(player)) + player.getName() + ": " + msg);
            }
        }
    }

    public final void sendIRCPM(Player player, Player target, String msg) {
        if (this.plugin.getIrcBot() == null) {
            Minecraftno.log.severe("IRC bot offline");
        } else {
            this.plugin.getIrcBot().sendMessage("#hardwork.pm", "[" + player.getName() + "] til [" + target.getName() + "] " + ": " + msg);
        }
    }

    public final void sendVaktStabMessage(Player player, String msg) {
        sendIRCMessage(player, "#hardwork.styret", msg);
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getAccessLevel() >= 2 && !entry.getValue().hasAdminChatDeactivated()) {
                entry.getKey().sendMessage("(" + ChatColor.GOLD + "Stab" + ChatColor.WHITE + "/" + ChatColor.BLUE + "Vakt" + ChatColor.WHITE + ") (" + player.getName() + ") " + nameHighlight(entry.getKey(), msg, null));
            }
        }
    }

    public final void sendTradeChatMessage(Player player, String msg) {
        if (this.userHandler.getMute(player)) {
            player.sendMessage(ChatColor.RED + "Du kan ikke snakke siden du er muted.");
            return;
        }

        msg = checkChatMessage(player, msg);

        if (!isChatOK(player, msg)) {
            return;
        }

        if (isSlowMode(player, msg)) {
            return;
        }

        if (isRepeat(player, msg)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du vart kicket for repeat av samme setningen i chat. (Spam)"));
            //player.kickPlayer("Du vart kicket for repeat av samme setningen i chat. (Spam)");
            for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                if (entry.getValue().getAnnonseringer()) {
                    entry.getKey().sendMessage(ChatColor.GRAY + player.getName() + " ble kicket for repeat av setning i chat. (Spam)");
                }
            }
            return;
        }

        if (isCapsLock(msg)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du vart kicket for overdrevet bruk av Capslock."));
            //player.kickPlayer("Du vart kicket for overdrevet bruk av Capslock.");
            for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                if (entry.getValue().getAnnonseringer()) {
                    entry.getKey().sendMessage(ChatColor.GRAY + player.getName() + " ble kicket for overdrevet bruk av Capslock. (Spam)");
                }
            }
            return;
        }

        sendIRCMessage(player, "#hardwork.handel", msg);
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getTradeChat()) {
                entry.getKey().sendMessage(ChatColor.GREEN + "[Kjøp/Salg] " + ChatColor.WHITE + player.getName() + ": " + nameHighlight(entry.getKey(), msg, null));
            }
        }
        this.logHandler.log(this.userHandler.getUserId(player), 0, 0, 0, msg, MinecraftnoLog.TRADECHAT);
    }

    public final void sendMainChatMessage(Player player, String msg) {
        if (this.userHandler.getMute(player)) {
            player.sendMessage(ChatColor.RED + "Du kan ikke snakke siden du er mutet. Kontakt en vakt eller stab for å bli unmutet.");
            return;
        }

        msg = checkChatMessage(player, msg);

        // Deny use of MineChat.
        if ((msg.startsWith("connected with an") || msg.startsWith("Koblet til med")) && msg.endsWith("MineChat")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "MineChat er blokkert. Bruk heller IRC: irc.minecraft.no #hardwork"));
            return;
        }

        if (!isChatOK(player, msg)) { // checks for ip addresses
            return;
        }

        if (isChatLock(player, msg)) {
            return;
        }
        /*
        if (isSlowMode(player, msg)) {
			return;
		}
		
		if (isRepeat(player, msg)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du vart kicket for repeat av samme setningen i chat. (Spam)"));
			//player.kickPlayer("Du vart kicket for repeat av samme setningen i chat. (Spam)");
    		for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
				if (entry.getValue().getAnnonseringer()) {
					entry.getKey().sendMessage(ChatColor.GRAY + player.getName()+ " ble kicket for repeat av setning i chat. (Spam)");
				}
			}
			return;
		}
		
		if (isCapsLock(msg)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new KickPlayerThread(player, "Du vart kicket for overdrevet bruk av Capslock."));
			//player.kickPlayer("Du vart kicket for overdrevet bruk av Capslock.");
			for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
				if (entry.getValue().getAnnonseringer()) {
					entry.getKey().sendMessage(ChatColor.GRAY + player.getName()+ " ble kicket for overdrevet bruk av Capslock. (Spam)");
				}
			}
			return;
		}*/

        if (!this.userHandler.getHovedChat(player)) {
            player.sendMessage(ChatColor.RED + "Du kan ikke snakke i hovedchat uten og ha den aktivert. For å skru den på: /chat on");
            return;
        }

        this.userHandler.setLastSendtMsg(player, msg);
        sendIRCMessage(player, "#hardwork", msg);
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getHovedChat()) {
                entry.getKey().sendMessage(player.getDisplayName() + ": " + ChatColor.WHITE + nameHighlight(entry.getKey(), msg, null));
            }
        }
        this.logHandler.log(this.userHandler.getUserId(player), 0, 0, 0, msg, MinecraftnoLog.CHAT);
    }

    public final void sendPrivatMessage(Player player, Player target, String msg) {
        if (!isChatOK(player, msg)) {
            return;
        }

        msg = checkChatMessage(player, msg);

        this.userHandler.setLastSendtMsg(player, msg);
        sendIRCPM(player, target, msg);
        target.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] til [" + target.getName() + "] " + nameHighlight(target, msg, ChatColor.AQUA));
        player.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] til [" + target.getName() + "] " + nameHighlight(player, msg, ChatColor.AQUA));
        this.logHandler.log(this.userHandler.getUserId(player), this.userHandler.getUserId(target), 0, 0, msg, MinecraftnoLog.MELDING);
        this.userHandler.setLastMsgSend(player, target.getName());
        this.userHandler.setLastMsgGet(target, player.getName());
    }

    public final void sendMessageToAccessLevel(int accessLevel, String msg) {
        for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
            if (entry.getValue().getAccessLevel() > accessLevel) {
                entry.getKey().sendMessage(msg);
            }
        }
    }

    public void sendGroupMessage(Player player, String groupMessage) {
        if (!isChatOK(player, groupMessage)) {
            return;
        }

        groupMessage = checkChatMessage(player, groupMessage);
        int groupID = this.plugin.getGroupHandler().getGroupID(player);
        if (groupID != 0) {
            ChatColor color = this.plugin.getGroupHandler().getGroupData(groupID).getChatColor();
            String groupName = this.plugin.getGroupHandler().getGroupData(groupID).getGroupName();
            this.plugin.getIrcBot().sendMessage("#hardwork.pm", Colors.GREEN + "[" + groupName + "] " + "(" + player.getName() + ") " + groupMessage);
            for (Entry<Player, PlayerData> entry : this.userHandler.getOnlineUsers().entrySet()) {
                if (entry.getValue().getGroupId() == groupID) {
                    nameHighlight(entry.getKey(), groupMessage, color);
                    entry.getKey().sendMessage(ChatColor.WHITE + "(" + ChatColor.DARK_GREEN + groupName + ChatColor.WHITE + ") " + player.getName() + ": " + color + groupMessage);
                }
            }
            this.groupLogHandler.logGroupMessage(groupID, this.userHandler.getUserId(player), groupMessage);
        } else {
            player.sendMessage(ChatColor.RED + "Du har bindet chat til gruppe, men er ikke lengre i gruppen. Fjerne nå dette automatisk");
            this.userHandler.setGroupChatBind(player, false);
        }
    }

    public void rankBroadcastMessage(int rank, String message) {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.plugin.getUserHandler().getAccess(player) >= rank) {
                player.sendMessage(message);
            }
        }
    }
}

