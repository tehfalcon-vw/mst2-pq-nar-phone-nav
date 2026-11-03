package com.mst2.rvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/** Detects the supported P0478T reverse-to-forward/neutral/park transition. */
public final class RvcFollowUp {
    private static final String ROOT = "/tsd/var/mst2-pq-phone-nav";
    private static final String CONFIG = ROOT + "/rvc-instant";
    private static final String INIT = ROOT + "/rvc-gear-init";
    private static final String CALLBACK = ROOT + "/rvc-gear-callback";
    private static final String CLOSE = ROOT + "/rvc-close-request";
    private static int lastMode = -1;

    private RvcFollowUp() {}

    public static boolean enabled() {
        return readInt(CONFIG, 0) == 1;
    }

    public static void markInit(String stage) {
        try { writeText(INIT, stage); } catch (Throwable t) {}
    }

    /** Returns true exactly once on each valid automatic-transmission R -> non-R edge. */
    public static synchronized boolean updateGear(int mode, int valid) {
        try { writeText(CALLBACK, "mode=" + mode + " valid=" + valid); } catch (Throwable t) {}
        if (valid != 1) return false;
        int previous = lastMode;
        lastMode = mode;
        return enabled() && previous == 2 && mode != 2;
    }

    public static void markClose(int mode) {
        try { writeText(CLOSE, "reverse-exit mode=" + mode); } catch (Throwable t) {}
    }

    private static int readInt(String path, int fallback) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            if (line == null || reader.readLine() != null) return fallback;
            return Integer.parseInt(line.trim());
        } catch (Throwable t) {
            return fallback;
        } finally {
            if (reader != null) try { reader.close(); } catch (Throwable t) {}
        }
    }

    private static void writeText(String path, String value) throws Exception {
        File dir = new File(ROOT);
        dir.mkdirs();
        File tmp = new File(path + ".new");
        FileOutputStream out = new FileOutputStream(tmp);
        out.write(value.getBytes("US-ASCII"));
        out.write(10);
        out.close();
        File dest = new File(path);
        if (dest.exists() && !dest.delete()) throw new Exception("cannot replace " + path);
        if (!tmp.renameTo(dest)) throw new Exception("cannot commit " + path);
    }

}
