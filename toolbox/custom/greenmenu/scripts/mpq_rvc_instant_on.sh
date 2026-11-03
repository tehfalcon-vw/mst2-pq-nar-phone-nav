#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
[ -f $MPQ/enabled ] || {
  echo "Aborted: enable the main mod permanently before testing the RVC fix."
  exit 1
}
mkdir -p $MPQ || { echo "RVC setting failed. Unit was not rebooted."; exit 1; }
echo 1 > $MPQ/rvc-instant && rm -f $MPQ/rvc-gear-init $MPQ/rvc-gear-callback $MPQ/rvc-gear-history $MPQ/rvc-close-request || {
  echo "RVC setting failed. Unit was not rebooted."
  exit 1
}
echo "Aftermarket RVC instant close requested."
echo "No vehicle setting is changed; the stock reverse listener bridge will start."
mpq_reboot
