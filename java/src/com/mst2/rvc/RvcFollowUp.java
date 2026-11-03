package com.mst2.rvc;

import de.adi961.miblogger.MIBLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.dsi.ifc.carparkingsystem.DSICarParkingSystem;

/** Safely changes the factory VPS post-reverse follow-up time for aftermarket cameras. */
public final class RvcFollowUp {
    private static final String ROOT = "/tsd/var/mst2-pq-phone-nav";
    private static final String CONFIG = ROOT + "/rvc-instant";
    private static final String BACKUP = ROOT + "/rvc-followup-stock";
    private static final String APPLIED = ROOT + "/rvc-instant-applied";
    private static final String RESTORED = ROOT + "/rvc-followup-restored";

    private RvcFollowUp() {}

    /** Register the parking DSI only when an enable or restore operation is pending. */
    public static boolean actionNeeded() {
        return enabled() || new File(BACKUP).isFile();
    }

    /** Called from the stock DSI notification path; valid=1 means the value is authoritative. */
    public static void update(DSICarParkingSystem service, int value, int valid) {
        if (service == null || valid != 1) return;
        try {
            if (enabled()) {
                delete(RESTORED);
                if (!new File(BACKUP).isFile()) writeInt(BACKUP, value);
                if (value != 0) {
                    service.setVPSFollowUpTime(0);
                    MIBLogger.getInstance().info("RVC instant close: requested VPS follow-up time 0");
                } else {
                    touch(APPLIED);
                    MIBLogger.getInstance().info("RVC instant close: VPS follow-up time verified at 0");
                }
                return;
            }

            int stock = readInt(BACKUP, -1);
            if (stock < 0) return;
            delete(APPLIED);
            if (value != stock) {
                service.setVPSFollowUpTime(stock);
                MIBLogger.getInstance().info("RVC instant close: requested stock VPS follow-up time " + stock);
            } else {
                touch(RESTORED);
                MIBLogger.getInstance().info("RVC instant close: stock VPS follow-up time restored");
            }
        } catch (Throwable t) {
            MIBLogger.getInstance().error("RVC follow-up update failed: " + t);
        }
    }

    private static boolean enabled() {
        return readInt(CONFIG, 0) == 1;
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

    private static void writeInt(String path, int value) throws Exception {
        File dir = new File(ROOT);
        dir.mkdirs();
        File tmp = new File(path + ".new");
        FileOutputStream out = new FileOutputStream(tmp);
        out.write(Integer.toString(value).getBytes("US-ASCII"));
        out.write(10);
        out.close();
        File dest = new File(path);
        if (dest.exists() && !dest.delete()) throw new Exception("cannot replace " + path);
        if (!tmp.renameTo(dest)) throw new Exception("cannot commit " + path);
    }

    private static void touch(String path) throws Exception {
        FileOutputStream out = new FileOutputStream(path);
        out.close();
    }

    private static void delete(String path) {
        try { new File(path).delete(); } catch (Throwable t) {}
    }
}
