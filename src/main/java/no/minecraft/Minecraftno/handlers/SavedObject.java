package no.minecraft.Minecraftno.handlers;

import java.io.*;

public class SavedObject {
    public static boolean save(Object obj, File binFile) throws IOException {
        // Make sure the directory exists before we start creating files in there.
        String path = binFile.getAbsolutePath();
        File dir = new File(path.substring(0, path.lastIndexOf(File.separator)));
        dir.mkdirs();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binFile));

        oos.writeObject(obj);
        oos.flush();

        oos.close();

        return true;
    }

    public static Object load(File binFile) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binFile));

        Object result = ois.readObject();

        ois.close();

        return result;
    }
}
