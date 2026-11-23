#!/usr/bin/env bash
# export-docs.sh - build what DocExport needs, then export the documentation pane's
# server answers as static JSON files for the purely static notebook site.
#
# Usage: export-docs.sh [outDir]
#   outDir defaults to matheclipse-web/target/notebook/doc-json
#   SKIP_BUILD=1 reuses an already compiled matheclipse-io and its saved classpath.
#
# Steps: (1) compile matheclipse-io plus the modules it needs and write its runtime
# classpath to target/doc-export/classpath.txt; (2) run DocExport.java (a single-file
# Java 21 program, never shipped to the browser) on that classpath.
set -euo pipefail

# Resolve paths from the script location so it works from any working directory.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WEB="$(cd "$HERE/../.." && pwd)"          # .../symja_android_library/matheclipse-web
PARENT="$(cd "$WEB/.." && pwd)"           # the Maven reactor root
REPO_ROOT="$(cd "$PARENT/.." && pwd)"     # the git checkout (holds .git)
OUT="${1:-$WEB/target/notebook/doc-json}"
WORK="$WEB/target/doc-export"
CP_FILE="$WORK/classpath.txt"
mkdir -p "$WORK"

if [ "${SKIP_BUILD:-0}" != "1" ] || [ ! -s "$CP_FILE" ]; then
  # Other sessions share this checkout: serialise every mvn call with a lock directory.
  LOCK="$REPO_ROOT/.git/symja-mvn.lock"
  (
    until mkdir "$LOCK" 2>/dev/null; do sleep 15; done
    trap 'rmdir "$LOCK"' EXIT
    cd "$PARENT"
    # -am builds the sibling modules in the reactor, so matheclipse-io's classpath points at their
    # fresh target/classes (which hold the doc/*.md resources) instead of stale ~/.m2 jars.
    # build-classpath runs for each module; matheclipse-io is last, so its file wins.
    mvn -q -B -pl matheclipse-io -am -DskipTests -Dmaven.javadoc.skip=true \
        -Dcheckstyle.skip -Denforcer.skip \
        compile dependency:build-classpath -Dmdep.outputFile="$CP_FILE"
  )
fi

# matheclipse-io's own classes are not in its dependency classpath, so add them.
CP="$PARENT/matheclipse-io/target/classes:$(cat "$CP_FILE")"

rm -rf "$OUT"
mkdir -p "$OUT"
# Single-file source launch: no compile step, no Maven module.
java -cp "$CP" "$HERE/DocExport.java" "$OUT"

# The format description travels with the data (the exporter only writes JSON).
cp "$HERE/doc-json-README.md" "$OUT/README.md"
echo "export-docs: wrote $(find "$OUT" -type f | wc -l | tr -d ' ') files, $(du -sh "$OUT" | cut -f1) to $OUT"
