#!/bin/ksh
. /tsd/etc/persistence/esd/scripts/mpq_common.sh
echo "MST2-PQ phone navigation diagnostics"
echo "HMI version:"
sed -n 's/^VERSION=//p' $HMI_SH | head -1
echo "MIBHMI.jxe cksum and size:"
cksum /tsd/hmi/ifs/MIBHMI.jxe 2>/dev/null || echo "unreadable"
echo "GAL cksum and size:"
cksum $GAL 2>/dev/null || echo "unreadable"
echo "iAP2 cksum and size:"
cksum /lib/dll/ipod-drvr-iap2.so 2>/dev/null || echo "unreadable"
echo "Known variant:"
mpq_variant 2>/dev/null || echo "unknown"
echo "This action does not change files."
