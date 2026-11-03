#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
CARD=$(mpq_card) || { echo "SD payload not found."; exit 1; }
mpq_prepare_hash $CARD || { echo "Bundled SHA-256 verifier cannot run."; exit 1; }
mpq_save_report $CARD || { echo "Unable to write report to SD card."; exit 1; }
cat $MPQ_REPORT
echo "Report saved to SD card root as:"
echo "mst2-pq-phone-nav-diagnostics.txt"
echo "No unit files were changed."
