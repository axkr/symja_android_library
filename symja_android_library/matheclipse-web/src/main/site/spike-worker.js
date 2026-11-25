/*
 * Web Worker of the Symja CheerpJ spike.
 *
 * Chain: spike.html (page) <-> this worker <-> CheerpJ (Java VM in WebAssembly) <-> range-server.mjs
 * <-> jars. The page sends {cmd:'start', version} once and {cmd:'eval', input} later; the worker
 * answers with messages of the form {type: ...}, see post() callers below.
 *
 * Why a Web Worker: CheerpJ's library mode makes every Java call async, but loading the runtime and
 * running Symja (seconds of CPU for the first Integrate or Solve) would still block the page's main
 * thread if it ran there. In a worker the page stays responsive, and its watchdog can terminate a
 * hung worker, which it could not do to code running on its own thread.
 *
 * Every step is timed and its result posted to the page immediately, not collected into one final
 * message. If CheerpJ hangs at step N, the page (which has a watchdog) still has the results of
 * steps 1..N-1.
 */

/**
 * The default runtime loader, the CheerpJ 5.0 release. In a worker it is pulled in with
 * importScripts, not a script tag. The page may pass another one (opts.loader), for example a
 * nightly build such as https://cjrtnc.leaningtech.com/20261119_3844/loader.js, which carries
 * fixes that 5.0 lacks.
 */
const DEFAULT_LOADER = 'https://cjrtnc.leaningtech.com/5.0/loader.js';

/** The resolved SymjaCheerpJ class; null until step "classResolved" has finished. */
let C = null;

const now = () => performance.now();

/** Send one message to the page. */
function post(msg) { self.postMessage(msg); }

/**
 * Make an error structured-cloneable. Error objects do not always survive postMessage, and the
 * stack is what tells a CheerpJ failure from a Java one.
 * @param {*} e anything that was thrown
 * @returns {{message:string, stack:string}}
 */
function errInfo(e) { return { message: safeText((e && e.message) || e), stack: safeText((e && e.stack) || '') }; }

/**
 * String() that cannot throw. A Java exception which reaches JavaScript through CheerpJ is a proxy
 * object without a primitive value, and String(proxy) throws "Cannot convert object to primitive
 * value", which would replace the real error with this one.
 * @param {*} v anything
 * @returns {string}
 */
function safeText(v) {
  try { return String(v); } catch (_) { /* fall through */ }
  try { if (v && typeof v.toString === 'function') return String(v.toString()); } catch (_) { /* fall through */ }
  return Object.prototype.toString.call(v);
}

/**
 * Run one named step, time it, and report it to the page.
 * Posts {type:'stepStart', step}, then {type:'step', step, millis} on success or
 * {type:'error', step, error} on failure (the error is then rethrown to stop the run).
 * @param {string} name step name, also the key in the page's timings table
 * @param {function(): Promise<*>} fn the work
 * @returns {Promise<*>} whatever fn returns
 */
async function step(name, fn) {
  // stepStart lets the page say which step hung if no 'step' message ever follows.
  post({ type: 'stepStart', step: name });
  const t0 = now();
  try {
    const v = await fn();
    post({ type: 'step', step: name, millis: Math.round(now() - t0) });
    return v;
  } catch (e) {
    post({ type: 'error', step: name, error: errInfo(e) });
    throw e;
  }
}

