package no.minecraft.Minecraftno.handlers.data;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.HashMap;

public class PlayerData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7252179218588562863L;

    private int accessLevel;
    private int groupId;
    private int userId;
    private int oldGroupID;
    private int afktaskid;
    private int repeatString = 0;

    private boolean mute = false;
    private boolean freeze = false;
    private boolean invisible = false;
    private boolean groupchatbind = false;
    private boolean adminchatdeactivated = false;
    private boolean tradechat = true;
    private boolean hovedchat = true;
    private boolean annonseringer = true;
    private boolean irc = true;
    private boolean inWork = false;
    
    private String uuid;

    //Location
    private HashMap<Integer, LocationData> location = new HashMap<Integer, LocationData>();

    private String toolData = null;
    private String lastmsgsend = null;
    private String lastmsgget = null;
    private String lastmsgLock = null;
    private String PlayerName = null;
    private String lastsendtmsg = null;
    private long lastMessageTime = 0;
    private long slowMode = 0;

    public long getSlowMode() {
        return slowMode;
    }

    public void setSlowMode(long slowmode) {
        this.slowMode = slowmode;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long LastMessageTime) {
        this.lastMessageTime = LastMessageTime;
    }

    public HashMap<Integer, LocationData> getLocation() {
        if (this.location == null) {
            this.location = new HashMap<Integer, LocationData>();
        }
        return this.location;
    }

    public Location getToolLocation1() {
        if (getLocation().containsKey(1)) {
            return getLocation().get(1).getLocation();
        }
        return null;
    }

    public void setToolLocation1(Location loc) {
        getLocation().put(1, new LocationData(loc.getWorld().getName(), loc.getX(), loc.getX(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public Location getToolLocation2() {
        if (getLocation().containsKey(2)) {
            return getLocation().get(2).getLocation();
        }
        return null;
    }

    public void setToolLocation2(Location loc) {
        getLocation().put(2, new LocationData(loc.getWorld().getName(), loc.getX(), loc.getX(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public String getToolData() {
        return toolData;
    }

    public void setToolData(String toolData) {
        this.toolData = toolData;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean getFreeze() {
        return freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public boolean getInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean getGroupChatBind() {
        return groupchatbind;
    }

    public void setGroupChatBind(boolean groupchatbind) {
        this.groupchatbind = groupchatbind;
    }

    public boolean hasAdminChatDeactivated() {
        return adminchatdeactivated;
    }

    public void setAdminChatDeactivated(boolean adminchat) {
        this.adminchatdeactivated = adminchat;
    }

    public boolean getTradeChat() {
        return tradechat;
    }

    public void setTradeChat(boolean tradechat) {
        this.tradechat = tradechat;
    }

    public boolean getHovedChat() {
        return hovedchat;
    }

    public void setHovedChat(boolean hovedchat) {
        this.hovedchat = hovedchat;
    }

    public boolean getAnnonseringer() {
        return annonseringer;
    }

    public void setAnnonseringer(boolean annonseringer) {
        this.annonseringer = annonseringer;
    }

    public boolean getirc() {
        return irc;
    }

    public void setirc(boolean irc) {
        this.irc = irc;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getlastmsgsend() {
        return lastmsgsend;
    }

    public void setlastmsgsend(String lastmsgsend) {
        this.lastmsgsend = lastmsgsend;
    }

    public String getlastmsgget() {
        return lastmsgget;
    }

    public void setlastmsgget(String lastmsgget) {
        this.lastmsgget = lastmsgget;
    }

    public String getlastmsgLock() {
        return lastmsgLock;
    }

    public void setlastmsgLock(String lastmsgLock) {
        this.lastmsgLock = lastmsgLock;
    }

    public String getlastsendtmsg() {
        return lastsendtmsg;
    }

    public void setlastsendtmsg(String lastsendtmsg) {
        this.lastsendtmsg = lastsendtmsg;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }

    public Location getTeleportBackLocation() {
        if (getLocation().containsKey(3)) {
            return getLocation().get(3).getLocation();
        }
        return null;
    }

    public void setTeleportBackLocation(Location loc) {
        getLocation().put(3, new LocationData(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public Location getHome() {
        if (getLocation().containsKey(4)) {
            return getLocation().get(4).getLocation();
        }
        return null;
    }

    public void setHome(Location loc) {
        getLocation().put(4, new LocationData(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public int getAfkTaskid() {
        return afktaskid;
    }

    public void setAfkTaskid(int afktaskid) {
        this.afktaskid = afktaskid;
    }

    public int getRepeatString() {
        return repeatString;
    }

    public void setRepeatString(int repeatString) {
        this.repeatString = repeatString;
    }

    public PlayerData(int accessLevel, int groupId) {
        setAccessLevel(accessLevel);
        setGroupId(groupId);
    }

    public PlayerData() {
        setAccessLevel(0);
        setGroupId(0);
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Brukes i forbindelse med force join av gruppe
     *
     * @param groupID
     */
    public void setOldGroupID(int groupID) {
        this.oldGroupID = groupID;
    }

    /**
     * Brukes i forbindelse med force join av gruppe
     *
     * @return
     */
    public int getOldGroupID() {
        return this.oldGroupID;
    }
    
    /**
     * Returns cached UUID of player.
     * @return string
     */
    public String getCachedUUID()
    {
    	return uuid;
    }
    
    /**
     * Set new cached UUID of player.
     * @param newuuid
     */
    public void setCachedUUID(String newuuid)
    {
    	this.uuid = newuuid;
    }

    public boolean isInWork() {
        return inWork;
    }

    public void setInWork(boolean inWork) {
        this.inWork = inWork;
    }
}
