#!/usr/bin/env node
/*
 * Local dev server for the Symja CheerpJ spike.
 *
 * How the pieces fit: spike.html starts a Web Worker (spike-worker.js), the worker loads CheerpJ,
 * and CheerpJ fetches the jars from this server as GET /jars/<name> (the classpath entry
 * /app/jars/<name> maps to the page's origin root). Everything else (spike.html, the worker script)
 * is served from the site directory.
 *
 * Why not "python -m http.server": CheerpJ does not download a jar as a whole. It reads the
 * central directory first and then only the chunks it needs, using Range requests, and it will not
 * run if the server answers 200 with the full body instead of 206. So this server implements
 * single-range requests properly.
 *
 * Responses are never compressed. A Content-Range refers to byte offsets of the representation
 * sent, so a compressed partial response has offsets CheerpJ cannot interpret, and a jar is
 * already deflated anyway.
 *
 * /__stats counts requests and bytes per path. The bytes one page visit transfers are what decides
 * which hosting plan (Cloudflare Workers) the real deployment needs, so the numbers are read after
 * every run. Start with --no-store to force cold loads and see the worst case.
 *
 * --no-range imitates Cloudflare's static assets (Workers static assets and Pages), which ignore
 * Range and always answer 200 with the whole file and no Accept-Ranges. That is the hosting the
 * project wants without a Worker in front; it shows how CheerpJ's /app/ loading fails there and
 * lets the page's jars=static mode (whole-jar GETs copied into /str/) be tested against it.
 *
 * No dependencies; Node only.
 */
import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { fileURLToPath } from 'node:url';

// Both default directories are resolved from the script's location, so the server can be started
// from any working directory.
const here = path.dirname(fileURLToPath(import.meta.url));
const moduleDir = path.resolve(here, '..');
const args = process.argv.slice(2);
let port = 8787, noStore = false, noRange = false;
let siteDir = path.join(moduleDir, 'src/main/site');
let jarsDir = path.join(moduleDir, 'target/site/jars');
for (let i = 0; i < args.length; i++) {
  const a = args[i];
  if (a === '--port') port = Number(args[++i]);
  else if (a === '--no-store') noStore = true;
  else if (a === '--no-range') noRange = true;
  else if (a === '--site') siteDir = path.resolve(args[++i]);
  else if (a === '--jars') jarsDir = path.resolve(args[++i]);
  else { console.error('usage: node range-server.mjs [--port 8787] [--no-store] [--no-range] [--site <dir>] [--jars <dir>]'); process.exit(2); }
}

/** Content-Type by file extension; anything unknown is served as opaque bytes. */
const TYPES = {
  '.html': 'text/html; charset=utf-8', '.js': 'text/javascript; charset=utf-8',
  '.mjs': 'text/javascript; charset=utf-8', '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8', '.svg': 'image/svg+xml', '.png': 'image/png',
  '.txt': 'text/plain; charset=utf-8', '.jar': 'application/java-archive', '.wasm': 'application/wasm',
};

/** An empty statistics record: totals, a split into /jars and other paths, and a per-path table. */
function fresh() {
  return { total: { requests: 0, bytes: 0 }, jars: { requests: 0, bytes: 0 }, other: { requests: 0, bytes: 0 }, paths: {} };
}
let stats = fresh();

/**
 * Add one served request to the statistics.
 * @param {string} p the URL path
 * @param {number} bytes body bytes actually sent (0 for HEAD and 416)
 */
function record(p, bytes) {
  const g = p.startsWith('/jars/') ? stats.jars : stats.other;
  for (const t of [stats.total, g]) { t.requests++; t.bytes += bytes; }
  const e = (stats.paths[p] ||= { requests: 0, bytes: 0 });
  e.requests++; e.bytes += bytes;
}

/**
 * Map a URL path to a file below root.
 * @param {string} root absolute directory the path must stay inside
 * @param {string} rel URL path relative to that directory, still percent-encoded
 * @returns {string|null} absolute file path, or null if it is malformed or escapes root
 */
function resolveSafe(root, rel) {
  let decoded;
  try { decoded = decodeURIComponent(rel); } catch { return null; }
  if (decoded.includes('\0')) return null;
  // Resolve first, then compare: a prefix check on the raw string would miss ".." and %2e%2e.
  const full = path.resolve(root, '.' + path.sep + decoded);
  if (full !== root && !full.startsWith(root + path.sep)) return null;
  return full;
}

/**
 * Parse a Range header. Only a single range is supported: CheerpJ asks for one range at a time,
 * and for anything fancier a full 200 response is still a valid answer.
 * @param {string} h the Range header value
 * @param {number} size file size in bytes
 * @returns {{start:number,end:number}|'unsat'|null} the inclusive byte range, 'unsat' for a 416,
 *   or null when the header should be ignored and the whole file sent
 */
