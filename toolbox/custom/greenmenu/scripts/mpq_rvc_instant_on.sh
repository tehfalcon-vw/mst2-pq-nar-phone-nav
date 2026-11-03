#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
[ -f $MPQ/enabled ] || {
  echo "Aborted: enable the main mod permanently before testing the RVC fix."
  exit 1
}
mkdir -p $MPQ || { echo "RVC setting failed. Unit was not rebooted."; exit 1; }
echo 1 > $MPQ/rvc-instant && rm -f $MPQ/rvc-followup-restored $MPQ/rvc-dsi-init $MPQ/rvc-dsi-callback || {
  echo "RVC setting failed. Unit was not rebooted."
  exit 1
}
echo "Aftermarket RVC instant close requested."
echo "The factory follow-up value will be saved before it is changed."
mpq_reboot
