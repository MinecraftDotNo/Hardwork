package no.minecraft.hardwork;

import java.util.UUID;

public class User {
    private final int id;
    private final UUID uuid;
    private final String name;
    private final int accessLevel;
    
    /* Non-final */
    private boolean working;

    public User(int id, UUID uuid, String name, int accessLevel) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.accessLevel = accessLevel;
    }

    public int getId() {
        return this.id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getAccessLevel() {
        return this.accessLevel;
    }

    /**
     * Indicates if User is currently in /work
     * @return
     */
    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }
}
