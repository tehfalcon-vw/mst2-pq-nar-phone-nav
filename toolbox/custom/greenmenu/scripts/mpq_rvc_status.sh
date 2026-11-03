#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "Aftermarket RVC instant-close status"
if [ "$(sed -n '1p' $MPQ/rvc-instant 2>/dev/null)" = 1 ]; then
  echo "Requested: ON"
else
  echo "Requested: OFF"
fi
if [ -s $MPQ/rvc-followup-stock ]; then
  echo "Saved stock follow-up value: $(sed -n '1p' $MPQ/rvc-followup-stock)"
else
  echo "Saved stock follow-up value: not captured"
fi
[ -f $MPQ/rvc-instant-applied ] && echo "Zero delay verified: YES" || echo "Zero delay verified: NO"
[ -f $MPQ/rvc-followup-restored ] && echo "Stock value restored: YES" || echo "Stock value restored: NO"
