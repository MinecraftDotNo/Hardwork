package no.minecraft.Minecraftno.sql;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLConnectionPool implements Closeable {
    //private final static int poolSize = 10;
    private final static long alive = 30000;
    private final List<JDBCConnectionPool> connections;
    private final Lock lock = new ReentrantLock();
    private final static String jdbc = "jdbc:mysql://";
    private final String database, dBuser, dBpass;

    public MySQLConnectionPool(String dbhost, int dbport, String dbname, String dBuser, String dBpass) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.database = jdbc + dbhost + ":" + dbport + "/" + dbname;
        this.dBuser = dBuser;
        this.dBpass = dBpass;
        connections = new ArrayList<JDBCConnectionPool>();
        ConnectionReaper reaper = new ConnectionReaper();
        new Thread(reaper).start();
    }

    @Override
    public void close() {
        lock.lock();
        final Iterator<JDBCConnectionPool> itr = connections.iterator();
        while (itr.hasNext()) {
            final JDBCConnectionPool conn = itr.next();
            itr.remove();
            conn.terminate();
        }
        lock.unlock();
    }

    public Connection getConnection() throws SQLException {
        this.lock.lock();
        try {
            final Iterator<JDBCConnectionPool> itr = connections.iterator();
            while (itr.hasNext()) {
                final JDBCConnectionPool conn = itr.next();
                if (conn.lease()) {
                    if (conn.isValid()) {
                        return conn;
                    }
                }
            }
            final JDBCConnectionPool conn = new JDBCConnectionPool(DriverManager.getConnection(database, dBuser, dBpass), this);
            conn.lease();
            if (!conn.isValid()) {
                conn.terminate();
                throw new SQLException("Failed to confirm connection");
            }
            this.connections.add(conn);
            return conn;
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized void removeConnection(JDBCConnectionPool JDBCCconn) {
        connections.remove(JDBCCconn);
    }

    public synchronized void checkTrueFalse(JDBCConnectionPool JDBCCconn) {
        System.out.println(JDBCCconn.inUse());
    }

    private void reapConnections() {
        this.lock.lock();
        final long stale = System.currentTimeMillis() - alive;
        final Iterator<JDBCConnectionPool> itr = connections.iterator();
        while (itr.hasNext()) {
            final JDBCConnectionPool conn = itr.next();
            if (conn.inUse() && stale > conn.getLastUse()) {
                conn.terminate();
                itr.remove();
            }
        }
        this.lock.unlock();
    }

    private class ConnectionReaper implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (final InterruptedException e) {
                }
                reapConnections();
            }
        }
    }
}