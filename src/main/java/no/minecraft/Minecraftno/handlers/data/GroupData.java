package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.ChatColor;

public class GroupData {

    private String groupName = "";
    private String groupOwner = "";

    //bank
    private int bankOn = 0;
    private int bank = 0;

    //invite
    private int inviteOn = 0;

    //public group
    private int publicOn = 0;

    //Color
    private ChatColor chatColor = ChatColor.WHITE;

    public GroupData(String groupName) {
        this.setGroupName(groupName);
    }

    /**
     * @return the bankOn
     */
    public boolean isBankOn() {
        return bankOn != 0;
    }

    /**
     * @param bankOn the bankOn to set
     */
    public void setBankOn(int bankOn) {
        this.bankOn = bankOn;
    }

    /**
     * @return the bank
     */
    public int getBank() {
        return bank;
    }

    /**
     * @param bank the bank to set
     */
    public void setBank(int bank) {
        this.bank = bank;
    }

    /**
     * @return the inviteOn
     */
    public boolean getPublicInviteOn() {
        return inviteOn != 0;
    }

    /**
     * @param inviteOn the inviteOn to set
     */
    public void setInviteOn(int inviteOn) {
        this.inviteOn = inviteOn;
    }

    /**
     * @return the publicOn
     */
    public boolean isGroupPublic() {
        return publicOn != 0;
    }

    /**
     * @param publicOn the publicOn to set
     */
    public void setPublicOn(int publicOn) {
        this.publicOn = publicOn;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the groupOwner
     */
    public String getGroupOwner() {
        return groupOwner;
    }

    /**
     * @param groupOwner the groupOwner to set
     */
    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    /**
     * @param color Fargen chat i gruppen skal ha
     */
    public void setChatColor(ChatColor color) {
        this.chatColor = color;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }
}
