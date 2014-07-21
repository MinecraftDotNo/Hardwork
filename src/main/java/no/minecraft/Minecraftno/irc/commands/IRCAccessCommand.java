package no.minecraft.Minecraftno.irc.commands;

import no.minecraft.Minecraftno.Minecraftno;
import org.jibble.pircbot.PircBot;

public class IRCAccessCommand implements IRCBotCommand {
    private Minecraftno plugin;

    public IRCAccessCommand(Minecraftno plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandName() {
        return "access";
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        String[] args = new String[0];
        if (!message.isEmpty())
            args = message.split(" ");

        if (args.length == 0) {
            bot.sendMessage(sender, "Ingen adgangskode angitt. Skriv /irc in-game for å få tildelt en.");
            return;
        }

        if (!args[0].equals(this.plugin.getIrcBot().getAccessCode(sender))) {
            this.plugin.getLogger().info("Access code for " + sender + " is " + this.plugin.getIrcBot().getAccessCode(sender) + ", " + args[0] + " given.");
            bot.sendMessage(sender, "Adgangskoden stemmer ikke over ens med den knyttet til ditt nick. Husk at du må ha samme navn på IRC som du har in-game.");
            return;
        }

        this.plugin.getIrcBot().promote(sender);

        bot.sendMessage(sender, "Din tilgang er oppdatert. Forsikre deg om at du er registrert med NickServ om du støter på problemer (/msg NickServ help register).");
    }
}
