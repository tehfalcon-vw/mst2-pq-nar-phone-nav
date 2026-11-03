#!/bin/ksh
MPQ=/tsd/var/mst2-pq-phone-nav
LIBDIR=/tsd/lib/mst2-pq-phone-nav
HMI_SH=/tsd/hmi/runHMI.sh
CPU_CONF=/tsd/etc/system/cpu.conf
GAL=/tsd/lib/sal/gal/libext.google.gal.receiver.so

mpq_card() {
  for v in /media/mp00?; do
    [ -d $v/custom/mst2-pq-phone-nav ] && { echo $v; return 0; }
  done
  return 1
}

mpq_prepare_hash() {
  MPQ_HASH=$1/custom/mst2-pq-phone-nav/sha256-check
  chmod 755 $MPQ_HASH 2>/dev/null && [ -x $MPQ_HASH ]
}

mpq_hash() { $MPQ_HASH "$1" 2>/dev/null; }

mpq_matches() {
  [ -f "$1" ] && [ "$(mpq_hash "$1")" = "$2" ]
}

mpq_fingerprint() {
  h=$(mpq_hash "$1") || h=unreadable
  set -- $(wc -c < "$1" 2>/dev/null)
  echo "sha256=$h size=${1:-unreadable}"
}

mpq_variant() {
  if mpq_matches /tsd/hmi/ifs/MIBHMI.jxe DEBB6893BC907432BC58E079567F31135572DA2F8DC05E2DE12948E5AE8EDC93; then
    { mpq_matches $GAL B030FFF517D641D3D6B72DD748565ADEC58368A1875C8E5FD58C83DD03116EB ||
      mpq_matches $GAL 57BC9B5B387970F4BFB0B09670B0A4D3CEC39CCAEDBB192BBA56BBE5F800F096; } &&
      { echo cpu; return 0; }
  fi
  if mpq_matches /tsd/hmi/ifs/MIBHMI.jxe E001E0A4861F5615E454FCC880F9C86E3A39282853A830A86E0AFBF1005CBEE8; then
    { mpq_matches $GAL C80164D62B69478C7F7AB7B2719B6CED7A529D7A24C2CD386CB6C399B22AE8C2 ||
      mpq_matches $GAL ECAEBC72D1EC5027AB36E65A65E63F3956AE8B5F8097894F8866333510471635; } &&
      { echo cpuplus; return 0; }
  fi
  return 1
}

mpq_mount_rw() { . /tsd/etc/persistence/esd/scripts/util_mount.sh; }
mpq_mount_ro() { . /tsd/etc/persistence/esd/scripts/util_mount_ro.sh; }

mpq_strip_hmi() { sed '/^# >>> mst2-pq-phone-nav/,/^# <<< mst2-pq-phone-nav/d' "$1"; }
mpq_hmi_with_block() { mpq_strip_hmi $HMI_SH | sed '/^BOOTCLASSPATH="\$IFSDIR\/tsd.mibstd2.hmi.v2.jxe:/r '"$1"; }

mpq_unpreload() {
  sed -e '/^   envar LD_PRELOAD \/tsd\/lib\/mst2-pq-phone-nav\/libmcn_sal.so$/d' \
      -e 's#:/tsd/lib/mst2-pq-phone-nav/libmcn_sal.so##g' $CPU_CONF
}

mpq_preload() {
  awk -v lib="$LIBDIR/libmcn_sal.so" '
    function flush(){if(sal && !done) print "   envar LD_PRELOAD " lib}
    /^service /{flush(); sal=($2=="iMX6.SAL"); done=0; print; next}
    sal && $1=="envar" && $2=="LD_PRELOAD" && !done{print $0 ":" lib; done=1; next}
    {print} END{flush()}' "$1"
}

mpq_rewrite() {
  f=$1; shift
  cp -p "$f" "$f.mpq" && "$@" > "$f.mpq" && [ -s "$f.mpq" ] && mv -f "$f.mpq" "$f"
  rc=$?; rm -f "$f.mpq"; return $rc
}

mpq_put() {
  cp "$1" "$2.new" && cmp -s "$1" "$2.new" && chmod "$3" "$2.new" && mv -f "$2.new" "$2"
  rc=$?; rm -f "$2.new"; return $rc
}
