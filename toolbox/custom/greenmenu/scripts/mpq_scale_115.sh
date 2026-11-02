#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ
echo 115 > $MPQ/scale
echo "CarPlay and Android Auto projection scale set to 115%. Reboot to apply."
