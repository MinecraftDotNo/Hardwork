package no.minecraft.Minecraftno.handlers.data;

public class BlockData {

    private int type;
    private String statment;
    private int playerId;
    private int blockTypeId;
    private int blockX;
    private int blockY;
    private int blockZ;
    private String world;
    private String blocklogreason;

    public BlockData(int type, String Statment, int playerId, int blockTypeId, int blockX, int blockY, int blockZ, String world, String blocklogreason) {
        this.type = type;
        this.statment = Statment;
        this.playerId = playerId;
        this.blockTypeId = blockTypeId;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.world = world;
        this.blocklogreason = blocklogreason;
    }

    public int getType() {
        return this.type;
    }

    public String getStatment() {
        return this.statment;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getBlockTypeId() {
        return this.blockTypeId;
    }

    public int getBlockX() {
        return this.blockX;
    }

    public int getBlockY() {
        return this.blockY;
    }

    public int getBlockZ() {
        return this.blockZ;
    }

    public String getWorld() {
        return this.world;
    }

    public String getBlockLogReason() {
        return this.blocklogreason;
    }
}
