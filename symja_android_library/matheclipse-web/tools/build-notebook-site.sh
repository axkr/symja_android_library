#!/usr/bin/env bash
# build-notebook-site.sh - assemble the static "notebook site": the existing
# Symja notebook UI (from matheclipse-io) wired to the in-browser CheerpJ
# kernel, as one folder that can be served by any static host (Cloudflare
# Workers assets, tools/range-server.mjs, ...).
#
# Why: the notebook page used to talk to a Java server (/ajax/...). In the
# client-side migration the kernel runs inside the browser (CheerpJ in a Web
# Worker), so the page needs: the unchanged UI files, a few glue files that
# redirect /ajax/ calls to the worker, the kernel jars, and the exported docs.
# This script only *assembles* those parts; other modules/sessions produce them.
#
# Output (target/notebook-site/, see notebook-site-protocol.md):
#   <everything from matheclipse-io src/main/resources/public/>
#       index.html is patched: kernel.css + kernel-config.js + kernel.js are
#       injected right after the prototype.js script tag.
#   kernel-config.js kernel.js kernel-worker.js kernel.css
#       copied from src/main/notebook/
#   jars/<stem>-<sha256 first 8 hex>.jar + jars/classpath.txt
#       from target/notebook/jars/; content-hashed names let the browser cache
#       jars forever (a changed jar always gets a new URL).
#   doc-json/...
#       from target/notebook/doc-json/ (produced by tools/doc-export).
#
# How to run (from any directory):
#   matheclipse-web/tools/build-notebook-site.sh [--mock]
#   --mock lets the page be tried with ?kernel=mock before the real jars/docs exist.
#   Environment: NOTEBOOK_MODULE_DIR overrides the module root (used only to
#   test this script against a scratch copy of the layout).
#
# Safety: everything is built in a staging folder and swapped into place only
# after all checks pass, so a failed run never destroys the previous good site.
# No credentials are read; nothing is deployed.
set -euo pipefail

# Cloudflare static assets reject files over 25 MiB and sites over 20,000 files.
MAX_BYTES=$((25 * 1024 * 1024))
MAX_FILES=20000

# The exact tag that anchors the injection. It must occur exactly once so we
# never patch the wrong place (or silently patch nothing).
PROTOTYPE_TAG='<script type="text/javascript" src="/media/js/prototype/prototype.js"></script>'

# Files that src/main/notebook/ must provide.
NOTEBOOK_FILES="kernel-config.js kernel.js kernel-worker.js kernel.css"

MOCK=0
STAGE=""

