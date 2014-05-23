package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.conf.ConfigurationServer;
import no.minecraft.Minecraftno.conf.ConfigurationWorld;
import no.minecraft.Minecraftno.handlers.LogHandler;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class MinecraftnoCommand implements CommandExecutor {
    protected final Minecraftno plugin;

    private ChatColor errorChatColor;
    private ChatColor warningChatColor;
    private ChatColor okChatColor;
    private ChatColor confirmChatColor;
    private ChatColor varChatColor;
    private ChatColor defaultChatColor;
    private ChatColor commandChatColor;
    private ChatColor infoChatColor;
    private ChatColor argChatColor;
    private ChatColor privatChatColor;

    private int accessLevel;

    protected UserHandler userHandler;
    protected LogHandler logHandler;

    public MinecraftnoCommand(Minecraftno instance) {
        this.plugin = instance;

        this.userHandler = instance.getUserHandler();
        this.logHandler = instance.getLogHandler();

        this.setErrorChatColor(ChatColor.RED);
        this.setWarningChatColor(ChatColor.YELLOW);
        this.setOkChatColor(ChatColor.GREEN);
        this.setConfirmChatColor(ChatColor.DARK_GREEN);
        this.setVarChatColor(ChatColor.WHITE);
        this.setDefaultChatColor(ChatColor.DARK_GREEN);
        this.setCommandChatColor(ChatColor.GOLD);
        this.setInfoChatColor(ChatColor.BLUE);
        this.setArgChatColor(ChatColor.GRAY);
        this.setPrivatChatColor(ChatColor.AQUA);

        this.setAccessLevel(4);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MinecraftnoCommand.isPlayer(sender)) {
            return false;
        }

        Player player = (Player) sender;

        ConfigurationServer cfg = this.plugin.getGlobalConfiguration();
        ConfigurationWorld wcfg = cfg.get(player.getWorld());

        if (!wcfg.isCommandEnabled(command.getName())) {
            player.sendMessage(getErrorChatColor() + "Denne kommandoen er slått av!");
            return true;
        }

        int accessLevel = this.userHandler.getAccess(player);
        if (accessLevel < getAccessLevel() && !player.isOp()) {
            if (accessLevel == 0) {
                player.sendMessage(this.getErrorChatColor() + "Du må registrere deg for å bruke denne kommandoen.");
            } else {
                player.sendMessage(this.getErrorChatColor() + "Du har ikke tillatelse til å bruke denne kommandoen.");
            }
            return true;
        }

        return onPlayerCommand(player, command, label, args);
    }

    public abstract boolean onPlayerCommand(Player player, Command command, String label, String[] args);

    public static boolean isPlayer(final CommandSender sender) {
        return sender instanceof Player;
    }

    public final void setAccessLevel(final int userLevel) {
        this.accessLevel = userLevel;
    }

    public final int getAccessLevel() {
        return this.accessLevel;
    }

    public final void setErrorChatColor(final ChatColor errorChatColor) {
        this.errorChatColor = errorChatColor;
    }

    public final ChatColor getErrorChatColor() {
        return this.errorChatColor;
    }

    public final void setWarningChatColor(final ChatColor warningChatColor) {
        this.warningChatColor = warningChatColor;
    }

    public final ChatColor getWarningChatColor() {
        return this.warningChatColor;
    }

    public final void setOkChatColor(final ChatColor okChatColor) {
        this.okChatColor = okChatColor;
    }

    public final ChatColor getOkChatColor() {
        return this.okChatColor;
    }

    public final void setConfirmChatColor(final ChatColor confirmChatColor) {
        this.confirmChatColor = confirmChatColor;
    }

    public final ChatColor getConfirmChatColor() {
        return this.confirmChatColor;
    }

    public final Minecraftno getInstance() {
        return this.plugin;
    }

    public final void setVarChatColor(final ChatColor varChatColor) {
        this.varChatColor = varChatColor;
    }

    public final ChatColor getVarChatColor() {
        return this.varChatColor;
    }

    public final void setDefaultChatColor(final ChatColor defaultChatColor) {
        this.defaultChatColor = defaultChatColor;
    }

    public final ChatColor getDefaultChatColor() {
        return this.defaultChatColor;
    }

    public final void setCommandChatColor(final ChatColor commandChatColor) {
        this.commandChatColor = commandChatColor;
    }

    public final ChatColor getCommandChatColor() {
        return this.commandChatColor;
    }

    public final void setInfoChatColor(final ChatColor infoChatColor) {
        this.infoChatColor = infoChatColor;
    }

    public final ChatColor getInfoChatColor() {
        return this.infoChatColor;
    }

    public final void setArgChatColor(final ChatColor argChatColor) {
        this.argChatColor = argChatColor;
    }

    public final ChatColor getArgChatColor() {
        return this.argChatColor;
    }

    public final void setPrivatChatColor(final ChatColor privatChatColor) {
        this.privatChatColor = privatChatColor;
    }

    public final ChatColor getPrivatChatColor() {
        return this.privatChatColor;
    }
}
