#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
mkdir -p $MPQ || { echo "RVC restore request failed. Unit was not rebooted."; exit 1; }
echo 0 > $MPQ/rvc-instant && rm -f $MPQ/rvc-instant-applied $MPQ/rvc-followup-restored || {
  echo "RVC restore request failed. Unit was not rebooted."
  exit 1
}
echo "Factory RVC follow-up time restore requested."
mpq_reboot
