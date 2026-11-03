#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "MST2-PQ phone navigation: install"
[ "$(sed -n 's/^VERSION=//p' $HMI_SH | head -1)" = "H29.319.93" ] || { echo "Aborted: wrong HMI version."; exit 1; }
CARD=$(mpq_card) || { echo "Aborted: beta files not found on SD card."; exit 1; }
SRC=$CARD/custom/mst2-pq-phone-nav
mpq_prepare_hash $CARD || { echo "Aborted: bundled SHA-256 verifier cannot run."; exit 1; }
if mpq_matches /lib/dll/ipod-drvr-iap2.so B0F52FC0F4B5FDA09E3E1AB75CBB23E9252173DA3199D998A947D9DF6D846F49; then
  echo "Stock P0478T iAP2 driver verified."
else
  echo "WARNING: iAP2 driver checksum differs; runtime ABI guards will validate it."
  echo "Actual iAP2 fingerprint:"
  mpq_fingerprint /lib/dll/ipod-drvr-iap2.so
fi
VARIANT=$(mpq_variant) || {
  echo "Aborted: unknown P0478T cpu/cpuplus GAL combination."
  if mpq_save_report $CARD; then
    echo "Compatibility report saved to SD card:"
    echo "mst2-pq-phone-nav-diagnostics.txt"
  else
    echo "Unable to save compatibility report to SD card."
  fi
  echo "No unit files were changed."
  exit 1
}
mkdir -p $MPQ/backup $CARD/mst2-pq-phone-nav-backup || { echo "Aborted: backup directories failed. Unit was not rebooted."; exit 1; }
echo 100 > $MPQ/scale || { echo "Aborted: scale reset failed. Unit was not rebooted."; exit 1; }
[ -s $MPQ/rvc-instant ] || echo 0 > $MPQ/rvc-instant || { echo "Aborted: RVC default failed. Unit was not rebooted."; exit 1; }
rm -f $MPQ/rvc-gear-init $MPQ/rvc-gear-callback $MPQ/rvc-gear-history $MPQ/rvc-parking-hook $MPQ/rvc-vps-callback $MPQ/rvc-pdc-callback $MPQ/rvc-close-request || { echo "Aborted: stale RVC status cleanup failed. Unit was not rebooted."; exit 1; }
[ -s $MPQ/backup/runHMI.sh ] || cp -p $HMI_SH $MPQ/backup/runHMI.sh || exit 1
[ -s $MPQ/backup/cpu.conf ] || cp -p $CPU_CONF $MPQ/backup/cpu.conf || exit 1
[ -s $MPQ/backup/libext.google.gal.receiver.so ] || cp -p $GAL $MPQ/backup/libext.google.gal.receiver.so || exit 1
cp -p $MPQ/backup/* $CARD/mst2-pq-phone-nav-backup/ || exit 1
mpq_put $SRC/mst2-pq-nar-phone-nav.jar $MPQ/mst2-pq-nar-phone-nav.jar 644 || exit 1
mpq_mount_rw
mkdir -p $LIBDIR
mpq_put $SRC/libmcn_sal.so $LIBDIR/libmcn_sal.so 755 || { mpq_mount_ro; exit 1; }
mpq_put $SRC/libmcn_ipod.so $LIBDIR/libmcn_ipod.so 755 || { mpq_mount_ro; exit 1; }
mpq_put $SRC/libext.google.gal.receiver.$VARIANT.so $GAL 755 || { mpq_mount_ro; exit 1; }
mpq_rewrite $HMI_SH mpq_hmi_with_block $SRC/runHMI.block || { mpq_mount_ro; exit 1; }
mpq_unpreload > $MPQ/cpu.clean && mpq_rewrite $CPU_CONF mpq_preload $MPQ/cpu.clean || {
  rm -f $MPQ/cpu.clean
  mpq_mount_ro
  echo "Aborted: cpu.conf update failed. Unit was not rebooted."
  exit 1
}
rm -f $MPQ/cpu.clean
mpq_mount_ro || { echo "Aborted: read-only remount failed. Unit was not rebooted."; exit 1; }
rm -f $MPQ/enabled && touch $MPQ/trial /dev/shmem/mst2-cluster-nav-armed || { echo "Aborted: trial marker failed. Unit was not rebooted."; exit 1; }
echo "Installed for $VARIANT. NEXT boot is a one-boot trial."
echo "Projection scale was reset to 100% for this trial. Select 125% only after validation."
echo "The existing aftermarket RVC ON/OFF preference was preserved."
echo "After a successful drive, choose Enable permanently."
mpq_reboot
