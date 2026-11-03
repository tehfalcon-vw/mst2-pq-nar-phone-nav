#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "MST2-PQ phone navigation diagnostics"
CARD=$(mpq_card) || { echo "SD payload not found."; exit 1; }
mpq_prepare_hash $CARD || { echo "Bundled SHA-256 verifier cannot run."; exit 1; }
echo "HMI version:"
sed -n 's/^VERSION=//p' $HMI_SH | head -1
echo "MIBHMI.jxe fingerprint:"
mpq_fingerprint /tsd/hmi/ifs/MIBHMI.jxe
echo "GAL fingerprint:"
mpq_fingerprint $GAL
echo "iAP2 fingerprint:"
mpq_fingerprint /lib/dll/ipod-drvr-iap2.so
echo "Known variant:"
mpq_variant 2>/dev/null || echo "unknown"
echo "This action does not change files."
