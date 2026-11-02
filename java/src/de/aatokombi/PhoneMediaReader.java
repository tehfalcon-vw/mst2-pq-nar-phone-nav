package de.aatokombi;

import de.vw.mib.asl.framework.internal.framework.ServiceManager;
import de.vw.mib.timer.Timer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

/** Polls the proven AA shmem and CarPlay iAP2 PPS now-playing sources on an HMI timer. */
public final class PhoneMediaReader implements Runnable {
    private static final String AA = "/dev/shmem/aa_media";
    private static final String CP = "/pps/services/multimedia/iap2/nowplaying";
    private static final long POLL_MS = 500L;
    private static final long STALE_MS = 120000L;
    private static volatile boolean started;
    private Timer timer;
    private long aaSeq = -1L, aaSession = -1L, aaChanged;
    private String cpLast = "";
    private long cpChanged;

    public static synchronized void ensureStarted() {
        if (started) return;
        started = true;
        try {
            PhoneMediaReader r = new PhoneMediaReader();
            r.timer = ServiceManager.timerManager.createTimer("PQ_PHONE_MEDIA", POLL_MS, true, r, Timer.TIMER_THREAD_INVOKER);
            r.timer.start();
        } catch (Throwable t) { started = false; }
    }

    public void run() {
        try { pollAndroidAuto(); } catch (Throwable t) {}
        try { pollCarPlay(); } catch (Throwable t) {}
    }

    private void pollAndroidAuto() {
        String line = line(AA);
        if (line == null) {
            if (aaChanged > 0 && System.currentTimeMillis() - aaChanged > STALE_MS) clearAA();
            return;
        }
        int sp = line.indexOf(' ');
        if (sp < 0) return;
        long seq;
        try { seq = Long.parseLong(line.substring(0, sp)); } catch (Throwable t) { return; }
        String[] f = tabs(line.substring(sp + 1));
        int connected = number(f, 7, 1);
        long session = longNumber(f, 8, -1L);
        if (session != aaSession) { aaSession = session; aaSeq = -1L; clearAA(); }
        if (connected == 0) { aaSeq = seq; clearAA(); return; }
        if (seq == aaSeq) return;
        aaSeq = seq; aaChanged = System.currentTimeMillis();
        MediaState.androidAutoTitle = field(f, 0);
        MediaState.androidAutoArtist = field(f, 1);
        MediaState.androidAutoAlbum = field(f, 2);
        MediaState.androidAutoPosSec = number(f, 3, 0);
        MediaState.androidAutoDurSec = number(f, 4, 0);
        poke();
    }

    private void pollCarPlay() {
        String content = file(CP);
        if (content == null) {
            if (cpChanged > 0 && System.currentTimeMillis() - cpChanged > STALE_MS) clearCP();
            return;
        }
        if (content.equals(cpLast)) return;
        cpLast = content; cpChanged = System.currentTimeMillis();
        String title = extract(content, "MediaItemTitle::");
        if (title.length() == 0) { clearCP(); return; }
        MediaState.carplayTitle = title;
        MediaState.carplayArtist = extract(content, "MediaItemArtist::");
        MediaState.carplayAlbum = extract(content, "MediaItemAlbumTitle::");
        poke();
    }

    private static void poke() {
        try { de.vw.mib.bap.mqbpq.audiosd.functions.CurrentStationInfo.poke(); } catch (Throwable t) {}
    }
    private static void clearAA() {
        if (MediaState.androidAutoTitle == null) return;
        MediaState.androidAutoTitle=null; MediaState.androidAutoArtist=null; MediaState.androidAutoAlbum=null;
        MediaState.androidAutoPosSec=0; MediaState.androidAutoDurSec=0; poke();
    }
    private static void clearCP() {
        if (MediaState.carplayTitle == null) return;
        MediaState.carplayTitle=null; MediaState.carplayArtist=null; MediaState.carplayAlbum=null; poke();
    }
    private static String line(String path) {
        BufferedReader r=null;
        try { r=new BufferedReader(new FileReader(path)); return r.readLine(); }
        catch(Throwable t){return null;} finally {if(r!=null)try{r.close();}catch(Throwable t){}}
    }
    private static String file(String path) {
        FileInputStream in=null;
        try { in=new FileInputStream(path); byte[] b=new byte[4096]; int n=in.read(b); return n>0?new String(b,0,n,"UTF-8"):""; }
        catch(Throwable t){return null;} finally {if(in!=null)try{in.close();}catch(Throwable t){}}
    }
    private static String extract(String content,String key) {
        int at=content.indexOf(key); if(at<0)return ""; at+=key.length(); int end=content.indexOf('\n',at);
        if(end<0)end=content.length(); return new String(content.substring(at,end).trim());
    }
    private static String[] tabs(String s) {
        int n=1; for(int i=0;i<s.length();i++)if(s.charAt(i)=='\t')n++;
        String[] out=new String[n]; int start=0,j=0;
        for(int i=0;i<s.length();i++)if(s.charAt(i)=='\t'){out[j++]=s.substring(start,i);start=i+1;}
        out[j]=s.substring(start); return out;
    }
    private static String field(String[] f,int i){return i<f.length?f[i]:"";}
    private static int number(String[] f,int i,int d){try{return Integer.parseInt(field(f,i).trim());}catch(Throwable t){return d;}}
    private static long longNumber(String[] f,int i,long d){try{return Long.parseLong(field(f,i).trim());}catch(Throwable t){return d;}}
}
