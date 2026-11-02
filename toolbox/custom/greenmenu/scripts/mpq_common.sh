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

mpq_cksum() {
  f=$1; expected_size=$2; expected_crc=$3
  set -- $(cksum "$f" 2>/dev/null)
  [ "$2" = "$expected_size" -a "$1" = "$expected_crc" ]
}

mpq_variant() {
  if mpq_cksum /tsd/hmi/ifs/MIBHMI.jxe 42329796 2318895772; then
    { mpq_cksum $GAL 1455784 1912803236 || mpq_cksum $GAL 1512328 1814017279; } &&
      { echo cpu; return 0; }
  fi
  if mpq_cksum /tsd/hmi/ifs/MIBHMI.jxe 41745824 2584275572; then
    { mpq_cksum $GAL 1455800 1947749194 || mpq_cksum $GAL 1512328 1567479481; } &&
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
