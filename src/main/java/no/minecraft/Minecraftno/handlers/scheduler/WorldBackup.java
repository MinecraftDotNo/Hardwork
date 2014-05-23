package no.minecraft.Minecraftno.handlers.scheduler;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldBackup {

    private final Minecraftno plugin;
    private final static String backupPath = "/mnt/snurredisk/backup/Hardwork/world/";
    private final static long backupDiskspaceWarn = 1048576 * 5;

    public WorldBackup(Minecraftno instance) {
        this.plugin = instance;
    }

    public class Run implements Runnable {
        public void run() {
            if (checkFreeDiskSpace(backupPath)) {
                Minecraftno.log.log(Level.SEVERE, ChatColor.RED + "[Minecraftno] ADVARSEL: Lite diskplass p� hardisk. Backup rutiner avslått!:");
            }
            deleteFilesOlderThanNdays(2, backupPath);
            runBackup(backupPath);
        }
    }

    protected static void deleteFilesOlderThanNdays(int daysBack, String dirWay) {
        File directory = new File(dirWay);
        if (directory.exists()) {
            File[] listFiles = directory.listFiles();
            long purgeTime = System.currentTimeMillis() - ((long) (daysBack * 24 * 60 * 60 * 1000));
            for (File listFile : listFiles) {
                if (listFile.lastModified() < purgeTime) {
                    if (!listFile.delete()) {
                        Minecraftno.log.log(Level.SEVERE, ChatColor.RED + "[Minecraftno] Unable to delete file: " + listFile);
                    }
                }
            }
        } else {
            Minecraftno.log.log(Level.SEVERE, ChatColor.RED + "[Minecraftno] Files were not deleted, directory " + dirWay + " does'nt exist!");
        }
    }

    protected boolean checkFreeDiskSpace(String dirWay) {
        long freeSpace = new File(dirWay).getFreeSpace();
        return (freeSpace < WorldBackup.backupDiskspaceWarn);
    }

    public void runBackup(String dirWay) {
        if (Bukkit.getServer().getOnlinePlayers().length > 0) {
            final String backup = dirWay + File.separator + Util.getDateTime();
            final ArrayList<File> inputFiles = new ArrayList<File>();
            final ArrayList<File> outputFiles = new ArrayList<File>();

            for (World world : Bukkit.getServer().getWorlds()) {
                world.setAutoSave(false);
                File folder = new File(world.getName()).getAbsoluteFile();
                String repl = folder.getParentFile().getAbsolutePath();

                for (File file : getFiles(folder, true)) {
                    inputFiles.add(file);
                    String tmp;
                    if (backup.contains(":")) {
                        tmp = backup;
                    } else {
                        tmp = folder.getParentFile().getAbsolutePath() + File.separator + backup;
                    }
                    outputFiles.add(new File(tmp + file.getAbsolutePath().replace(repl, "")));
                }
            }
            new Thread() {
                public void run() {
                    try {
                        File output = new File(backup + ".zip");
                        if (!output.getParentFile().exists()) {
                            output.getParentFile().mkdirs();
                        }
                        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
                        String repl = new File("").getAbsolutePath();
                        for (int i = 0; i < inputFiles.size(); i++) {
                            FileInputStream fis = new FileInputStream(inputFiles.get(i));
                            ZipEntry entry = new ZipEntry(inputFiles.get(i).getAbsolutePath().replace(repl + File.separator, ""));
                            try {
                                zos.putNextEntry(entry);
                                byte[] buffer = new byte[4096];
                                int len = 0;
                                while ((len = fis.read(buffer)) > 0)
                                    zos.write(buffer, 0, len);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fis.close();
                                } catch (IOException e) {

                                }
                            }
                        }
                        zos.flush();
                        zos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        for (World world : Bukkit.getServer().getWorlds()) {
            world.setAutoSave(true);
        }
    }

    private static ArrayList<File> getFiles(File folder, boolean readSubFolders) {
        ArrayList<File> files = new ArrayList<File>();
        if (folder.isFile()) {
            return files;
        }
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                files.add(file);
            } else if (readSubFolders) {
                files.addAll(getFiles(file, true));
            }
        }
        return files;
    }

    public void scheduleWorldBackup() {
        if (this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Run(), 36000L, 432000) > 0) {
            Minecraftno.log.log(Level.INFO, "WorldBackup startet.");
        }
    }
}
