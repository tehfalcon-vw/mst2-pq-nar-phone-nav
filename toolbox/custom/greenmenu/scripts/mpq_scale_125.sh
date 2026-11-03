#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ
echo 125 > $MPQ/scale
echo "CarPlay and Android Auto projection scale set to 125%. Reboot to apply."
