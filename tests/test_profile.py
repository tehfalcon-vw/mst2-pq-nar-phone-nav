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

    def test_release_payload_is_navigation_only(self):
        names = {p.name for p in (ROOT / "toolbox/custom/mst2-pq-phone-nav").iterdir()}
        self.assertIn("libmcn_sal.so", names)
        self.assertIn("libmcn_ipod.so", names)
        self.assertFalse(any("dm" in n or "video" in n for n in names))

if __name__ == "__main__":
    unittest.main()
