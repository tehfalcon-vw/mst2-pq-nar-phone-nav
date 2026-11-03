package com.mst2.rvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/** Bridges the always-running stock car-service gear callback to parking close. */
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

    /** Called by HsmTargetCarService's existing attribute-15 listener. R is mode 2. */
    public static synchronized void updateGear(int mode) {
        try { writeText(INIT, "car-service-listener-active"); } catch (Throwable t) {}
        try { writeText(CALLBACK, "mode=" + mode); } catch (Throwable t) {}
        int previous = lastMode;
        lastMode = mode;
        if (enabled() && previous == 2 && mode != 2) {
            try {
                boolean delivered = de.vw.mib.asl.framework.internal.framework.ServiceManager.eventMain
                        .getServiceRegister().triggerObserver(1073745153);
                writeText(CLOSE, delivered ? "reverse-exit mode=" + mode + " delivered=1" : "reverse-exit mode=" + mode + " delivered=0");
            } catch (Throwable t) {
                try { writeText(CLOSE, "failed"); } catch (Throwable ignored) {}
            }
        }
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
