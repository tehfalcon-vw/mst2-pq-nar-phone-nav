package com.mst2.phonescale;

import java.io.BufferedReader;
import java.io.FileReader;

/** Applies one fail-safe physical-size scale to CarPlay and both AA DSI generations. */
public final class ProjectedScale {
    private static final String CONFIG = "/tsd/var/mst2-pq-phone-nav/scale";
    private ProjectedScale() {}

    private static int readScale() {
        BufferedReader reader = null;
        try {
            String override = System.getProperty("mst2pq.scale.file");
            reader = new BufferedReader(new FileReader(override == null ? CONFIG : override));
            String value = reader.readLine();
            if (value == null || reader.readLine() != null) return 100;
            int scale = Integer.parseInt(value.trim());
            return scale == 100 || scale == 125 ? scale : 100;
        } catch (Throwable t) {
            return 100;
        } finally {
            if (reader != null) try { reader.close(); } catch (Throwable t) {}
        }
    }

    private static int scaled(int value, int scale) {
        if (value <= 0 || scale == 100) return value;
        long result = ((long)value * scale + 50L) / 100L;
        return result > 32767L ? value : (int)result;
    }

    public static void apply(org.dsi.ifc.carplay.ServiceConfiguration c) {
        try { int s = readScale(); c.physicalDisplayHeight = scaled(c.physicalDisplayHeight, s); c.physicalDisplayWidth = scaled(c.physicalDisplayWidth, s); }
        catch (Throwable t) {}
    }

    public static void apply(org.dsi.ifc.androidauto.ServiceConfiguration c) {
        try { int s = readScale(); c.physicalDisplayHeight = scaled(c.physicalDisplayHeight, s); c.physicalDisplayWidth = scaled(c.physicalDisplayWidth, s); }
        catch (Throwable t) {}
    }

    public static void apply(org.dsi.ifc.androidauto2.ServiceConfiguration c) {
        try { int s = readScale(); c.physicalDisplayHeight = scaled(c.physicalDisplayHeight, s); c.physicalDisplayWidth = scaled(c.physicalDisplayWidth, s); }
        catch (Throwable t) {}
    }
}
