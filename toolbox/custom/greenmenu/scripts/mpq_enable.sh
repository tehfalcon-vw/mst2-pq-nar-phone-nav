#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ && touch $MPQ/enabled && rm -f $MPQ/trial /dev/shmem/mst2-cluster-nav-off
echo "Enabled permanently. Reboot to apply."
