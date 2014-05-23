package no.minecraft.Minecraftno.handlers.scheduler;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.Util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ServerLogBackup implements Runnable {

    private final Minecraftno plugin;

    public ServerLogBackup(Minecraftno instance) {
        this.plugin = instance;
    }

    public void scheduleServerLogBackup() {
        if (this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 15000L, 15000L) > 0) {
            this.plugin.getLogger().info("Server log backup task started.");
        }
    }

    public void run() {
        deleteFilesOlderThanNdays(this.plugin.getConfig().getInt("backup.log.max_age"), this.plugin.getConfig().getString("backup.log.directory"));

        zipLog();
    }

    private void deleteFilesOlderThanNdays(int daysBack, String dirWay) {
        File directory = new File(dirWay);

        if (directory.exists()) {
            File[] listFiles = directory.listFiles();

            long purgeTime = System.currentTimeMillis() - ((long) (daysBack * 24 * 60 * 60 * 1000));

            for (File listFile : listFiles) {
                if (listFile.lastModified() < purgeTime) {
                    if (!listFile.delete()) {
                        this.plugin.getLogger().severe("Could not delete old log backup! " + listFile);
                    }
                }
            }
        }
    }

    private void zipLog() {
        File logFile = new File("server.log");

        // This shouldn't really be possible, but the check was around
        // when I got here. Better leave it in place.
        if (!logFile.exists()) {
            return;
        }

        long fileSize = logFile.length() / 1024L;

        // Has the server log reached the maximum size?
        if (fileSize < this.plugin.getConfig().getInt("backup.log.max_size")) {
            return;
        }

        File dir = new File(this.plugin.getConfig().getString("backup.log.directory"));

        // Make sure it exists.
        if (!dir.mkdirs()) {
            this.plugin.getLogger().severe("Could not create server log backup directory! " + dir);
            return;
        }

        // Create our variables in this scope so we can deal with them outside
        // the try/catch should it fail.
        OutputStream out = null;
        BufferedOutputStream bufOut = null;
        ZipOutputStream zipOut = null;
        ZipEntry zipEntry = null;
        FileInputStream in = null;

        try {
            // Create a stream to write to our backup zip.
            out = new FileOutputStream(new File(dir, Util.getDateTime() + ".zip"));

            // Wrap our stream with a buffer.
            bufOut = new BufferedOutputStream(out);

            // And finally create a zip stream.
            zipOut = new ZipOutputStream(bufOut);

            // Create an entry in the zip file.
            zipEntry = new ZipEntry("server.log");
            zipOut.putNextEntry(zipEntry);

            // Create a stream to read from the server log.
            in = new FileInputStream(logFile);

            // Write the log to the backup.
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                zipOut.write(buffer, 0, len);
            }
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to backup server log!");
            e.printStackTrace();
        }

        // Ignore any and all exceptions thrown below.
        // They will be NPEs or IO exceptions.

        // Close the log file.
        try {
            in.close();
        } catch (Exception e) {
        }

        // Close the zip entry.
        try {
            zipOut.closeEntry();
        } catch (Exception e) {
        }

        // Close the zip file.
        try {
            zipOut.close();
        } catch (Exception e) {
        }

        // Close the buffered stream.
        try {
            bufOut.close();
        } catch (Exception e) {
        }

        // And finally close the backup file stream.
        try {
            out.close();
        } catch (Exception e) {
        }
    }
}
