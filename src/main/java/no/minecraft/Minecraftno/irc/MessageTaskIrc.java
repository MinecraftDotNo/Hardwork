package no.minecraft.Minecraftno.irc;

import org.jibble.pircbot.PircBot;

public class MessageTaskIrc implements Runnable {
    private PircBot bot;
    private String channel;
    private String message;

    public MessageTaskIrc(PircBot bot, String channel, String message) {
        super();
        this.message = message;
        this.bot = bot;
        this.channel = channel;
    }

    @Override
    public void run() {
        bot.sendMessage(channel, (message));
    }
}
