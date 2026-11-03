#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
[ -s $MPQ/backup/runHMI.sh -a -s $MPQ/backup/cpu.conf -a -s $MPQ/backup/libext.google.gal.receiver.so ] || { echo "Aborted: stock backup is incomplete."; exit 1; }
mpq_mount_rw
mpq_put $MPQ/backup/runHMI.sh $HMI_SH 755 &&
mpq_put $MPQ/backup/cpu.conf $CPU_CONF 644 &&
mpq_put $MPQ/backup/libext.google.gal.receiver.so $GAL 755
rc=$?
mpq_mount_ro
[ $rc -eq 0 ] || { echo "Restore failed; keep the SD-card backup."; exit 1; }
rm -f $MPQ/enabled $MPQ/trial /dev/shmem/mst2-cluster-nav-on
touch /dev/shmem/mst2-cluster-nav-off
echo "Stock files restored. Reboot, then the mod is removed."
