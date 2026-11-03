#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "Aftermarket RVC instant-close status"
if [ "$(sed -n '1p' $MPQ/rvc-instant 2>/dev/null)" = 1 ]; then
  echo "Requested: ON"
else
  echo "Requested: OFF"
fi
echo "Stock reverse-listener bridge: $(sed -n '1p' $MPQ/rvc-gear-init 2>/dev/null || echo not-ready)"
echo "Reverse callback: $(sed -n '1p' $MPQ/rvc-gear-callback 2>/dev/null || echo not-received)"
echo "Last close request: $(sed -n '1p' $MPQ/rvc-close-request 2>/dev/null || echo none)"
