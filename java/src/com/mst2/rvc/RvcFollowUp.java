package com.mst2.rvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/** Optional aftermarket-camera failure interceptor for active parking popups. */
public final class RvcFollowUp {
    private static final String ROOT = "/tsd/var/mst2-pq-phone-nav";
    private static final String CONFIG = ROOT + "/rvc-instant";
    private static final String INIT = ROOT + "/rvc-parking-hook";
    private static final String CALLBACK = ROOT + "/rvc-pdc-callback";
    private static final String VPS_CALLBACK = ROOT + "/rvc-vps-callback";
    private static final String CLOSE = ROOT + "/rvc-close-request";
    private static final String SIGNALS = ROOT + "/rvc-signal-log";
    private static int signalSequence;

    private RvcFollowUp() {}

    public static boolean enabled() {
        return readInt(CONFIG, 0) == 1;
    }

    public static synchronized void markParkingHook(String state) {
        try { writeText(INIT, "parking-state-hooks-loaded last=" + state); } catch (Throwable t) {}
    }

    public static synchronized void recordSignal(String state, String signal) {
        FileOutputStream out = null;
        try {
            File dir = new File(ROOT);
            dir.mkdirs();
            File file = new File(SIGNALS);
            boolean append = file.exists() && file.length() < 1800;
            out = new FileOutputStream(file, append);
            String line = String.valueOf(++signalSequence) + " " + state + " " + signal + "\n";
            out.write(line.getBytes("US-ASCII"));
        } catch (Throwable t) {
        } finally {
            if (out != null) try { out.close(); } catch (Throwable t) {}
        }
    }

    /** PQ P0478T exposes reverse exit only as departure from the active camera state. */
    public static synchronized void closeOnStateExit(String state) {
        if (!enabled()) return;
        try {
            boolean delivered = de.vw.mib.asl.framework.internal.framework.ServiceManager.eventMain
                    .getServiceRegister().triggerObserver(1073745153);
            writeText(CLOSE, "state-exit state=" + state + (delivered ? " delivered=1" : " delivered=0"));
        } catch (Throwable t) {
            try { writeText(CLOSE, "state-exit state=" + state + " failed"); } catch (Throwable ignored) {}
        }
    }

    /** Returns true only when stock CLOSE_PARKING was delivered for this failure. */
    public static synchronized boolean interceptPdcFailure(boolean failed, int valid, String state) {
        try { writeText(CALLBACK, "state=" + state + " failed=" + (failed ? 1 : 0) + " valid=" + valid); } catch (Throwable t) {}
        return interceptFailure(failed, state, "pdc");
    }

    /** Active camera states normally inherit this callback from StatePopupOff. */
    public static synchronized boolean interceptVpsFailure(boolean failed, int valid, String state) {
        try { writeText(VPS_CALLBACK, "state=" + state + " failed=" + (failed ? 1 : 0) + " valid=" + valid); } catch (Throwable t) {}
        return interceptFailure(failed, state, "vps");
    }

    private static boolean interceptFailure(boolean failed, String state, String source) {
        if (!failed || !enabled()) return false;
        try {
            boolean delivered = de.vw.mib.asl.framework.internal.framework.ServiceManager.eventMain
                    .getServiceRegister().triggerObserver(1073745153);
            writeText(CLOSE, source + "-failure state=" + state + (delivered ? " delivered=1" : " delivered=0"));
            return delivered;
        } catch (Throwable t) {
            try { writeText(CLOSE, source + "-failure state=" + state + " failed"); } catch (Throwable ignored) {}
            return false;
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
