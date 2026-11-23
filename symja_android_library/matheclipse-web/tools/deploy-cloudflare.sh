#!/usr/bin/env bash
# deploy-cloudflare.sh - assemble and deploy the Symja notebook as a purely static
# Cloudflare Workers site (assets only, no Worker script, no R2).
#
# Steps: validate inputs -> assemble target/cf-site/ -> wrangler deploy.
# The notebook (built by tools/build-notebook-site.sh into target/notebook-site/)
# is served at "/", and the CheerpJ spike stays reachable at "/spike".
# Credentials: matheclipse-web/.env (git-ignored; CF_ACCOUNT_ID, CF_WORKER_TOKEN)
# if it exists, otherwise the macOS login Keychain, where they are stored once with
#   security add-generic-password -a "$USER" -s symja-cf-account-id -w
#   security add-generic-password -a "$USER" -s symja-cf-worker-token -w
# (-w last makes security prompt for the value, so it never reaches the shell
# history). They are loaded only inside this script and passed only to wrangler;
# they are never printed and tracing is never enabled.
#
# Usage: tools/deploy-cloudflare.sh [--assemble-only]
#   --assemble-only  build and validate target/cf-site/ but do not deploy
set -euo pipefail

# Resolve the module root so the script works from any working directory.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NOTEBOOK_SRC="$ROOT/target/notebook-site"
SPIKE_SRC="$ROOT/src/main/site"
OUT="$ROOT/target/cf-site"
# Pinned so deploys are reproducible.
WRANGLER="wrangler@4.166.0"
# Cloudflare static assets reject files larger than 25 MiB.
MAX_BYTES=$((25 * 1024 * 1024))

# Fail early, before touching anything, if the notebook site was never built.
for f in "$NOTEBOOK_SRC/index.html" "$NOTEBOOK_SRC/jars/classpath.txt" \
         "$SPIKE_SRC/spike.html" "$SPIKE_SRC/spike-worker.js"; do
  if [ ! -f "$f" ]; then
    echo "ERROR: $f is missing; run tools/build-notebook-site.sh first." >&2
    exit 1
  fi
done

# Assemble from scratch so stale files never get uploaded.
rm -rf "$OUT"
mkdir -p "$OUT"
cp -R "$NOTEBOOK_SRC"/. "$OUT"/
# Only the spike page and its worker are added (not the old spike index.html or
# jars): the spike reads /jars/classpath.txt, so it runs on the notebook's jars.
cp "$SPIKE_SRC/spike.html" "$SPIKE_SRC/spike-worker.js" "$OUT"/

# Cache headers, read by Cloudflare static assets. Rules are matched top to
# bottom and every matching rule ADDS its headers, so the classpath.txt rule must
# first detach (!) the Cache-Control inherited from /jars/* before setting its own.
cat > "$OUT/_headers" <<'HEADERS'
# Jar names carry a content hash, so a given URL never changes: cache for a year
# and tell browsers not to revalidate.
/jars/*
  Cache-Control: public, max-age=31536000, immutable

# classpath.txt keeps a fixed name but lists the current hashed jars, so it must
# always be revalidated or visitors would see stale jar names after a redeploy.
/jars/classpath.txt
  ! Cache-Control
  Cache-Control: no-cache

# HTML and JS (index.html, kernel*.js, spike*) keep Cloudflare's default
# (revalidate on every load), so no rule is needed here.
HEADERS

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

# Stop here when only the assembly was requested.
if [ "${1:-}" = "--assemble-only" ]; then
  echo "Assembled $OUT ($(find "$OUT" -type f | wc -l | tr -d ' ') files)."
  exit 0
fi

# Load the credentials silently: from .env if present, else from the Keychain.
# The item names can be overridden (only useful to test the missing-item path).
ACCOUNT_ITEM="${SYMJA_CF_ACCOUNT_ITEM:-symja-cf-account-id}"
TOKEN_ITEM="${SYMJA_CF_TOKEN_ITEM:-symja-cf-worker-token}"
CF_ACCOUNT_ID=""
CF_WORKER_TOKEN=""
if [ -f "$ROOT/.env" ]; then
  set -a
  # shellcheck disable=SC1091
  . "$ROOT/.env"
  set +a
elif command -v security > /dev/null 2>&1; then
  # stderr is dropped so a missing item reports only through the check below.
  CF_ACCOUNT_ID="$(security find-generic-password -s "$ACCOUNT_ITEM" -w 2> /dev/null || true)"
  CF_WORKER_TOKEN="$(security find-generic-password -s "$TOKEN_ITEM" -w 2> /dev/null || true)"
fi
if [ -z "$CF_ACCOUNT_ID" ] || [ -z "$CF_WORKER_TOKEN" ]; then
  cat >&2 <<EOF
ERROR: no Cloudflare credentials. Either create $ROOT/.env with
CF_ACCOUNT_ID and CF_WORKER_TOKEN, or store them in the macOS Keychain:
  security add-generic-password -a "\$USER" -s $ACCOUNT_ITEM -w
  security add-generic-password -a "\$USER" -s $TOKEN_ITEM -w
EOF
  exit 1
fi

# Hand them to wrangler under the names it expects, and to nothing else.
cd "$ROOT/cloudflare"
CLOUDFLARE_ACCOUNT_ID="$CF_ACCOUNT_ID" CLOUDFLARE_API_TOKEN="$CF_WORKER_TOKEN" \
  npx --yes "$WRANGLER" deploy
