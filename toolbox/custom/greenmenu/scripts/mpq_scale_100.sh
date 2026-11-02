#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ
echo 100 > $MPQ/scale
echo "CarPlay and Android Auto projection scale set to stock 100%. Reboot to apply."
