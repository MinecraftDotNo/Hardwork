package no.minecraft.Minecraftno.irc.commands;

import org.jibble.pircbot.PircBot;

import java.util.Date;

public class IRCTimeCommand implements IRCBotCommand {

    public String getCommandName() {
        return "time";
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        bot.sendMessage(channel, sender + ": Kl er nå " + (new Date()));
    }
}
