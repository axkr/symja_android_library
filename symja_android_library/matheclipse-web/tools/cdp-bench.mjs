#!/usr/bin/env node
/*
 * Headless-Chrome benchmark driver for the Symja CheerpJ spike.
 *
 * Why: the spike's timings measured in the Claude desktop app's browser pane depend on whether the
 * pane is displayed (when it is, the page's visibilityState flickers and everything runs ~3x
 * slower). This tool measures in a plain Chrome tab instead, which is what a real visitor gets.
 *
 * How the pieces fit: this script starts the installed Google Chrome headless with a throw-away
 * profile and the DevTools protocol (CDP) on a local port, opens one page target, and for every
 * run navigates it to spike.html (or /spike on the deployed site) with ?auto=1. The page starts a
 * Web Worker which loads CheerpJ and runs the benchmark inputs; this script polls the page's log
 * through Runtime.evaluate until the 11 benchmark results are there, records them, and navigates
 * to about:blank, which ends the worker before the self tests (5.0 hangs in arraysEqualsDouble).
 *
 * Before every run it samples `top`, because other processes (builds) share the machine and the
 * numbers must be read against that load. Runs rotate the loader order per round so drift affects
 * every configuration alike. One warm-up run per loader fills the HTTP cache (CheerpJ runtime) and
 * Cache Storage (jars, with jarcache=api), so measured runs are warm.
 *
 * No dependencies: Node 22+ has fetch and a global WebSocket.
 *
 * usage: node cdp-bench.mjs --profile <throw-away dir> [--port 9333] [--rounds 3] [--warmup 1]
 *          [--url <spike URL without loader>] [--loaders 5.0,20261119_3844] [--out results.json]
 *          [--chrome <path to Chrome binary>]
 */
import { spawn, execFileSync } from 'node:child_process';
import fs from 'node:fs';

// ---- arguments ---------------------------------------------------------------------------------
const opt = {
  chrome: '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
  port: 9333, rounds: 3, warmup: 1, profile: null, out: null,
  url: 'https://symja-web.duytranle-it.workers.dev/spike?version=17&auto=1&jars=static&jarcache=api',
  loaders: ['5.0', '20261119_3844'],
};
const argv = process.argv.slice(2);
for (let i = 0; i < argv.length; i++) {
  const a = argv[i], v = () => argv[++i];
  if (a === '--chrome') opt.chrome = v();
  else if (a === '--port') opt.port = Number(v());
  else if (a === '--rounds') opt.rounds = Number(v());
  else if (a === '--warmup') opt.warmup = Number(v());
  else if (a === '--profile') opt.profile = v();
  else if (a === '--url') opt.url = v();
  else if (a === '--loaders') opt.loaders = v().split(',');
  else if (a === '--out') opt.out = v();
  else { console.error('unknown argument ' + a); process.exit(2); }
}
// The profile is required, never defaulted: running against the user's own profile would mix
// their cache and extensions into the measurement (and touch their data).
if (!opt.profile) { console.error('--profile <throw-away dir> is required'); process.exit(2); }

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// ---- CDP client --------------------------------------------------------------------------------
/**
 * Minimal DevTools protocol client over one WebSocket: send() returns a promise for the response
 * with the same id. Events are ignored; this tool only needs request/response calls.
 */
class Cdp {
  /** @param {string} wsUrl the target's webSocketDebuggerUrl */
  constructor(wsUrl) {
    this.ws = new WebSocket(wsUrl);
    this.id = 0;
    this.pending = new Map();
    this.ws.onmessage = (ev) => {
      const m = JSON.parse(ev.data);
      if (m.id && this.pending.has(m.id)) {
        const { resolve, reject } = this.pending.get(m.id);
        this.pending.delete(m.id);
        m.error ? reject(new Error(m.error.message)) : resolve(m.result);
      }
    };
    this.ready = new Promise((res, rej) => { this.ws.onopen = res; this.ws.onerror = rej; });
  }
  /**
   * Call one CDP method.
   * @param {string} method e.g. 'Page.navigate'
   * @param {object} [params]
   * @returns {Promise<object>} the result object
   */
  send(method, params = {}) {
    const id = ++this.id;
    this.ws.send(JSON.stringify({ id, method, params }));
    return new Promise((resolve, reject) => this.pending.set(id, { resolve, reject }));
  }
  /** Close the socket. */
  close() { this.ws.close(); }
}

// ---- page-side probe ---------------------------------------------------------------------------
/**
 * Evaluated in the page on every poll. It reads the spike page's log (window.spikeResults is
 * only published after the self tests, which this tool skips), and counts visibilitychange
 * events from the first poll on, since a flickering visibility is what slowed the pane down.
 */
const PROBE = `(() => {
  if (!window.__bench) {
    window.__bench = { flips: 0, states: new Set() };
    document.addEventListener('visibilitychange', () => window.__bench.flips++);
  }
  window.__bench.states.add(document.visibilityState);
  const el = document.getElementById('log');
  if (!el) return { ready: false };
  const lines = el.innerText.split('\\n');
  const tim = {};
  for (const l of lines) { const m = /\\] (\\w+) done in (\\d+) ms/.exec(l); if (m) tim[m[1]] = +m[2]; }
  const bench = lines.filter((l) => /\\] eval#\\d+ /.test(l)).map((l) => {
    try { const j = JSON.parse(l.slice(l.indexOf('{'))); return j.ok ? j.millis : 'ERR'; } catch (e) { return '?'; }
  });
  const init = lines.find((l) => /init: /.test(l)) || '';
  const jv = (init.match(/javaVersion":"([^"]+)/) || [])[1] || null;
  const cache = (lines.find((l) => /from Cache Storage/.test(l)) || '').replace(/^.*from Cache Storage: /, '');
  const errors = lines.filter((l) => /ERROR|HUNG/.test(l)).slice(0, 3);
  return { ready: true, tim, bench, jv, cache, errors, vis: document.visibilityState,
           states: [...window.__bench.states], flips: window.__bench.flips };
})()`;

