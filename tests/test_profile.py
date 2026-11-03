import json
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]

class ProfileTest(unittest.TestCase):
    def test_exact_target(self):
        profile = json.loads((ROOT / "profiles/MST2_US_VW_PQ_P0478T.json").read_text())
        self.assertEqual(profile["release"], "MST2_US_VW_PQ_P0478T")
        self.assertEqual(profile["part_number"], "3G0919360JP")
        self.assertEqual(set(profile["variants"]), {"cpu", "cpuplus"})
        self.assertEqual(profile["iap2_driver"]["size"], 771102)

    def test_release_payload_has_no_cluster_video_components(self):
        names = {p.name for p in (ROOT / "toolbox/custom/mst2-pq-phone-nav").iterdir()}
        self.assertIn("libmcn_sal.so", names)
        self.assertIn("libmcn_ipod.so", names)
        self.assertFalse(any("dm" in n or "video" in n for n in names))

    def test_projection_scale_covers_both_phone_protocols(self):
        src = (ROOT / "java/src/com/mst2/phonescale/ProjectedScale.java").read_text()
        self.assertIn("org.dsi.ifc.carplay.ServiceConfiguration", src)
        self.assertIn("org.dsi.ifc.androidauto.ServiceConfiguration", src)
        self.assertIn("org.dsi.ifc.androidauto2.ServiceConfiguration", src)
        self.assertIn("scale == 100 || scale == 115", src)
        self.assertNotIn("displayResolutionX = scaled", src)
        self.assertNotIn("touchscreenResolutionX = scaled", src)

    def test_now_playing_sources_and_protocol_caches(self):
        reader = (ROOT / "java/src/de/aatokombi/PhoneMediaReader.java").read_text()
        state = (ROOT / "java/src/de/aatokombi/MediaState.java").read_text()
        self.assertIn('/dev/shmem/aa_media', reader)
        self.assertIn('/pps/services/multimedia/iap2/nowplaying', reader)
        for key in ("MediaItemTitle::", "MediaItemArtist::", "MediaItemAlbumTitle::"):
            self.assertIn(key, reader)
        self.assertIn("carplayTitle", state)
        self.assertIn("androidAutoTitle", state)

    def test_scale_menu_is_fail_safe(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        menu = (ROOT / "toolbox/custom/greenmenu/mst2-pq-phone-nav.esd").read_text()
        self.assertIn("echo 100 > $MPQ/scale", install)
        self.assertIn("mpq_scale_100.sh", menu)
        self.assertIn("mpq_scale_115.sh", menu)

    def test_iap2_mismatch_uses_runtime_guards(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        hook = (ROOT / "native/src/ipod/ipod.c").read_text()
        self.assertIn("WARNING: iAP2 driver checksum differs", install)
        self.assertNotIn("Aborted: wrong iAP2 driver", install)
        for guard in ("packet_addparam_blob", "ident_info_funcs", "iap2_ctrl_msg_table",
                      "Location slot mismatch", "receive entry mismatch"):
            self.assertIn(guard, hook)

if __name__ == "__main__":
    unittest.main()
