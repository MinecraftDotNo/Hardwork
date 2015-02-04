package no.minecraft.Minecraftno.irc;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import no.minecraft.Minecraftno.irc.commands.*;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRCBot extends PircBot {

    private final Minecraftno plugin;
    private final UserHandler userHandler;
    private final static String commandprefix = "-";
    private final List<IRCBotCommand> commands = new ArrayList<IRCBotCommand>();
    private final Map<String, String> accessCodes = new HashMap<>();

    public IRCBot(Minecraftno instance) {
        this.plugin = instance;
        this.userHandler = this.plugin.getUserHandler();
        this.setName("H");
        this.setLogin("H");
        try {
            this.setEncoding("utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void ircBot() {
        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();

        if (cfg.irc) {
            this.setVerbose(false);
            this.setAutoNickChange(true);

            try {
                this.plugin.getIrcBot().connect(cfg.irchostname, cfg.ircport, new TrustingSSLSocketFactory());
            } catch (NickAlreadyInUseException e) {
                this.plugin.getLogger().info("IRC nick already in use!");
            } catch (IrcException e) {
                this.plugin.getLogger().severe("IRC exception! " + e.getMessage());
            } catch (IOException e) {
                this.plugin.getLogger().severe("IRC IO exception!");
            }

            if (this.isConnected()) {
                this.sendMessage("NickServ", "identify " + this.plugin.getConfig().getString("irc.nickserv_password"));

                try {
                    addCommands();

                    Thread.sleep(2000);

                    this.plugin.getIrcBot().joinChannel("#hardwork");
                    this.plugin.getIrcBot().joinChannel("#hardwork.styret");
                    this.plugin.getIrcBot().joinChannel("#hardwork.pm");
                    this.plugin.getIrcBot().joinChannel("#hardwork.logg");
                    this.plugin.getIrcBot().joinChannel("#hardwork.iplogg");
                    this.plugin.getIrcBot().joinChannel("#hardwork.handel");
                    this.plugin.getIrcBot().joinChannel("#hardwork.bank");
                } catch (InterruptedException e) {

                }
            } else {
                this.plugin.getLogger().info("IRC connection failed!");
            }
        }
    }

    public void addCommands() {
        commands.add(new IRCTimeCommand());
        commands.add(new IRCWhoCommand(this.plugin));
        commands.add(new IRCAddCommand(this.plugin));
        commands.add(new IRCKickCommand(this.plugin));
        commands.add(new IRCBanCommand(this.plugin));
        commands.add(new IRCUnbanCommand(this.plugin));
        commands.add(new IRCListWarningsCommand(this.plugin));
        commands.add(new IRCAccessCommand(this.plugin));
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.startsWith(commandprefix)) {
            message = message.replaceFirst(commandprefix, "");
            for (IRCBotCommand command : commands) {
                if (message.startsWith(command.getCommandName())) {
                    this.plugin.getServer().getScheduler().runTask(this.plugin, new CommandTask(this, channel, sender, message, command));
                }
            }
        } else {
            boolean getNames = true;
            while (true) {
                for (User user : this.getUsers(channel)) {
                    if (user.getNick().equalsIgnoreCase(sender) && !sender.equalsIgnoreCase("H2") && !sender.equalsIgnoreCase("H")) {
                        if (channel.equalsIgnoreCase("#hardwork.styret")) {
                            for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                                if (this.userHandler.getAccess(reciever) >= 2) {
                                    String msg = message.trim();
                                    reciever.sendMessage("(" + ChatColor.GOLD + "Stab" + ChatColor.WHITE + "/" + ChatColor.BLUE + "Vakt" + ChatColor.WHITE + ") (" + sender + ") " + msg);
                                }
                            }
                        } else if (channel.equalsIgnoreCase("#hardwork")) {
                            if (message.contains(("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))) {
                                sendMessage(channel, "Ingen reklame!");
                            } else {
                                String msg = message.replace('§', ' ').trim();
                                for (Player reciever : this.plugin.getServer().getOnlinePlayers()) {
                                    if (this.userHandler.getirc(reciever)) {
                                        reciever.sendMessage(ChatColor.GRAY + this.userHandler.getIrcToGamePrefix(user) + sender + ": " + ChatColor.WHITE + msg);
                                    }
                                }
                            }
                        }

                        return;
                    } else if (user.getNick().toLowerCase().contains(sender.toLowerCase())) {
                        this.plugin.getLogger().info("We are looking for " + sender + " on IRC, and found " + user.getNick() + ". Is that the droid we're looking for?");
                    }
                }

                if (getNames) {
                    this.sendRawLine("NAMES " + channel);
                    getNames = false;
                } else {
                    sendMessage(channel, "Beklager, men jeg klarte ikke å sende meldingen din, " + sender + ". Vennligst forlat kanalen, kom tilbake, og prøv igjen.");
                    return;
                }
            }
        }
    }

    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        super.onPrivateMessage(sender, login, hostname, message);

        if (message.startsWith(this.commandprefix)) {
            message = message.replaceFirst(this.commandprefix, "");
            String[] segments = message.split(" ");

            for (IRCBotCommand command : this.commands) {
                if (command.getCommandName().equalsIgnoreCase(segments[0])) {
                    this.plugin.getServer().getScheduler().runTask(
                        this.plugin,
                        new CommandTask(this, sender, sender, message, command)
                    );
                    return;
                }
            }
        }

        this.sendMessage(sender, "Du gjør meg nervøs :(");
    }

    public void onDisconnect() {
        while (!isConnected()) {
            try {
                reconnect();
            } catch (Exception e) {
                Minecraftno.log.info(String.format("[Minecraftno] Kunne ikke reconnecte til irc-serveren."));
            }
        }
    }

    @Override
    protected void onServerResponse(int code, String response) {

    }

    public String getAccessCode(String nick) {
        if (!this.accessCodes.containsKey(nick))
            this.accessCodes.put(nick, RandomStringUtils.randomAlphanumeric(8));

        return this.accessCodes.get(nick);
    }

    public void promote(String nick) {
        String group = "";

        switch (this.plugin.getUserHandler().getAccess(nick)) {
            case 2: group = "pensjonist"; break;
            case 3: group = "vakt"; break;
            case 4: group = "stab"; break;
            case 5: group = "tech"; break;
        }

        if (group.equals(""))
            return;

        this.sendMessage("GroupServ", "FLAGS !hardwork." + group + " " + nick + " +cmv");
    }
}
