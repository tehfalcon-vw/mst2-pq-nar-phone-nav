#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
[ ! -s $MPQ/rvc-followup-stock -o -f $MPQ/rvc-followup-restored ] || {
  echo "Aborted: restore the stock RVC delay before disabling the main mod."
  exit 1
}
rm -f $MPQ/enabled $MPQ/trial /dev/shmem/mst2-cluster-nav-on /dev/shmem/mst2-cluster-nav-trial
touch /dev/shmem/mst2-cluster-nav-off
echo "Disabled. Reboot to return to stock behavior."
