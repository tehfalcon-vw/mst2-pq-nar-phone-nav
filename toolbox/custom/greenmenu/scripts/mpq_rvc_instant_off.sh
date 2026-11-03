#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ || { echo "RVC restore request failed. Unit was not rebooted."; exit 1; }
echo 0 > $MPQ/rvc-instant && rm -f $MPQ/rvc-gear-init $MPQ/rvc-gear-callback $MPQ/rvc-gear-history $MPQ/rvc-close-request || {
  echo "RVC restore request failed. Unit was not rebooted."
  exit 1
}
echo "Aftermarket RVC instant close disabled; stock runtime behavior restored."
mpq_reboot
