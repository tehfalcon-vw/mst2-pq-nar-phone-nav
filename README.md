# MST2-PQ NAR Phone Navigation, Media and 115% Scale

Beta support for CarPlay and Android Auto navigation arrows, now-playing information
and optional 115% projection scaling on a VW PQ MST2 unit. Navigation appears in the
factory cluster Navigation page; song, artist and album appear in the MFD audio page.
PQ has no MOST link to the cluster, so phone map video is not part of this project.

## Supported firmware

| Item | Required value |
|---|---|
| Train | `MST2_US_VW_PQ_P0478T` |
| MU | `0478` |
| Part number | `3G0919360JP` |
| HMI | `H29.319.93` |
| Unit | TechniSat/Preh MST2-PQ (`T` suffix) |

Both `stdNavi` (`cpu`) and `stdPlus` (`cpuplus`) variants from this release are
included. The installer requires the exact HMI and Android Auto library. A differing
iAP2 checksum is reported as a warning because the hook resolves symbols dynamically
and validates every modified slot at runtime; an incompatible driver remains stock.

This has been built and statically checked against the supplied P0478T firmware but has
not yet been tested in a vehicle. Treat it as an early beta.

## What it adds

- Android Auto navigation through the proven GAL navigation endpoint bridge.
- CarPlay navigation through iAP2 Route Guidance.
- Output through the stock PQ `mqbpq` NavSD/BAP functions.
- CarPlay and Android Auto title, artist and album on the cluster MFD audio page.
- Selectable stock 100% or enlarged 115% CarPlay and Android Auto screen scale.
- Support for both Android Auto DSI generations present in P0478T.
- A one-boot trial, emergency kill switch, stock backups and uninstall.
- No cluster map video, MOST code, display-manager hook or album art.

## Before installing

You need:

- The exact firmware shown above, already running on the unit.
- A working [MIB STD2 Toolbox](https://github.com/olli991/mib-std2-pq-zr-toolbox)
  SD card and its Green Engineering Menu.
- The beta ZIP from this repository's Releases page.
- Stable vehicle power and an untouched backup of the unit.

Do not install on ZR, Delphi, another firmware train, or a unit without the factory
Navigation page in the cluster. Do not test while driving.

## Install the beta

1. Download `mst2-pq-nar-phone-nav-p0478t-beta.zip` from the latest release.
2. Extract it and open the included `sd` folder.
3. Merge its `custom` folder into the root of your existing MIB STD2 Toolbox SD card.
   Keep all Toolbox files already on the card.
4. Insert the card and open the Green Engineering Menu.
5. Open **Customization → MST2-PQ NAR CarPlay + Android Auto navigation arrows**.
6. Choose **Install / update**. An iAP2 checksum warning is allowed and prints the
   unit's actual checksum. If the HMI/GAL result says `Aborted`, stop; never bypass it.
7. Reboot. The next boot is a one-boot trial.
8. Connect a phone, start a route in CarPlay or Android Auto, and select the cluster's
   factory Navigation page. Play audio and verify title, artist and album in the MFD
   audio page.
9. If the radio, phone connection, controls and MFD remain stable, return to the same
   menu and choose **Enable permanently**, then reboot once more.
10. The first trial uses stock 100% projection scale. After the base features pass,
    choose **Projection scale: 115%**, reboot, and verify CarPlay and Android Auto touch
    alignment. Choose **Projection scale: stock 100%** and reboot to revert the scale.

The installer saves stock files on the unit and in
`mst2-pq-phone-nav-backup` on the SD card. Keep the SD-card copy.

## Emergency disable and uninstall

To force the mod off before the HMI starts, create an empty file named
`mst2-pq-phone-nav-disable` in the root of the Toolbox SD card, insert it, and boot.

For a normal removal, open the same Green Engineering Menu page, choose **Uninstall and
restore stock files**, wait for success, and reboot.

## Tester report

Please include:

- Software train, part number and whether the installer selected `cpu` or `cpuplus`.
- Vehicle, model year, cluster part number and display type.
- Phone model, OS version, CarPlay/Android Auto connection type and navigation app.
- Which fields worked: route-active state, arrow, distance, street and destination time.
- Whether title, artist and album update, clear on disconnect and switch correctly
  between CarPlay and Android Auto.
- Whether 115% works for both phone protocols without cropped controls or touch offset.
- Whether disconnect, reconnect, trial boot, permanent enable, kill switch and uninstall worked.
- Relevant `/tsd/var/mst2-pq-phone-nav` and SAL log output.

Do not upload firmware archives.

## Build from source

The HMI overlay requires JDK 8 and a `MIBHMI.jar` converted from your own P0478T
`MIBHMI.jxe`:

```sh
MIBHMI=/path/to/MIBHMI.jar JAVAC=/path/to/jdk8/bin/javac ./java/build.sh
./native/check.sh
CLANG=clang LLD=ld.lld ./native/build.sh
./aa-shim/build.sh /path/to/libext.google.gal.receiver.so
```

`package.sh` lays out the Toolbox SD-card ZIP after the built artifacts have been staged
under `toolbox/custom/mst2-pq-phone-nav`.

## Credits and licenses

The Android Auto bridge and PQ NavSD shadows are adapted from
[MrGame11/mib-std2-AAtoKombi](https://github.com/MrGame11/mib-std2-AAtoKombi).
The CarPlay iAP2 Route Guidance decoder and native hooks are adapted from
[perkthefox/mst2-cluster-nav](https://github.com/perkthefox/mst2-cluster-nav).
Projection scaling is adapted from
[ipaifix/mst2-carplay-scale](https://github.com/ipaifix/mst2-carplay-scale), with an
Android Auto port for both P0478T DSI generations. PQ media integration is adapted
from [xPaiiN/mst2-carplay-vc](https://github.com/xPaiiN/mst2-carplay-vc) and the PQ
AudioSD work in AAtoKombi.

See the four license files and `NOTICE`. The combined release is non-commercial because
the CarPlay route-guidance portion is CC BY-NC-SA 4.0.
