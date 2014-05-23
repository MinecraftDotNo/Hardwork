package no.minecraft.Minecraftno.handlers;

public enum SqlNullType {
    NULLINT(java.sql.Types.INTEGER),
    NULLSTRING(java.sql.Types.VARCHAR);

    private int sqlType;

    private SqlNullType(int type) {
        sqlType = type;
    }

    public int getSqlType() {
        return sqlType;
    }
}
