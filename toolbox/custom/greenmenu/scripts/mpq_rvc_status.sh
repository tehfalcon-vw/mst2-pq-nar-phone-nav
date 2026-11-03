#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "Aftermarket RVC instant-close status"
if [ "$(sed -n '1p' $MPQ/rvc-instant 2>/dev/null)" = 1 ]; then
  echo "Requested: ON"
else
  echo "Requested: OFF"
fi
echo "Parking-state hooks: $(sed -n '1p' $MPQ/rvc-parking-hook 2>/dev/null || echo not-loaded)"
echo "VPS failure callback: $(sed -n '1p' $MPQ/rvc-vps-callback 2>/dev/null || echo not-received)"
echo "PDC failure callback: $(sed -n '1p' $MPQ/rvc-pdc-callback 2>/dev/null || echo not-received)"
echo "Last close request: $(sed -n '1p' $MPQ/rvc-close-request 2>/dev/null || echo none)"