function parseRange(h, size) {
  const m = /^bytes=(.*)$/i.exec(h.trim());
  if (!m || m[1].includes(',')) return null;
  const mm = /^(\d*)-(\d*)$/.exec(m[1].trim());
  if (!mm || (mm[1] === '' && mm[2] === '')) return null;
  let start, end;
  if (mm[1] === '') {
    const n = Number(mm[2]);
    if (n === 0) return 'unsat';
    // suffix form "-n": the last n bytes (CheerpJ uses it to find the zip central directory)
    start = Math.max(0, size - n); end = size - 1;
  } else {
    start = Number(mm[1]);
    // an end past the file is clamped, as RFC 9110 requires
    end = mm[2] === '' ? size - 1 : Math.min(Number(mm[2]), size - 1);
    if (start >= size || start > end) return 'unsat';
  }
  return { start, end };
}

/** Handle one request: stats endpoint, or a static file with optional single-range support. */
const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  const pathname = url.pathname;
  const rangeHdr = req.headers.range || '';
  let status = 200, sent = 0;
  // One log line per request: method, path, range, status, bytes sent.
  const finish = () => {
    console.log(`${req.method} ${pathname} ${rangeHdr ? '[' + rangeHdr + ']' : '-'} ${status} ${sent}`);
  };
  /** Send a small complete response (errors, stats). Files are streamed further below. */
  const reply = (code, headers, body) => {
    status = code;
    res.writeHead(code, headers);
    if (req.method === 'HEAD' || body === undefined) { res.end(); } else { sent = Buffer.byteLength(body); res.end(body); }
    finish();
  };

  if (req.method !== 'GET' && req.method !== 'HEAD') return reply(405, { Allow: 'GET, HEAD' }, 'Method Not Allowed');

  // The stats endpoint itself is not counted, so polling it does not distort the numbers. The JSON
  // is built before the optional reset, so ?reset=1 returns the old values and then starts afresh.
  if (pathname === '/__stats') {
    const body = JSON.stringify(stats, null, 2);
    if (url.searchParams.get('reset') === '1') stats = fresh();
    return reply(200, { 'Content-Type': TYPES['.json'], 'Content-Length': Buffer.byteLength(body), 'Cache-Control': 'no-store' }, body);
  }

  let root, rel;
  // The jar directory is a build output (target/), the site directory is source: two roots, one URL space.
  if (pathname.startsWith('/jars/')) { root = jarsDir; rel = pathname.slice('/jars/'.length); }
  else { root = siteDir; rel = pathname === '/' ? 'index.html' : pathname.slice(1); }
  const file = resolveSafe(root, rel);
  if (!file) return reply(403, {}, 'Forbidden');

  let st;
  try { st = fs.statSync(file); } catch { return reply(404, {}, 'Not Found'); }
  if (!st.isFile()) return reply(404, {}, 'Not Found');

  const ext = path.extname(file).toLowerCase();
  // Strong validators (ETag, Last-Modified) let the browser cache and revalidate jar chunks and
  // tell a rebuilt jar from the old one: size or mtime changes, so the ETag changes, and chunks of
  // two builds are not mixed.
  const etag = '"' + crypto.createHash('sha1').update(`${st.size}-${st.mtimeMs}-${file}`).digest('hex') + '"';
  const headers = {
    'Content-Type': TYPES[ext] || 'application/octet-stream',
    // Without range support, say so the way a server that never heard of ranges does: no header.
    ...(noRange ? {} : { 'Accept-Ranges': 'bytes' }),
    'ETag': etag,
    'Last-Modified': st.mtime.toUTCString(),
    // Jars are large and immutable per build, so they may be cached; --no-store disables every cache
    // to measure cold loads. HTML and JS are always revalidated so edits show up on reload.
    'Cache-Control': noStore ? 'no-store' : (ext === '.jar' ? 'public, max-age=3600' : 'no-cache'),
  };

  // Default is the whole file (200); a valid Range turns it into a 206 with Content-Range.
  let start = 0, end = st.size - 1, code = 200;
  // --no-range: the header is ignored and the whole file is sent with 200, as Cloudflare's assets do.
  if (rangeHdr && !noRange) {
    const r = parseRange(rangeHdr, st.size);
    if (r === 'unsat') {
      // RFC 9110: a 416 must say how big the file is, so the client can retry with a valid range.
      record(pathname, 0);
      return reply(416, { ...headers, 'Content-Range': `bytes */${st.size}`, 'Content-Length': 0 });
    }
    if (r) { start = r.start; end = r.end; code = 206; headers['Content-Range'] = `bytes ${start}-${end}/${st.size}`; }
  }
  const len = st.size === 0 ? 0 : end - start + 1;
  headers['Content-Length'] = len;
  status = code;
  res.writeHead(code, headers);
  if (req.method === 'HEAD' || len === 0) { res.end(); record(pathname, 0); finish(); return; }
  // Stream instead of reading into memory: the jars are tens of megabytes.
  const stream = fs.createReadStream(file, { start, end });
  stream.on('data', (c) => { sent += c.length; });
  stream.on('error', () => res.destroy());
  // Count on 'close', not 'end', so an aborted download only counts the bytes that really went out.
  res.on('close', () => { record(pathname, sent); finish(); });
  stream.pipe(res);
});

server.listen(port, () => {
  console.log(`range-server on http://localhost:${port}/  site=${siteDir}  jars=${jarsDir}  noStore=${noStore}  noRange=${noRange}`);
});
