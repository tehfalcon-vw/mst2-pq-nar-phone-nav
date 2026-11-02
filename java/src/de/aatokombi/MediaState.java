package de.aatokombi;

/** Protocol-separated now-playing caches consumed by the PQ audio BAP shadow. */
public final class MediaState {
    public static volatile String carplayTitle, carplayArtist, carplayAlbum;
    public static volatile String androidAutoTitle, androidAutoArtist, androidAutoAlbum;
    public static volatile int androidAutoPosSec, androidAutoDurSec;
    private MediaState() {}
}
