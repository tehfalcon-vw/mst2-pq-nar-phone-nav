#!/bin/sh
set -e
HERE="$(cd "$(dirname "$0")" && pwd)"
MIBHMI="${MIBHMI:?set MIBHMI to the converted P0478T MIBHMI.jar}"
JAVAC="${JAVAC:-javac}"
JAR="${JAR:-jar}"
rm -rf "$HERE/build"
mkdir -p "$HERE/build/classes"
find "$HERE/src" -name '*.java' > "$HERE/build/sources.txt"
"$JAVAC" -source 1.3 -target 1.3 -encoding UTF-8 -nowarn -cp "$MIBHMI" \
  -d "$HERE/build/classes" @"$HERE/build/sources.txt"
rm -f "$HERE/build/classes/org/dsi/ifc/androidauto2/Constants.class"
"$JAR" cf "$HERE/build/mst2-pq-nar-phone-nav.jar" -C "$HERE/build/classes" .
echo "$HERE/build/mst2-pq-nar-phone-nav.jar"