// ---- helpers -----------------------------------------------------------------------------------
/**
 * The 5 busiest processes, from the second of two 1-second top samples (the first sample of
 * `top -l` has no CPU deltas and shows 0% for everything).
 * @returns {string} e.g. "java 299.2; Stats 67.3; ..."
 */
function topSample() {
  try {
    const out = execFileSync('top', ['-l', '2', '-s', '1', '-o', 'cpu', '-n', '5', '-stats', 'command,cpu'], { encoding: 'utf8' });
    const blocks = out.split(/^COMMAND.*$/m);
    return blocks[blocks.length - 1].trim().split('\n').slice(0, 5)
      .map((l) => l.trim().replace(/\s+(\S+)$/, ' $1')).join('; ');
  } catch (e) { return 'top failed: ' + e.message; }
}

/** Median of numbers. */
function median(v) { const s = [...v].sort((a, b) => a - b); const n = s.length; return n % 2 ? s[(n - 1) / 2] : (s[n / 2 - 1] + s[n / 2]) / 2; }

/**
 * One run: navigate, poll until all benchmarks are in (or an error, or 180 s), then leave the
 * page so its worker stops before the self tests.
 * @returns {Promise<object>} the probe's last answer plus wall time and the loader
 */
async function runOnce(cdp, loader, label) {
  const load = topSample();
  const url = opt.url + '&loader=' + encodeURIComponent(loader);
  const t0 = Date.now();
  await cdp.send('Page.navigate', { url });
  let r = { ready: false };
  while (Date.now() - t0 < 180000) {
    await sleep(1000);
    try {
      const res = await cdp.send('Runtime.evaluate', { expression: PROBE, returnByValue: true });
      r = res.result.value || r;
    } catch (e) { /* the page may be between documents; poll again */ }
    if (r.ready && (r.bench.length >= 11 || r.errors.length)) break;
  }
  await cdp.send('Page.navigate', { url: 'about:blank' });
  const rec = { label, loader, load, wallSeconds: (Date.now() - t0) / 1000, ...r };
  console.log(JSON.stringify(rec));
  return rec;
}

// ---- main --------------------------------------------------------------------------------------
fs.mkdirSync(opt.profile, { recursive: true });
// --headless=new is the real browser engine without a window; the throw-away profile and
// --disable-extensions keep the user's settings, cache and extensions out of the measurement.
const chrome = spawn(opt.chrome, [
  '--headless=new', `--user-data-dir=${opt.profile}`, `--remote-debugging-port=${opt.port}`,
  '--no-first-run', '--no-default-browser-check', '--disable-extensions', 'about:blank',
], { stdio: 'ignore' });
let exitCode = 0;
try {
  // Wait until the DevTools endpoint answers.
  let version = null;
  for (let i = 0; i < 50 && !version; i++) {
    await sleep(200);
    try { version = await (await fetch(`http://127.0.0.1:${opt.port}/json/version`)).json(); } catch (e) { /* not up yet */ }
  }
  if (!version) throw new Error('Chrome DevTools endpoint did not come up');
  console.log(JSON.stringify({ browser: version.Browser, userAgent: version['User-Agent'] }));
  // A dedicated page target; PUT is required for /json/new by current Chrome versions.
  const target = await (await fetch(`http://127.0.0.1:${opt.port}/json/new?about:blank`, { method: 'PUT' })).json();
  const cdp = new Cdp(target.webSocketDebuggerUrl);
  await cdp.ready;
  await cdp.send('Page.enable');
  await cdp.send('Runtime.enable');

  for (let w = 0; w < opt.warmup; w++) for (const l of opt.loaders) await runOnce(cdp, l, 'warmup');
  const runs = [];
  for (let r = 0; r < opt.rounds; r++) {
    // rotate the order per round, so drift and background load hit every loader alike
    const order = opt.loaders.map((_, i) => opt.loaders[(i + r) % opt.loaders.length]);
    for (const l of order) runs.push(await runOnce(cdp, l, 'round' + (r + 1)));
  }
  cdp.close();

  // Summary: median (min-max) per loader for runLibrary, init and each benchmark.
  const names = ['1+1', 'D', 'Int x^2Sin', 'Int 1/(x^3+1)', 'Factor', 'Solve', 'Expand', 'N(Pi,200)', 'Eigen', 'FactorInt', 'Plot SVG'];
  console.log('\n| metric | ' + opt.loaders.join(' | ') + ' |\n|---|' + opt.loaders.map(() => '---|').join(''));
  const rows = [['runLibrary', (x) => x.tim.cheerpjRunLibrary], ['init', (x) => x.tim.init],
    ...names.map((n, i) => [n, (x) => x.bench[i]])];
  for (const [name, get] of rows) {
    const cells = opt.loaders.map((l) => {
      const v = runs.filter((x) => x.loader === l && x.ready).map(get).filter((n) => typeof n === 'number');
      return v.length ? `${median(v)} (${Math.min(...v)}–${Math.max(...v)})` : '-';
    });
    console.log(`| ${name} | ${cells.join(' | ')} |`);
  }
  if (opt.out) fs.writeFileSync(opt.out, JSON.stringify(runs, null, 2));
} catch (e) {
  console.error('FAILED: ' + e.message);
  exitCode = 1;
} finally {
  // Always stop the browser, even after a failure, so no headless Chrome is left behind.
  chrome.kill('SIGTERM');
  await sleep(1500);
  if (chrome.exitCode === null) chrome.kill('SIGKILL');
}
process.exit(exitCode);
