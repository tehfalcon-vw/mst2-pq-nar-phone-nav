# MST2-PQ NAR Phone Navigation, Media, 125% Scale and RVC Fix

Beta support for CarPlay and Android Auto navigation arrows, now-playing information
and optional 125% projection scaling on a VW PQ MST2 unit. Navigation appears in the
factory cluster Navigation page; song, artist and album appear in the MFD audio page.
PQ has no MOST link to the cluster, so phone map video is not part of this project.
An optional aftermarket-camera mode removes the factory rear-view-camera follow-up
delay after reverse is disengaged.

## Supported firmware

| Item | Required value |
|---|---|
| Train | `MST2_US_VW_PQ_P0478T` |
| MU | `0478` |
| Part number | `3G0919360JP` |
| HMI | `H29.319.93` |
| Unit | TechniSat/Preh MST2-PQ (`T` suffix) |

Both `stdNavi` (`cpu`) and `stdPlus` (`cpuplus`) variants from this release are
included. The installer uses its bundled QNX SHA-256 verifier and requires the exact
HMI and Android Auto library. A differing iAP2 hash is reported as a warning because the hook resolves symbols dynamically
and validates every modified slot at runtime; an incompatible driver remains stock.

This has been built and statically checked against the supplied P0478T firmware. The
`cpu` variant, CarPlay controls and 125% scale have been validated on one physical NAR
unit; other vehicles, clusters, phones and the `cpuplus` variant remain beta coverage.

## What it adds

- Android Auto navigation through the proven GAL navigation endpoint bridge.
- CarPlay navigation through iAP2 Route Guidance.
- Output through the stock PQ `mqbpq` NavSD/BAP functions.
- CarPlay and Android Auto title, artist and album on the cluster MFD audio page.
- Selectable stock 100% or enlarged 125% CarPlay and Android Auto screen scale.
- Optional instant rear-view-camera close for aftermarket cameras that power down as
  soon as the vehicle leaves reverse.
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

1. Download the P0478T ZIP from the latest release.
2. Extract it and open the included `sd` folder.
3. Merge its `custom` folder into the root of your existing MIB STD2 Toolbox SD card.
   Keep all Toolbox files already on the card.
4. Insert the card and open the Green Engineering Menu.
5. Open **Toolbox → Customization → Greenmenu**.
6. Choose **Copy custom GEM scripts to unit**, then reboot. After that reboot,
   **MST2_PQ_Phone_Nav** appears under **Toolbox → Customization**.
7. Open **MST2_PQ_Phone_Nav** and choose **Install / update**. An iAP2 fingerprint warning is allowed and prints the
   unit's actual SHA-256 and size. If the HMI/GAL result says `Aborted`, stop; never bypass it.
8. After a successful installation, the unit reboots automatically into a one-boot
   trial. Every install/update resets projection scaling to stock 100%.
9. Connect a phone, start a route in CarPlay or Android Auto, and select the cluster's
   factory Navigation page. Play audio and verify title, artist and album in the MFD
   audio page.
10. If the radio, phone connection, controls and MFD remain stable, return to the same
    menu and choose **Enable permanently**. A successful enable reboots automatically.
11. The first trial uses stock 100% projection scale. After navigation, media metadata,
    CarPlay/Android Auto controls and reconnect all pass,
    choose **Projection scale: 125%**, reboot, and verify CarPlay and Android Auto touch
    alignment. Choose **Projection scale: stock 100%** and reboot to revert the scale.

## Optional aftermarket rear-view-camera fix

Use this only when an aftermarket camera turns off immediately after leaving reverse
and the factory display delay produces a blank image or **ParkPilot is currently
unavailable** message.

1. First confirm the normal phone-navigation installation is stable and permanently
   enabled.
2. Open **Toolbox → Customization → MST2_PQ_Phone_Nav**.
3. Choose **Aftermarket RVC: instant close**. The unit reboots automatically.
4. Engage reverse, wait for the camera image, then shift out of reverse while stopped.
   The camera page should close immediately without the ParkPilot error.
