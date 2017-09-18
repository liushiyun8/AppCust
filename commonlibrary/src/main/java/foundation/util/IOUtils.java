package foundation.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by wujian on 2016/4/6.
 */
public class IOUtils {
    public IOUtils() {
    }

    public static void silentlyClose(Closeable... closeables) {
        Closeable[] var4 = closeables;
        int var3 = closeables.length;

        for (int var2 = 0; var2 < var3; ++var2) {
            Closeable cl = var4[var2];

            try {
                if (cl != null) {
                    cl.close();
                }
            } catch (Exception var6) {
            }
        }

    }

    public static String readToString(InputStream is) throws IOException {
        byte[] data = readToByteArray(is);
        return new String(data, "utf-8");
    }

    public static byte[] readToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        readToStream(is, baos);
        return baos.toByteArray();
    }

    public static void readToStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[8192];

        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

    }

    public static ArrayList<File> getFileList(File dir, String... fileExtensions) {
        ArrayList files = new ArrayList();
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            File[] var7 = fileList;
            int var6 = fileList.length;

            for (int var5 = 0; var5 < var6; ++var5) {
                File file = var7[var5];
                if (file.isFile()) {
                    if (fileExtensions.length == 0) {
                        files.add(file);
                    } else {
                        String fileName = file.getName().toLowerCase();
                        String[] var12 = fileExtensions;
                        int var11 = fileExtensions.length;

                        for (int var10 = 0; var10 < var11; ++var10) {
                            String ext = var12[var10];
                            if (fileName.endsWith(ext)) {
                                files.add(file);
                                break;
                            }
                        }
                    }
                } else {
                    files.addAll(getFileList(file, fileExtensions));
                }
            }
        }

        return files;
    }

    public static void copy(File fileFrom, File fileTo) throws IOException {
        if (fileTo.exists()) {
            fileTo.delete();
        }

        FileChannel src = null;
        FileChannel dst = null;

        try {
            src = (new FileInputStream(fileFrom)).getChannel();
            dst = (new FileOutputStream(fileTo)).getChannel();
            dst.transferFrom(src, 0L, src.size());
        } finally {
            silentlyClose(new Closeable[]{src, dst});
        }

    }

    public static void dumpDBToCacheDir(Context ctx, SQLiteDatabase db) {
        String dbFilePath = db.getPath();
        String dbFileName = dbFilePath.substring(dbFilePath.lastIndexOf(47, dbFilePath.length()));
        File fileTo = new File(ctx.getExternalCacheDir(), dbFileName);

        try {
            copy(new File(dbFilePath), fileTo);
        } catch (IOException var6) {

        }

    }
}
