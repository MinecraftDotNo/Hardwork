package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenInvCommand extends MinecraftnoCommand {

    private final Minecraftno plugin;
    private final UserHandler userHandler;

    public OpenInvCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(3);
        this.userHandler = instance.getUserHandler();
        this.plugin = instance;
    }

    @Override
    public final boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player target = this.plugin.playerMatch(args[0]);
            if (target == null) {
                player.sendMessage(getErrorChatColor() + "Spiller '" + args[0] + "' er ikke på serveren!");
                return true;
            }

            if (target == player) {
                player.sendMessage(getErrorChatColor() + "Du kan ikke åpne din egen!");
                return true;
            }

            if (this.userHandler.getAccess(target) > 3) {
                player.sendMessage(getErrorChatColor() + target.getDisplayName() + "'s inventory er beskyttet!");
                return true;
            }

            if (target.getWorld() != player.getWorld()) {
                player.sendMessage(getErrorChatColor() + target.getDisplayName() + " Spiller er ikke i samme verden!");
                return true;
            }

            //EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
            //EntityPlayer entitytarget = ((CraftPlayer) target).getHandle();
            //replaceInv((CraftPlayer) target);

            //entityplayer.openContainer(entitytarget.inventory);

            Inventory inv = target.getInventory();
            player.openInventory(inv);

            return true;
        } else {
            return false;
        }
    }

    /**public final static void replaceInv(CraftPlayer player) {
     try {
     //EntityPlayer entityplayer = player.getHandle();
     //entityplayer.defaultContainer = new ContainerPlayer(entityplayer.inventory, !entityplayer.world.isStatic);
     //entityplayer.activeContainer = entityplayer.defaultContainer;
     try {
     //entityplayer.syncInventory();

     } catch(Exception e){

     }
     //entityplayer.a(entityplayer.activeContainer, entityplayer.activeContainer.b());
     //entityplayer.activeContainer.a();
     //entityplayer.defaultContainer.a();
     //player.setHandle(entityplayer);

     } catch (Exception localException) {

     }
     }**/
}