/**
 * Static jar loading, for hosts which answer every request with 200 and the whole file (such as
 * Cloudflare's static assets). CheerpJ refuses such answers for /app/ files, because it reads jars
 * in Range-sized chunks. So the worker downloads each jar whole itself, with a plain GET that the
 * browser's HTTP cache can serve on the next visit, and hands the bytes to CheerpJ through /str/,
 * the filesystem whose files JavaScript creates in memory. CheerpJ then never touches the network
 * for classes. The download and the copy are separate steps so the report can tell transfer time
 * from the cost of copying ~40 MB into CheerpJ.
 * @param {string[]} jars jar file names from classpath.txt, in classpath order
 * @param {'http'|'api'} [jarCache] where a later visit finds the jars. 'http' (default): the
 *   browser's HTTP cache, which in the spike's test did not keep the 16 MB matheclipse-core jar
 *   (most likely its per-entry size limit), so every visit downloaded it again. 'api': the Cache
 *   Storage API, which this code fills and reads itself and which has no per-entry limit. Its
 *   entries never expire, so a real deployment needs jar names that change with the content.
 * @returns {Promise<string>} the classpath: /str/<name> entries joined by ':'. Flat, because
 *   /str/ has no directories: cheerpOSAddStringFile('/str/jars/x.jar') fails with "CheerpOS:
 *   Directories are not supported", and the classpath then names files that do not exist.
 */
async function loadStatic(jars, jarCache) {
  const bufs = await step('jarDownload', async () => {
    // The cache name carries a version so a format change can drop all old entries at once.
    const cache = jarCache === 'api' ? await caches.open('symja-jars-v1') : null;
    let fromCacheApi = 0;
    // All in parallel: HTTP/1.1 caps connections per origin anyway, and there is no ordering need.
    const list = await Promise.all(jars.map(async (n) => {
      const url = '/jars/' + n;
      const hit = cache && await cache.match(url);
      if (hit) { fromCacheApi++; return new Uint8Array(await hit.arrayBuffer()); }
      const r = await fetch(url);
      if (!r.ok) throw new Error(n + ' HTTP ' + r.status);
      // put() consumes a body, so the copy goes to the cache and the original is read here.
      if (cache) await cache.put(url, r.clone());
      return new Uint8Array(await r.arrayBuffer());
    }));
    post({ type: 'info', jarBytes: list.reduce((a, b) => a + b.length, 0), fromCacheApi });
    return list;
  });
  await step('jarCopy', async () => {
    for (let i = 0; i < jars.length; i++) cheerpOSAddStringFile('/str/' + jars[i], bufs[i]);
  });
  return jars.map(n => '/str/' + n).join(':');
}

/**
 * The whole spike run: steps 1-9 of the brief, in order.
 * @param {number} version Java version for CheerpJ, 21 (Hipparchus 5 needs it). It must be a number: cheerpjInit
 *   expects one, and the page converts the select's string value before sending.
 * @param {{loader?: string, jars?: 'range'|'static', jarCache?: 'http'|'api', benchmarks?: boolean, selfTests?: string[]|null}} [opts]
 *   loader: the CheerpJ loader URL (default DEFAULT_LOADER).
 *   jars: how the classpath reaches CheerpJ. 'range' (default) names /app/jars/<name>, which
 *   CheerpJ reads lazily with HTTP Range requests. 'static' downloads every jar whole and puts it
 *   into CheerpJ's in-memory /str/ filesystem first, for hosts that ignore Range (see loadStatic).
 *   jarCache: in static mode, which cache keeps the jars for the next visit (see loadStatic).
 *   benchmarks, selfTests: what to run after init.
 *   The page's first worker runs everything (the defaults). When a self test hangs, the page
 *   terminates this worker and starts a fresh one with benchmarks:false and selfTests set to the
 *   tests still to run (possibly none, which leaves just a live kernel for the Eval box): a Java
 *   call stuck inside CheerpJ cannot be interrupted, so a new JVM is the only way to go on.
 * @returns {Promise<void>} resolves after posting {type:'done'}; throws on the first failed step
 */
