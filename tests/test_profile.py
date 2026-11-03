import json
import hashlib
import re
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
        self.assertIn("scale == 100 || scale == 125", src)
        self.assertNotIn("scale == 115", src)
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
        self.assertIn("mpq_scale_125.sh", menu)
        self.assertNotIn("mpq_scale_115.sh", menu)
        boot = (ROOT / "toolbox/custom/mst2-pq-phone-nav/runHMI.block").read_text()
        self.assertIn("-Xbootclasspath/p:$MPQ/mst2-pq-nar-phone-nav.jar", boot)
        self.assertNotIn('BOOTCLASSPATH="$BOOTCLASSPATH:$MPQ/', boot)

    def test_every_update_resets_scale_for_trial(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        self.assertIn("echo 100 > $MPQ/scale", install)
        self.assertNotIn("[ -s $MPQ/scale ]", install)

    def test_successful_install_and_enable_auto_reboot(self):
        common = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_common.sh").read_text()
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        enable = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_enable.sh").read_text()
        self.assertIn("util_reboot.sh", common)
        self.assertIn("sleep 2; ksh $reboot_script", common)
        self.assertGreater(install.rfind("mpq_reboot"), install.rfind("touch $MPQ/trial"))
        self.assertIn("Unit was not rebooted", install)
        self.assertGreater(enable.rfind("mpq_reboot"), enable.rfind("touch $MPQ/enabled"))
        self.assertIn("Enable failed. Unit was not rebooted", enable)

    def test_readme_has_toolbox_greenmenu_bootstrap(self):
        readme = (ROOT / "README.md").read_text()
        self.assertIn("Customization", readme)
        self.assertIn("Greenmenu", readme)
        self.assertIn("Copy custom GEM scripts to unit", readme)
        self.assertIn("MST2_PQ_Phone_Nav", readme)

    def test_rvc_fix_uses_reverse_edge_and_stock_close_event(self):
        target = (ROOT / "java/src/de/vw/mib/asl/internal/androidauto/target/AndroidAutoTarget.java").read_text()
        helper = (ROOT / "java/src/com/mst2/rvc/RvcFollowUp.java").read_text()
        stock_hook = (ROOT / "java/src/de/vw/mib/asl/internal/car/thinkblue/calc/table/ConditionsContainer.java").read_text()
        menu = (ROOT / "toolbox/custom/greenmenu/mst2-pq-phone-nav.esd").read_text()
        uninstall = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_uninstall.sh").read_text()
        self.assertNotIn("DSICarVehicleStates", target)
        self.assertNotIn("setNotification(15", target)
        self.assertNotIn("RvcFollowUp", target)
        self.assertIn("RvcFollowUp.updateReverse(value)", stock_hook)
        self.assertIn("refreshConditionsRGear(boolean value)", stock_hook)
        self.assertIn("getServiceRegister().triggerObserver(1073745153)",
                      "".join(helper.split()))
        self.assertIn("previous == 1 && !reverse", helper)
        self.assertIn("rvc-gear-callback", helper)
        self.assertNotIn("setVPSFollowUpTime", helper)
        self.assertNotIn("updatePDCFailure", helper)
        self.assertIn("mpq_rvc_instant_on.sh", menu)
        self.assertIn("mpq_rvc_instant_off.sh", menu)
        self.assertIn("mpq_rvc_status.sh", menu)
        status = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_rvc_status.sh").read_text()
        self.assertIn("Stock reverse-listener bridge", status)
        self.assertIn("Reverse callback", status)
        self.assertIn("Last close request", status)
        self.assertNotIn("rvc-followup", uninstall)
        disable = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_disable.sh").read_text()
        self.assertNotIn("rvc-followup", disable)
        self.assertIn("ServiceManager.eventMain", helper)

    def test_rvc_fix_defaults_off_but_survives_updates(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        self.assertIn("[ -s $MPQ/rvc-instant ] || echo 0", install)
        self.assertNotIn("echo 0 > $MPQ/rvc-instant ||", install.split("[ -s $MPQ/rvc-instant ]", 1)[0])

    def test_iap2_mismatch_uses_runtime_guards(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        hook = (ROOT / "native/src/ipod/ipod.c").read_text()
        self.assertIn("WARNING: iAP2 driver checksum differs", install)
        self.assertNotIn("Aborted: wrong iAP2 driver", install)
        for guard in ("packet_addparam_blob", "ident_info_funcs", "iap2_ctrl_msg_table",
                      "Location slot mismatch", "receive entry mismatch"):
            self.assertIn(guard, hook)

    def test_unknown_gal_aborts_with_diagnostics(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        diag = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_diagnostics.sh").read_text()
        self.assertIn("unknown P0478T cpu/cpuplus GAL combination", install)
        common = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_common.sh").read_text()
        self.assertIn("mpq_fingerprint $GAL", common)
        self.assertIn("mst2-pq-phone-nav-diagnostics.txt", diag)
        self.assertIn("No unit files were changed", diag)
        self.assertIn("mpq_fingerprint /tsd/hmi/ifs/MIBHMI.jxe", common)

    def test_failed_install_exports_report(self):
        install = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_install.sh").read_text()
        common = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_common.sh").read_text()
        self.assertIn("mpq_save_report $CARD", install)
        self.assertIn("mst2-pq-phone-nav-diagnostics.txt", common)
        self.assertIn("mpq_report > $MPQ_REPORT", common)

    def test_bundled_qnx_hash_helper(self):
        helper = ROOT / "toolbox/custom/mst2-pq-phone-nav/sha256-check"
        self.assertEqual(helper.stat().st_size, 9051)
        self.assertEqual(hashlib.sha256(helper.read_bytes()).hexdigest(),
                         "0c510051f2e79cad6d7868d94abda76310f1fff960a2b6a9ef267106a85bb301")
        common = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_common.sh").read_text()
        self.assertNotIn("cksum", common)
        self.assertIn("sha256-check", common)

    def test_all_target_hashes_are_complete(self):
        common = (ROOT / "toolbox/custom/greenmenu/scripts/mpq_common.sh").read_text()
        tokens = re.findall(r"mpq_matches\s+\S+\s+([A-F0-9]+)", common)
        self.assertEqual(len(tokens), 6)
        self.assertTrue(all(len(token) == 64 for token in tokens))
        profile = json.loads((ROOT / "profiles/MST2_US_VW_PQ_P0478T.json").read_text())
        expected = {
            profile["variants"]["cpu"]["hmi_jxe"]["sha256"].upper(),
            profile["variants"]["cpu"]["libgal"]["sha256"].upper(),
            profile["variants"]["cpuplus"]["hmi_jxe"]["sha256"].upper(),
            profile["variants"]["cpuplus"]["libgal"]["sha256"].upper(),
        }
        self.assertTrue(expected.issubset(set(tokens)))

if __name__ == "__main__":
    unittest.main()
