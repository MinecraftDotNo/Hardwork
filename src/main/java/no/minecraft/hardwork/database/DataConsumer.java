package no.minecraft.hardwork.database;

import java.sql.SQLException;

public interface DataConsumer {
    public void prepareStatements() throws SQLException;
}
