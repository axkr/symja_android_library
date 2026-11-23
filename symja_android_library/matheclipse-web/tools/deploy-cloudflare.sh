#!/usr/bin/env bash
# deploy-cloudflare.sh - assemble and deploy the Symja CheerpJ spike as a purely
# static Cloudflare Workers site (assets only, no Worker script, no R2).
#
# Steps: validate inputs -> assemble target/cf-site/ -> wrangler deploy.
# Credentials come from matheclipse-web/.env (git-ignored) and are loaded only
# inside this script; the file is never printed and tracing is never enabled.
set -euo pipefail

# Resolve the module root so the script works from any working directory.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SITE_SRC="$ROOT/src/main/site"
JARS_SRC="$ROOT/target/site/jars"
OUT="$ROOT/target/cf-site"
# Pinned so deploys are reproducible.
WRANGLER="wrangler@4.166.0"
# Cloudflare static assets reject files larger than 25 MiB.
MAX_BYTES=$((25 * 1024 * 1024))

# Fail early, before touching anything, if the jar set was never built.
if [ ! -f "$JARS_SRC/classpath.txt" ]; then
  echo "ERROR: $JARS_SRC/classpath.txt is missing; build the jars first." >&2
  exit 1
fi

# Assemble from scratch so stale files never get uploaded.
rm -rf "$OUT"
mkdir -p "$OUT/jars"
cp -R "$SITE_SRC"/. "$OUT"/
cp -R "$JARS_SRC"/. "$OUT/jars"/

# Landing page that links to the spike (kept simple on purpose).
cat > "$OUT/index.html" <<'HTML'
<!doctype html>
<meta charset="utf-8">
<title>Symja web (static CheerpJ spike)</title>
<h1>Symja web</h1>
<p><a href="spike.html">Open the CheerpJ spike</a></p>
HTML

# Enforce the per-file limit; wc -c is portable across macOS and Linux.
too_big=0
while IFS= read -r f; do
  size=$(wc -c < "$f")
  if [ "$size" -gt "$MAX_BYTES" ]; then
    echo "ERROR: $f is $size bytes, over Cloudflare's 25 MiB limit." >&2
    too_big=1
  fi
done < <(find "$OUT" -type f)
[ "$too_big" -eq 0 ] || exit 1

# Load credentials silently and map them to the names wrangler expects.
set -a
# shellcheck disable=SC1091
. "$ROOT/.env"
set +a
export CLOUDFLARE_ACCOUNT_ID="$CF_ACCOUNT_ID"
export CLOUDFLARE_API_TOKEN="$CF_WORKER_TOKEN"

cd "$ROOT/cloudflare"
npx --yes "$WRANGLER" deploy
