#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "MST2-PQ phone navigation: install"
[ "$(sed -n 's/^VERSION=//p' $HMI_SH | head -1)" = "H29.319.93" ] || { echo "Aborted: wrong HMI version."; exit 1; }
mpq_cksum /lib/dll/ipod-drvr-iap2.so 771102 1621430627 || { echo "Aborted: wrong iAP2 driver."; exit 1; }
VARIANT=$(mpq_variant) || { echo "Aborted: this is not an untouched P0478T cpu/cpuplus target."; exit 1; }
CARD=$(mpq_card) || { echo "Aborted: beta files not found on SD card."; exit 1; }
SRC=$CARD/custom/mst2-pq-phone-nav
mkdir -p $MPQ/backup $CARD/mst2-pq-phone-nav-backup
[ -s $MPQ/scale ] || echo 100 > $MPQ/scale
[ -s $MPQ/backup/runHMI.sh ] || cp -p $HMI_SH $MPQ/backup/runHMI.sh
[ -s $MPQ/backup/cpu.conf ] || cp -p $CPU_CONF $MPQ/backup/cpu.conf
[ -s $MPQ/backup/libext.google.gal.receiver.so ] || cp -p $GAL $MPQ/backup/libext.google.gal.receiver.so
cp -p $MPQ/backup/* $CARD/mst2-pq-phone-nav-backup/
mpq_put $SRC/mst2-pq-nar-phone-nav.jar $MPQ/mst2-pq-nar-phone-nav.jar 644 || exit 1
mpq_mount_rw
mkdir -p $LIBDIR
mpq_put $SRC/libmcn_sal.so $LIBDIR/libmcn_sal.so 755 || { mpq_mount_ro; exit 1; }
mpq_put $SRC/libmcn_ipod.so $LIBDIR/libmcn_ipod.so 755 || { mpq_mount_ro; exit 1; }
mpq_put $SRC/libext.google.gal.receiver.$VARIANT.so $GAL 755 || { mpq_mount_ro; exit 1; }
mpq_rewrite $HMI_SH mpq_hmi_with_block $SRC/runHMI.block || { mpq_mount_ro; exit 1; }
mpq_unpreload > $MPQ/cpu.clean && mpq_rewrite $CPU_CONF mpq_preload $MPQ/cpu.clean
rm -f $MPQ/cpu.clean
mpq_mount_ro
rm -f $MPQ/enabled
touch $MPQ/trial /dev/shmem/mst2-cluster-nav-armed
echo "Installed for $VARIANT. NEXT boot is a one-boot trial."
echo "Projection scale is 100% for the first trial. Select 115% after validation."
echo "After a successful drive, choose Enable permanently."
