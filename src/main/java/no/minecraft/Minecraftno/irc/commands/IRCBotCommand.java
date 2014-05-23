package no.minecraft.Minecraftno.irc.commands;

import org.jibble.pircbot.PircBot;

public interface IRCBotCommand {

    public String getCommandName();

    public void handleMessage(PircBot bot, String channel, String sender, String message);
}