#!/bin/sh
set -e
HERE="$(cd "$(dirname "$0")" && pwd)"
for mib in "$@"; do
  MIBHMI="$mib" "$HERE/build.sh"
  cp "$HERE/build/mst2-pq-nar-phone-nav.jar" "$HERE/build/$(basename "$(dirname "$mib")").jar"
done
