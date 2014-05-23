package no.minecraft.Minecraftno.handlers.enums;

public enum BlockLogReason {
    ADMINSTICKED("adminsticked"),
    PLASSERTE("plasserte"),
    FJERNET("fjernet"),
    BUCKETTAKE("bucket tatt"),
    BUCKETUSE("bucket brukt"),
    CHEST("chest"),
    PROTECTED("beskyttet"),
    UNPROTECTED("fjernet beskyttelse"),
    CHANGEOWNER("endret eier");

    private BlockLogReason(String name) {
        this.name = name;
    }

    private final String name;

    public String toString() {
        return name;
    }
}