# usage - print the help text (stdout for -h, stderr for errors via caller).
usage() {
  cat <<'USAGE'
Usage: build-notebook-site.sh [--mock] [-h|--help]

Assembles matheclipse-web/target/notebook-site/ from:
  1. ../matheclipse-io/src/main/resources/public/   (copied; index.html patched)
  2. src/main/notebook/                              (kernel-config.js kernel.js
                                                      kernel-worker.js kernel.css)
  3. target/notebook/jars/                           (hashed names + classpath.txt)
  4. target/notebook/doc-json/                       (copied as is)

Options:
  --mock      tolerate missing parts 3 and 4: create an empty jars/ folder with an
              empty classpath.txt and a placeholder doc-json/index.json, so the
              page can be tested with ?kernel=mock. Loud warning is printed.
  -h, --help  show this text

Environment:
  NOTEBOOK_MODULE_DIR   module root override (default: parent of this script's folder)

Checks: no file over 25 MiB (fatal), no more than 20,000 files (warning).
Exit status is non-zero on any failure.
USAGE
}

# die - print an error to stderr and exit non-zero.
die() {
  echo "ERROR: $*" >&2
  exit 1
}

# warn - print a warning to stderr, keep going.
warn() {
  echo "WARNING: $*" >&2
}

# cleanup - remove the staging folder if the run ended before the final swap.
cleanup() {
  if [ -n "$STAGE" ] && [ -e "$STAGE" ]; then
    rm -rf "$STAGE"
  fi
}

# sha8 FILE - first 8 hex digits of the file's SHA-256, used in jar names.
sha8() {
  shasum -a 256 "$1" | cut -c1-8
}

# is_empty_dir DIR - true when DIR contains nothing (not even dotfiles).
is_empty_dir() {
  [ -z "$(ls -A "$1")" ]
}

# copy_public - part 1: copy the matheclipse-io public/ folder unchanged.
# The copy must be complete (also dotfiles), hence "dir/." instead of a glob.
copy_public() {
  [ -d "$PUBLIC_SRC" ] || die "missing folder $PUBLIC_SRC (source of the notebook UI; lives in module matheclipse-io)."
  [ -f "$PUBLIC_SRC/index.html" ] || die "missing $PUBLIC_SRC/index.html."
  cp -R "$PUBLIC_SRC"/. "$STAGE"/
  # matheclipse-core also ships part of the page's static files (symja_webgl.js, the 3D plot
  # renderer, moved there by commit 21dab82d5). index.html loads it, so without this copy every
  # Plot3D shows an empty frame. Copied second and without overwriting: the io module wins.
  if [ -d "$CORE_PUBLIC_SRC" ]; then
    cp -Rn "$CORE_PUBLIC_SRC"/. "$STAGE"/
  else
    die "missing folder $CORE_PUBLIC_SRC (static files, e.g. media/js/symja_webgl.js, in module matheclipse-core)."
  fi
}

# patch_index - inject kernel.css, kernel-config.js and kernel.js after the
# prototype.js tag in the *copied* index.html. Done with python3 (not sed) so
# the exact-once check and the line-ending handling are reliable. Config must
# come before kernel.js because kernel.js reads KERNEL_CONFIG at load time;
# kernel-worker.js is loaded by kernel.js itself, so it is not listed here.
patch_index() {
  command -v python3 >/dev/null 2>&1 || die "python3 is required to patch index.html."
  python3 -I - "$STAGE/index.html" "$PROTOTYPE_TAG" <<'PY'
import sys

path, tag = sys.argv[1], sys.argv[2]
# newline='' + surrogateescape keep the file byte-for-byte except our insertion.
with open(path, encoding='utf-8', errors='surrogateescape', newline='') as f:
    text = f.read()

count = text.count(tag)
if count != 1:
    sys.stderr.write("ERROR: expected the prototype.js script tag exactly once in "
                     "index.html, found %d: %s\n" % (count, tag))
    sys.exit(1)

end = text.index(tag) + len(tag)
line_start = text.rfind('\n', 0, text.index(tag)) + 1
# Reuse the tag's indentation (only if the tag starts its line after whitespace).
indent = text[line_start:text.index(tag)]
if indent.strip():
    indent = ''
eol = '\r\n' if '\r\n' in text else '\n'
lines = [
    '<link rel="stylesheet" type="text/css" href="/kernel.css" />',
    '<script type="text/javascript" src="/kernel-config.js"></script>',
    '<script type="text/javascript" src="/kernel.js"></script>',
]
patched = text[:end] + eol + (eol).join(indent + l for l in lines) + text[end:]
with open(path, 'w', encoding='utf-8', errors='surrogateescape', newline='') as f:
    f.write(patched)
PY
}

# copy_notebook_sources - part 2: the hand-written glue files. All four are
# mandatory, even with --mock, because the page cannot start without them.
copy_notebook_sources() {
  local f
  for f in $NOTEBOOK_FILES; do
    [ -f "$NOTEBOOK_SRC/$f" ] || die "missing $NOTEBOOK_SRC/$f (hand-written glue file in module matheclipse-web, src/main/notebook/)."
  done
  for f in $NOTEBOOK_FILES; do
    cp "$NOTEBOOK_SRC/$f" "$STAGE/$f"
  done
}

# mock_jars - --mock stand-in for part 3: empty jars/ and empty classpath.txt.
mock_jars() {
  warn "--mock: no jars. $JARS_SRC is missing or empty; created an EMPTY jars/classpath.txt."
  warn "--mock: the real kernel cannot start from this site; use ?kernel=mock."
  mkdir -p "$STAGE/jars"
  : > "$STAGE/jars/classpath.txt"
}

# copy_jars - part 3: copy each jar named in classpath.txt under a
# content-hashed name and write a classpath.txt that lists the new names in the
# original order. Order matters (Java classpath precedence), hence we iterate
# classpath.txt rather than the directory.
copy_jars() {
  local missing_msg="missing jars: $JARS_SRC/classpath.txt (run the jar build in module matheclipse-web / the kernel-packaging session that produces target/notebook/jars/)."
  if [ ! -f "$JARS_SRC/classpath.txt" ]; then
    if [ "$MOCK" -eq 1 ]; then
      mock_jars
      return 0
    fi
    die "$missing_msg"
  fi

  mkdir -p "$STAGE/jars"
  : > "$STAGE/jars/classpath.txt"

  local line name stem hash newname listed=$'\n' n=0
  while IFS= read -r line || [ -n "$line" ]; do
    # Tolerate CRLF and stray whitespace; skip blank lines.
    line="${line%$'\r'}"
    line="${line#"${line%%[![:space:]]*}"}"
    line="${line%"${line##*[![:space:]]}"}"
    [ -n "$line" ] || continue
    name="$line"
    case "$name" in
      */*) die "classpath.txt entry '$name' must be a bare jar file name." ;;
      *.jar) ;;
      *) die "classpath.txt entry '$name' does not end in .jar." ;;
    esac
    [ -f "$JARS_SRC/$name" ] || die "classpath.txt names '$name' but $JARS_SRC/$name does not exist."

    stem="${name%.jar}"
    hash="$(sha8 "$JARS_SRC/$name")"
    newname="$stem-$hash.jar"
    cp "$JARS_SRC/$name" "$STAGE/jars/$newname"
    printf '%s\n' "$newname" >> "$STAGE/jars/classpath.txt"
    listed="$listed$name"$'\n'
    n=$((n + 1))
  done < "$JARS_SRC/classpath.txt"

  # An empty classpath is only acceptable as a mock; a real kernel needs jars.
  if [ "$n" -eq 0 ]; then
    [ "$MOCK" -eq 1 ] || die "$JARS_SRC/classpath.txt lists no jars."
    warn "--mock: classpath.txt is empty."
  fi

  # Jars on disk but not on the classpath are never loaded; flag them so a
  # forgotten entry is noticed (they are deliberately not shipped).
  local jar base
  for jar in "$JARS_SRC"/*.jar; do
    [ -e "$jar" ] || continue
    base="$(basename "$jar")"
    case "$listed" in
      *$'\n'"$base"$'\n'*) ;;
      *) warn "$base is in $JARS_SRC but not in classpath.txt; not copied." ;;
    esac
  done
}

# mock_docs - --mock stand-in for part 4: a one-entry doc tree the page's
# doc viewer can load.
mock_docs() {
  warn "--mock: no docs. $DOCS_SRC is missing or empty; wrote a placeholder doc-json/index.json."
  mkdir -p "$STAGE/doc-json"
  printf '%s' '{"content":"<p>mock docs</p>","data":{}}' > "$STAGE/doc-json/index.json"
}

# copy_docs - part 4: the exported documentation JSON, copied as is.
copy_docs() {
  if [ ! -d "$DOCS_SRC" ] || is_empty_dir "$DOCS_SRC"; then
    if [ "$MOCK" -eq 1 ]; then
      mock_docs
      return 0
    fi
    die "missing or empty doc-json: $DOCS_SRC (produced by the doc export, matheclipse-web/tools/doc-export)."
  fi
  mkdir -p "$STAGE/doc-json"
  cp -R "$DOCS_SRC"/. "$STAGE/doc-json"/
}

# check_and_summarize - walk every file in the staged site: fail on files over
# the Cloudflare limit, warn on too many files, then print a summary. Sizes go
# to a temp list so we can sort for the "largest files" section.
check_and_summarize() {
  local list f size total=0 count=0 jar_count=0 jar_bytes=0 oversize=0 rel
  list="$(mktemp "${TMPDIR:-/tmp}/notebook-site-sizes.XXXXXX")"

  # -print0 so unusual file names cannot break the loop. wc -c is portable
  # between macOS and Linux (unlike stat).
  while IFS= read -r -d '' f; do
    size="$(wc -c < "$f" | tr -d ' ')"
    rel="${f#"$STAGE"/}"
    printf '%s\t%s\n' "$size" "$rel" >> "$list"
    total=$((total + size))
    count=$((count + 1))
    case "$rel" in
      jars/*.jar)
        jar_count=$((jar_count + 1))
        jar_bytes=$((jar_bytes + size))
        ;;
    esac
    if [ "$size" -gt "$MAX_BYTES" ]; then
      echo "ERROR: $rel is $size bytes, over Cloudflare's 25 MiB per-file limit ($MAX_BYTES)." >&2
      oversize=$((oversize + 1))
    fi
  done < <(find "$STAGE" -type f -print0)

  if [ "$oversize" -gt 0 ]; then
    rm -f "$list"
    die "$oversize file(s) exceed the 25 MiB per-file limit; the site was not written."
  fi
  if [ "$count" -gt "$MAX_FILES" ]; then
    warn "$count files exceeds Cloudflare's $MAX_FILES file limit; the deploy will be rejected."
  fi

  echo "Notebook site summary"
  echo "  files:        $count"
  awk -v b="$total" 'BEGIN { printf "  total size:   %.2f MB (%d bytes)\n", b / 1048576, b }'
  awk -v n="$jar_count" -v b="$jar_bytes" 'BEGIN { printf "  jars:         %d files, %.2f MB (%d bytes)\n", n, b / 1048576, b }'
  echo "  largest files:"
  # awk reads ALL of sort's output and prints only the first five: "| head -5" would close the pipe
  # early, sort would die of SIGPIPE, and under pipefail that aborted the build before the swap.
  sort -t "$(printf '\t')" -k1,1nr "$list" | awk -F '\t' 'NR <= 5 { printf "    %10d  %s\n", $1, $2 }'
  rm -f "$list"
}

# main - parse flags, resolve paths, assemble in staging, verify, swap in.
main() {
  local arg
  for arg in "$@"; do
    case "$arg" in
      --mock) MOCK=1 ;;
      -h|--help) usage; exit 0 ;;
      *) echo "ERROR: unknown argument '$arg'" >&2; usage >&2; exit 2 ;;
    esac
  done

  # Resolve the module root from the script's own location so the script runs
  # from any working directory; NOTEBOOK_MODULE_DIR exists for script tests.
  local default_root
  default_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
  ROOT="${NOTEBOOK_MODULE_DIR:-$default_root}"
  [ -d "$ROOT" ] || die "module folder $ROOT does not exist."
  ROOT="$(cd "$ROOT" && pwd)"

  PUBLIC_SRC="$ROOT/../matheclipse-io/src/main/resources/public"
  CORE_PUBLIC_SRC="$ROOT/../matheclipse-core/src/main/resources/public"
  NOTEBOOK_SRC="$ROOT/src/main/notebook"
  JARS_SRC="$ROOT/target/notebook/jars"
  DOCS_SRC="$ROOT/target/notebook/doc-json"
  OUT="$ROOT/target/notebook-site"

  # The output dir is deleted with rm -rf, so refuse anything unexpected.
  case "$OUT" in
    */target/notebook-site) ;;
    *) die "refusing to wipe unexpected output path $OUT." ;;
  esac

  mkdir -p "$ROOT/target"
  # Same filesystem as OUT so the final mv is a cheap rename.
  STAGE="$ROOT/target/.notebook-site.staging.$$"
  rm -rf "$STAGE"
  mkdir -p "$STAGE"
  trap cleanup EXIT

  copy_public
  patch_index
  copy_notebook_sources
  copy_jars
  copy_docs
  check_and_summarize

  if [ "$MOCK" -eq 1 ]; then
    warn "--mock build: this site only works with ?kernel=mock. Do not deploy it."
  fi

  # Swap in only now: every check passed, the old output (if any) is replaced.
  rm -rf "$OUT"
  mv "$STAGE" "$OUT"
  STAGE=""
  echo "Wrote $OUT"
}

main "$@"
