#!/bin/sh
set -e
HERE="$(cd "$(dirname "$0")" && pwd)"
OUT="$HERE/dist"
rm -rf "$OUT"
mkdir -p "$OUT/sd"
cp -R "$HERE/toolbox/custom" "$OUT/sd/custom"
(cd "$OUT" && zip -qr mst2-pq-nar-phone-nav-p0478t-beta.zip sd)
sha256sum "$OUT/mst2-pq-nar-phone-nav-p0478t-beta.zip" > "$OUT/SHA256SUMS.txt"
echo "$OUT/mst2-pq-nar-phone-nav-p0478t-beta.zip"