5. Reopen the menu and choose **Aftermarket RVC: show status**. It should report
   **Parking-state hooks: parking-state-hooks-loaded**. When the aftermarket-camera
   fault occurs, **VPS failure callback** should normally show `state=vps` or
   `state=ops-vps` with `failed=1`. Units that report the fault through the OPS side
   may instead populate **PDC failure callback**. On the confirmed P0478T hardware,
   both failure lines can remain `not-received`; **Last close request** should show
   `state-exit state=vps delivered=1`. Other coding may show a delivered VPS/PDC
   failure close instead.
   Diagnostic beta builds also print an **Active-camera signal timeline** containing
   popup requests, content updates, acknowledgements, camera on/off changes and state
   entry/exit events. Include the complete timeline in the tester report.
6. Repeat several times, then test normal parking-sensor activation and its on-screen
   close button.

The fix is independent of CarPlay and Android Auto. Hardware tracing on P0478T showed
that this PQ unit publishes neither a usable reverse gear transition nor a VPS/PDC
failure or camera-system callback. Its only reliable reverse-exit edge is HSM exit from
the active VPS state. With the optional mode enabled, the overlay sends the stock
`CLOSE_PARKING` event as that active VPS or OPS+VPS state exits, while allowing the
original state transition to continue normally. With the option off, state handling is
unchanged. Failure-channel interception remains as a fallback for other unit codings.

Hardware tracing confirmed that P0478T can dispatch the camera failure directly to the
`StatePopupOff` receiver only after both the active VPS and wait-for-close states have
exited. The final receiver therefore suppresses a true VPS or PDC failure only within
ten seconds of an enabled camera-state exit and only when stock close-event delivery
succeeds. Outside that narrow window its original failure handling remains unchanged.

**Warning:** this optional mode closes parking whenever the active camera state exits,
including a transition away from the camera for a reason other than leaving reverse.
It may also suppress a genuine parking-system fault reported during the camera popup.
Disable the mode before diagnosing parking-sensor trouble. To undo it, choose
**Aftermarket RVC: disable instant close** and allow the automatic reboot.

The installer saves stock files on the unit and in
`mst2-pq-phone-nav-backup` on the SD card. Keep the SD-card copy.

### Updating an earlier beta

Merge the new release's `custom` folder onto the Toolbox card, then repeat **Toolbox →
Customization → Greenmenu → Copy custom GEM scripts to unit** and reboot. This refreshes
the installer and adds any new menu actions. After reboot, open
**MST2_PQ_Phone_Nav → Install / update** and complete the new one-boot trial before
choosing **Enable permanently** again.

If installation reports an unknown HMI/GAL combination, it automatically writes
`mst2-pq-phone-nav-diagnostics.txt` to the SD-card root. You can also create it with
**Export compatibility report to SD card**. Share that file; the diagnostic changes
no unit files. Unknown GAL builds are not overwritten because they may
contain an existing App-Connect or FEC patch that must be preserved deliberately.

## Emergency disable and uninstall

To force the mod off before the HMI starts, create an empty file named
`mst2-pq-phone-nav-disable` in the root of the Toolbox SD card, insert it, and boot.

For a normal removal, choose **Uninstall and restore stock files**, wait for success,
and reboot. The RVC watcher is runtime-only, so removing the overlay restores stock
behavior without leaving a vehicle setting behind.

## Tester report

Please include:

- Software train, part number and whether the installer selected `cpu` or `cpuplus`.
- Vehicle, model year, cluster part number and display type.
- Phone model, OS version, CarPlay/Android Auto connection type and navigation app.
- Which fields worked: route-active state, arrow, distance, street and destination time.
- Whether title, artist and album update, clear on disconnect and switch correctly
  between CarPlay and Android Auto.
- Whether 125% works for both phone protocols without cropped controls or touch offset.
- For the optional RVC fix: original delay behavior, instant-close result, repeated
  reverse cycles, parking-sensor button behavior, all three status lines, and whether
  disabling the option restores stock behavior.
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
