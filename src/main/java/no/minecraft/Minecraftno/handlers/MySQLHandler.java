package no.minecraft.Minecraftno.handlers;

import no.minecraft.Minecraftno.Minecraftno;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class MySQLHandler {

    private final Minecraftno plugin;

    public MySQLHandler(Minecraftno instance) {
        this.plugin = instance;
    }

    public void checkWarnings() {
        try {
            SQLWarning warning = this.plugin.getConnection().getWarnings();
            while (warning != null) {
                Minecraftno.log.log(Level.WARNING, "[Minecraftno] SQL-Advarsel: ", warning);
                warning = warning.getNextWarning();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Fikk ikke hentet warnings fra databasetilkobling! ", e);
        }
    }

    /**
     * Oppdaterer tabellen med gitte spørring og verdiene i arrayet. Verdiene må
     * castes om til object og puttes i arrayet for at ting skal fungere
     * korrekt.
     *
     * @param query (String) Spørringen som skal utføres. Det er viktig at det er
     *              ? hvor verdiene skal være
     * @param array (Object[]) Her lagres verdiene som skal inn i spørringa. Det
     *              kjøres sjekk på hvilken type verdi det er, som igjen kaster de
     *              om til korrekt object.
     *
     * @return (boolean) True/False alt ettersom om spørringen lykkes
     */
    public boolean update(String query, Object[] array) {
        Connection conn = null;
        PreparedStatement ps = null;
        int counter = 1; // Definerer indeks som blir brukt i spørringen. Skal
        // starte på 1.
        if (array != null) {
            try {
                // conn = sqlConnector.getConnection();
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement(query);

                // Her kjører man inn variablene
                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o); // indeks og object som
                        // blir castet
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else if (o instanceof SqlNullType) { // Dette vil neppe
                        // bli brukt...
                        // fjernes
                        // etterhvert!
                        ps.setNull(counter, ((SqlNullType) o).getSqlType());
                    } else {
                        Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Ukjent objekt i mysql-handler.(" + o.getClass().toString() + ")");
                        Minecraftno.log.log(Level.SEVERE, Arrays.toString(array));
                    }
                    counter++; // øker indeks
                }
                ps.setEscapeProcessing(true);
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
                return false;
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        //conn.close();
                    }
                } catch (SQLException e) {
                    Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
                }
            }
        } else { // Arrayet er tomt, ergo har man brukt funksjonen feil
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Empty array in mysqlhandler.update");
            return false;
        }
    }

    /**
     * Metode som kjører en update uten preparedStatement.
     *
     * @param query (String) Spørringen
     *
     * @return True/False alt ettersom om spørringen lyktes
     */

    public boolean update(String query) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.plugin.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return false;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Oppdaterer databasen med din spørring samtidig som den returnerer den
     * genererte iden
     *
     * @param query Selve spørringen (String)
     *
     * @return int. Returnerer 0 hvis det ikke ble generert id.
     */
    public int insert(String query) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setEscapeProcessing(true);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return 0;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Henter ut en gitt kolonne fra tabellen. Funker kun så lenge spørringen
     * ber om kun en kolonne.
     *
     * @param query Selve spørringen (String)
     *
     * @return Kolonnen (String) eller null
     */
    public String getColumn(String query) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String column = null;
        try {
            // conn = sqlConnector.getConnection();
            conn = this.plugin.getConnection();
            ps = conn.createStatement();
            rs = ps.executeQuery(query);

            if (rs.next()) {
                column = rs.getString(1);
            }
            return column;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return null;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Henter ut en gitt kolonne fra tabellen. Funker kun så lenge spørringen
     * ber om kun en kolonne.
     *
     * @param query Selve spørringen (String)
     * @param c     Kolonnen som skal returneres (String)
     *
     * @return Kolonnen (String) eller null
     */
    public String getColumn(String query, String c, Object[] array) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String column = "";
        int counter = 1; // Definerer indeks som blir brukt i spørringen. Skal
        // starte på 1.
        if (array != null) {
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement(query);

                // Her kjører man inn variablene
                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o); // indeks og object som
                        // blir castet
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else {
                        Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Nullobjekt i mysql-handler. (getColumn)");
                    }
                    counter++; // øker indeks
                }
                rs = ps.executeQuery();

                if (rs.next()) {
                    column = rs.getString(c);
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
                return null;
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        //conn.close();
                    }
                } catch (SQLException e) {
                    Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
                }
            }
        }
        Minecraftno.debugLog.exiting(MySQLHandler.class.getName(), Thread.currentThread().getStackTrace()[0].getMethodName(), query);
        return column;
    }

    /**
     * Henter ut en gitt kolonne fra tabellen. Funker kun så lenge spørringen
     * ber om kun en kolonne.
     *
     * @param query Selve spørringen (String)
     * @param c     Kolonnen som skal returneres (int)
     *
     * @return Kolonnen (int) eller 0
     */
    public int getColumnInt(String query, String c) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        int column = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.createStatement();
            rs = ps.executeQuery(query);

            if (rs.next()) {
                column = rs.getInt(c);
            }

            return column;
        } catch (SQLException ex) {
            Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
            return 0;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    //conn.close();
                }
            } catch (SQLException e) {
                Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    /**
     * Utestet! Metode for å hente ut flere rader og kolonner fra en database
     *
     * @param query (String) Spørringen
     * @param array (Object[]) Et array som inneholder alle variabler som skal
     *              settes i spørringen
     *
     * @return ArrayList<ArrayList<String>> som inneholder all data
     */
    public ArrayList<ArrayList<String>> getRows(String query, Object[] array) {
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int counter = 1; // Definerer indeks som blir brukt i spørringen. Skal
        // starte på 1.
        int rowCounter = 0;

        if (array != null) {
            try {
                // conn = sqlConnector.getConnection();
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement(query);

                // Her kjører man inn variablene
                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o); // indeks og object som
                        // blir castet
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else {
                        Minecraftno.log.log(Level.SEVERE, "[Minecraftno] Nullobjekt i mysql-handler. (getRows)");
                    }
                    counter++; // øker indeks
                }

                rs = ps.executeQuery();

                if (rs.next()) {
                    rows.add(new ArrayList<String>());
                    for (int i = 1; i <= array.length; i++) {
                        rows.get(rowCounter).add(rs.getString(i));
                        rowCounter++;
                    }
                }
            } catch (SQLException ex) {
                Minecraftno.log.log(Level.SEVERE, "[Minecraftno] SQL Exception", ex);
                return null;
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        //conn.close();
                    }
                } catch (SQLException e) {
                    Minecraftno.log.log(Level.SEVERE, "SQL-error:", e);
                }
            }
        } else {
            Minecraftno.log.log(Level.SEVERE, Thread.currentThread().getStackTrace()[0].getMethodName() + " - array is null");
            return null;
        }
        return rows;
    }

    /**
     * Checks if the string contains any non-alphabet characters or _
     *
     * @param string The string to check.
     *
     * @return true if it doesn't any non-alphabet characters or _, false if it does.
     */
    public static boolean checkString(String string) {

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isLetterOrDigit(string.codePointAt(i))) {
                if (string.charAt(i) != '_') {
                    return false;
                }
            }
        }

        return true;
    }
}