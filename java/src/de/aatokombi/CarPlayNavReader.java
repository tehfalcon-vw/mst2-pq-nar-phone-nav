package de.aatokombi;

import de.adi961.miblogger.MIBLogger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** Receives decoded iAP2 Route Guidance snapshots from the native CarPlay hook. */
public final class CarPlayNavReader implements Runnable {
    private static final int PORT = 39750;
    private static final long TIMEOUT_MS = 5000L;
    private static volatile boolean started;
    private DatagramSocket socket;
    private long lastUpdate;
    private boolean active;

    public static synchronized void ensureStarted() {
        if (started) return;
        started = true;
        Thread t = new Thread(new CarPlayNavReader(), "MST2-PQ-CarPlay-Nav");
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        try {
            socket = new DatagramSocket(PORT, InetAddress.getByName("127.0.0.1"));
            socket.setSoTimeout(1000);
            byte[] data = new byte[4096];
            MIBLogger.getInstance().info("CarPlay route reader listening on 127.0.0.1:" + PORT);
            for (;;) {
                try {
                    DatagramPacket p = new DatagramPacket(data, data.length);
                    socket.receive(p);
                    receive(new String(p.getData(), 0, p.getLength(), "UTF-8"));
                } catch (java.io.InterruptedIOException timeout) {
                    if (active && System.currentTimeMillis() - lastUpdate > TIMEOUT_MS) clear();
                }
            }
        } catch (Throwable t) {
            MIBLogger.getInstance().error("CarPlay route reader stopped: " + t);
            clear();
        }
    }

    private void receive(String text) {
        if (text.startsWith("video=")) return;
        int type = number(text, "type", -1);
        int angle = number(text, "angle", CarPlayManeuvers.NO_ANGLE);
        boolean lht = number(text, "lht", 0) == 1;
        boolean nowActive = number(text, "active", 0) == 1;
        if (!nowActive) { clear(); return; }
        int[] mapped = CarPlayManeuvers.map(type, angle, lht);
        NavState.mainElement = mapped[0];
        NavState.direction = mapped[1];
        NavState.street = value(text, "street");
        NavState.distanceMeters = number(text, "dist", 0);
        NavState.destinationSeconds = number(text, "rtt", 0);
        NavState.destinationMeters = number(text, "dest", 0);
        NavState.sideStreets = CarPlayManeuvers.sideStreets(numbers(value(text, "roads")), angle);
        NavState.approaching = true;
        NavState.zLevelGuidance = 0;
        NavState.source = 2;
        NavState.ACTIVE = true;
        active = true;
        lastUpdate = System.currentTimeMillis();
        poke();
    }

    private void clear() {
        active = false;
        if (NavState.source != 2) return;
        NavState.ACTIVE = false;
        NavState.source = 0;
        NavState.destinationMeters = 0;
        NavState.destinationSeconds = 0;
        poke();
    }

    private static void poke() {
        try {
            de.vw.mib.bap.mqbpq.navsd.functions.RouteGuidanceStatus.poke();
            de.vw.mib.bap.mqbpq.navsd.functions.ManeuverDescriptor.poke();
            de.vw.mib.bap.mqbpq.navsd.functions.DistanceToNextManeuver.poke();
            de.vw.mib.bap.mqbpq.navsd.functions.TurnToInfo.poke();
            de.vw.mib.bap.mqbpq.navsd.functions.DistanceTimeToDestination.poke();
        } catch (Throwable ignored) {}
    }

    private static String value(String text, String key) {
        String prefix = key + "=";
        int at = text.indexOf(prefix);
        if (at < 0 || (at > 0 && text.charAt(at - 1) != '\n')) return "";
        at += prefix.length();
        int end = text.indexOf('\n', at);
        return text.substring(at, end < 0 ? text.length() : end).trim();
    }

    private static int number(String text, String key, int fallback) {
        try { return Integer.parseInt(value(text, key)); }
        catch (Throwable t) { return fallback; }
    }

    private static int[] numbers(String csv) {
        if (csv == null || csv.length() == 0) return null;
        int count = 1;
        for (int i = 0; i < csv.length(); i++) if (csv.charAt(i) == ',') count++;
        int[] out = new int[count];
        int start = 0;
        for (int i = 0; i < count; i++) {
            int end = csv.indexOf(',', start);
            if (end < 0) end = csv.length();
            try { out[i] = Integer.parseInt(csv.substring(start, end).trim()); }
            catch (Throwable t) { out[i] = 1000; }
            start = end + 1;
        }
        return out;
    }
}
