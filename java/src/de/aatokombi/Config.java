package de.aatokombi;

import de.adi961.miblogger.MIBLogger;

/** Compile-time settings for the P0478T PQ navigation-only overlay. */
public final class Config {
    public static final boolean SHOW_NAV = true;
    public static final boolean SHOW_MEDIA = false;
    public static final boolean SHOW_COVER_ART = false;
    public static final int MANEUVER_NEAR_METERS = 400;
    public static final int MANEUVER_NEAR_SECONDS = 30;
    public static final long AA_STALE_BACKSTOP_MS = 300000L;
    public static final int LOG_LEVEL = MIBLogger.INFO;
    private Config() {}
}
