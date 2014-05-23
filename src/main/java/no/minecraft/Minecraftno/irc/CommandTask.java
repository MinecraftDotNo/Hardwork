package no.minecraft.Minecraftno.irc;

import no.minecraft.Minecraftno.irc.commands.IRCBotCommand;

public class CommandTask implements Runnable {
    private final IRCBot ircBot;
    private final String channel;
    private final String sender;
    private final String message;
    private final IRCBotCommand command;

    public CommandTask(IRCBot ircBot, String channel, String sender, String message, IRCBotCommand command) {
        this.ircBot = ircBot;
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.command = command;
    }

    @Override
    public void run() {
        this.command.handleMessage(this.ircBot, this.channel, this.sender, this.message.replaceFirst(command.getCommandName(), "").trim());
    }
}