async function run(version, opts = {}) {
  await step('loader', async () => { importScripts(opts.loader || DEFAULT_LOADER); });
  await step('cheerpjInit', async () => { await cheerpjInit({ version }); });

  const jars = await step('classpath', async () => {
    // no-store: the list changes with every build and a stale one would name jars that are gone.
    const r = await fetch('/jars/classpath.txt', { cache: 'no-store' });
    if (!r.ok) throw new Error('classpath.txt HTTP ' + r.status);
    const names = (await r.text()).split(/\r?\n/).map(s => s.trim()).filter(Boolean);
    if (!names.length) throw new Error('classpath.txt is empty');
    return names;
  });
  post({ type: 'info', classpath: jars });

  // CheerpJ maps /app/ to the root of the page's origin, so /app/jars/x.jar is fetched as
  // GET /jars/x.jar, which is what the range server serves. The order of classpath.txt is kept:
  // the module's own jar is first so its classes win over any duplicate in a dependency.
  const cp = opts.jars === 'static' ? await loadStatic(jars, opts.jarCache)
    : jars.map(n => '/app/jars/' + n).join(':');
  const lib = await step('cheerpjRunLibrary', async () => await cheerpjRunLibrary(cp));
  // Library mode: a class is reached by walking its package path on the library object, and each
  // lookup is awaited. Static methods are then called on it, and all calls return promises.
  C = await step('classResolved', async () => await lib.org.matheclipse.web.SymjaCheerpJ);

  // The Java side returns JSON strings (strings are the one type that crosses the boundary cheaply),
  // so each result is parsed here.
  const init = await step('init', async () => JSON.parse(await C.init()));
  post({ type: 'init', value: init });

  // A restarted worker skips the benchmarks: they were measured by the first worker, and running
  // them again would only delay the remaining self tests.
  const inputs = opts.benchmarks === false ? []
    : await step('benchmarkInputs', async () => JSON.parse(await C.benchmarkInputs()));
  for (let i = 0; i < inputs.length; i++) {
    post({ type: 'stepStart', step: 'eval#' + i });
    const t0 = now();
    const r = JSON.parse(await C.eval(inputs[i]));
    // wallMillis includes the boundary crossing; r.millis (from Java) is the evaluation alone.
    post({ type: 'benchmark', index: i, wallMillis: Math.round(now() - t0), value: r });
  }

  // The page needs the full list to know which tests are left when one of them hangs.
  const names = Array.isArray(opts.selfTests) ? opts.selfTests
    : await step('selfTestNames', async () => JSON.parse(await C.selfTestNames()));
  post({ type: 'selfTestNames', names });
  for (let i = 0; i < names.length; i++) {
    post({ type: 'stepStart', step: 'selfTest:' + names[i] });
    const t0 = now();
    const r = JSON.parse(await C.selfTest(names[i]));
    post({ type: 'selfTest', name: names[i], wallMillis: Math.round(now() - t0), value: r });
  }
  post({ type: 'done' });
}

/**
 * Messages from the page.
 *   {cmd:'start', version, loader?, jars?, jarCache?, benchmarks?, selfTests?}  run the spike once (see run())
 *   {cmd:'eval', input}     evaluate one expression after a successful start; posts {type:'userEval'}
 * A failure of the run is posted as {type:'fatal'} so the page can still collect its results.
 */
self.onmessage = async (ev) => {
  const m = ev.data || {};
  if (m.cmd === 'start') {
    try { await run(m.version, { loader: m.loader, jars: m.jars, jarCache: m.jarCache, benchmarks: m.benchmarks, selfTests: m.selfTests }); }
    catch (e) { post({ type: 'fatal', error: errInfo(e) }); }
  } else if (m.cmd === 'eval') {
    post({ type: 'stepStart', step: 'userEval' });
    try {
      if (!C) throw new Error('not initialised');
      post({ type: 'userEval', value: JSON.parse(await C.eval(m.input)) });
    } catch (e) {
      // Always answer with a userEval too, so the page's watchdog for this call is cleared.
      post({ type: 'error', step: 'userEval', error: errInfo(e) });
      post({ type: 'userEval', value: { ok: false, error: String(e.message || e) } });
    }
  }
};
