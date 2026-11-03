#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
rm -f $MPQ/enabled $MPQ/trial /dev/shmem/mst2-cluster-nav-on /dev/shmem/mst2-cluster-nav-trial
touch /dev/shmem/mst2-cluster-nav-off
echo "Disabled. Reboot to return to stock behavior."
