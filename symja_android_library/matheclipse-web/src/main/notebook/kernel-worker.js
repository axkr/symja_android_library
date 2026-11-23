/*
 * kernel-worker.js - the Web Worker that hosts Symja's in-browser kernel (CheerpJ) for the
 * notebook site, or, in mock mode, a stand-in for it.
 *
 * How it fits the whole:
 *
 *   index.html (the notebook UI, unchanged Symja scripts)
 *      | Ajax.Request calls to /ajax/... are intercepted by kernel.js
 *   kernel.js (page)  <== postMessage, contract in notebook-site-protocol.md ==>  kernel-worker.js (this file)
 *                                                                                   | CheerpJ (Java VM in WebAssembly)
 *                                                                                 org.matheclipse.web.NotebookKernel
 *                                                                                   | Symja, from the jars in /jars/
 *
 * Why a worker: Symja evaluation takes seconds (the first Integrate costs ~4 s in CheerpJ), and
 * on the page's thread that would freeze the whole notebook. Worse, a stuck Java call inside
 * CheerpJ cannot be interrupted: the only way out is terminating the worker, which the page's
 * Stop button does, and a page cannot terminate itself. Hence the kernel lives here and a Stop
 * simply throws this worker away and starts a new one (definitions are lost, by design).
 *
 * Messages in (self.onmessage), all handled one at a time, see "Serialisation" below:
 *   {cmd:'start', mock:boolean}                  load everything, post 'ready' (once per worker)
 *   {cmd:'call', id, endpoint, params}           NotebookKernel.call(endpoint, JSON.stringify(params))
 *   {cmd:'upload', id, fileName, bytes}          NotebookKernel.upload(fileName, Int8Array)
 *   {cmd:'download', id, name}                   NotebookKernel.download(name) -> bytes|null
 *   {cmd:'reset', id}                            NotebookKernel.reset()
 * Messages out:
 *   {type:'progress', phase, text, loaded, total}   phase: loader|init|classpath|jars|java
 *   {type:'ready', initMillis, symjaVersion, fromCache, jarBytes}
 *   {type:'fatal', error:{message, stack}}           start failed; this worker is useless
 *   {type:'result', id, ok:true, body}               call, upload, reset
 *   {type:'result', id, ok:true, bytes}              download (Int8Array or null, buffer transferred)
 *   {type:'result', id, ok:false, error}             the Java call threw
 *
 * Loading follows the proven approach of the spike (src/main/site/spike-worker.js): the nightly
 * loader via importScripts, cheerpjInit({version}), every jar downloaded WHOLE and handed to
 * CheerpJ through its in-memory /str/ filesystem (the site host, Cloudflare static assets, does
 * not answer HTTP Range requests, which CheerpJ's own /app/ file access needs).
 */

importScripts('/kernel-config.js');

/** Shorthand for the shared tunables (loader URL, jar paths, cache name, ...), see kernel-config.js. */
const CFG = self.KERNEL_CONFIG;

/** The resolved NotebookKernel Java class (all static methods async); null until start finished. */
let K = null;

/** 'idle' -> 'starting' -> 'ready' | 'failed'. A worker starts once; there is no second chance. */
let state = 'idle';

/** True when started with {mock:true}: no CheerpJ, canned answers (see mockCall). */
let mockMode = false;

/** Numbers for the 'ready' message, filled while the jars load. */
let jarStats = { fromCache: 0, jarBytes: 0 };

/** Monotonic milliseconds, for progress throttling and the busy loop of the mock's Pause. */
const now = () => performance.now();

/** Send one message to the page. */
function post(msg, transfer) { self.postMessage(msg, transfer || []); }

/**
 * String() that cannot throw. A Java exception which reaches JavaScript through CheerpJ is a proxy
 * object without a primitive value, and String(proxy) throws "Cannot convert object to primitive
 * value", which would replace the real error with this one (and, inside a catch block, would
 * leave the page waiting forever for an answer that is never sent).
 * @param {*} v anything
 * @returns {string}
 */
function safeText(v) {
  try { return String(v); } catch (_) { /* fall through */ }
  try { if (v && typeof v.toString === 'function') return String(v.toString()); } catch (_) { /* fall through */ }
  return Object.prototype.toString.call(v);
}

/**
 * Describe a thrown value as plain text for the page. Prefers .message (what an Error and most
 * Java exceptions carry) and falls back to the value itself; every access is guarded because on a
 * Java proxy even reading .message may throw.
 * @param {*} e anything that was thrown
 * @returns {string}
 */
function errText(e) {
  let m;
  try { m = e && e.message; } catch (_) { /* proxy without .message */ }
  return safeText(m || e);
}

/**
 * Make an error structured-cloneable for postMessage. Error objects do not always survive it, and
 * the stack is what tells a CheerpJ failure from a Java one.
 * @param {*} e anything that was thrown
 * @returns {{message:string, stack:string}}
 */
function errInfo(e) {
  let stack = '';
  try { stack = safeText((e && e.stack) || ''); } catch (_) { /* ignore */ }
  return { message: errText(e), stack };
}

/** Resolve after ms milliseconds (non-blocking; used by the mock's fake loading). */
function sleep(ms) { return new Promise((resolve) => setTimeout(resolve, ms)); }

/**
 * Post a progress message. loaded/total are only meaningful for phase 'jars' (bytes); other
 * phases send 0/0 so the page can treat every progress message the same way.
 * @param {string} phase loader|init|classpath|jars|java
 * @param {string} text human-readable status line for the page's chip
 * @param {number} [loaded] bytes downloaded so far
 * @param {number} [total] bytes expected in total, 0 when unknown
 */
function progress(phase, text, loaded, total) {
  post({ type: 'progress', phase, text, loaded: loaded || 0, total: total || 0 });
}

/* ------------------------------------------------------------------------------------------ *
 * Serialisation
 * ------------------------------------------------------------------------------------------ */

/**
 * Tail of the promise chain through which every job runs. The page already sends one request at
 * a time, but the worker must not depend on that: two overlapping Java calls would interleave on
 * CheerpJ's single thread and could mix up their answers. Also, a 'call' that arrives while
 * start is still running simply waits behind it instead of failing.
 */
let chain = Promise.resolve();

/**
 * Append a job to the chain. The chain itself swallows failures (a failed job must not stop later
 * ones); the caller of enqueue still sees the rejection through the returned promise.
 * @param {function(): Promise<*>} job
 * @returns {Promise<*>} the job's own result
 */
function enqueue(job) {
  const p = chain.then(job);
  chain = p.catch(() => { /* reported by the job's own handler */ });
  return p;
}

/* ------------------------------------------------------------------------------------------ *
 * Jar download and Cache Storage
 * ------------------------------------------------------------------------------------------ */

/**
 * Open the jar cache, or null when Cache Storage is unavailable (insecure context, private mode
 * in some browsers, quota policy). The kernel then still works, it only downloads the jars on
 * every visit instead of once.
 * @returns {Promise<Cache|null>}
 */
async function openJarCache() {
  try {
    if (typeof caches === 'undefined') return null;
    return await caches.open(CFG.cacheName);
  } catch (_) {
    return null;
  }
}

/**
 * Delete cache entries that are not part of the current classpath. Jar names are content-hashed,
 * so every rebuild that changes a jar leaves its previous version behind; without this the
 * cache would grow by up to the size of the changed jars (16 MB for the core) per release.
 * Failure is harmless (only wasted space), so nothing here throws.
 * @param {Cache} cache
 * @param {Set<string>} keep absolute URLs of the jars in the current classpath.txt
 * @returns {Promise<void>}
 */
async function pruneJarCache(cache, keep) {
  try {
    const keys = await cache.keys();
    await Promise.all(keys.filter((req) => !keep.has(req.url)).map((req) => cache.delete(req)));
  } catch (_) { /* leave the stale entries; they cost space only */ }
}

/**
 * Read a response body fully, reporting bytes as they arrive. A streaming reader (instead of
 * arrayBuffer()) is what makes a byte progress bar for the 16 MB core jar possible.
 * @param {Response} r the fetch response
 * @param {function(number)} onBytes called with the size of each chunk
 * @returns {Promise<Uint8Array>} the whole body
 */
async function readBody(r, onBytes) {
  if (!r.body || !r.body.getReader) {
    // Very old engines have no streaming body: no progress inside the jar, but still correct.
    const whole = new Uint8Array(await r.arrayBuffer());
    onBytes(whole.length);
    return whole;
  }
  const reader = r.body.getReader();
  const chunks = [];
  let size = 0;
  for (;;) {
    const { done, value } = await reader.read();
    if (done) break;
    chunks.push(value);
    size += value.length;
    onBytes(value.length);
  }
  // One allocation of the exact size, then copy: concatenating with a Blob would take a second
  // asynchronous round trip for no gain.
  const out = new Uint8Array(size);
  let at = 0;
  for (const c of chunks) { out.set(c, at); at += c.length; }
  return out;
}

/**
 * Obtain every jar of the classpath, from Cache Storage when present, else from the network
 * (and then store it). All jars load in parallel: HTTP/2 multiplexes them, and there is no
 * ordering need since nothing is used before all are present.
 * Progress is the sum of bytes of all jars. The total is only reported once every jar's size
 * is known (Content-Length, or the cached blob's size); until then, and for a jar served with a
 * Content-Encoding (whose Content-Length counts compressed bytes, not what the reader yields),
 * the total is 0 = unknown, so the page never shows a percentage that jumps or exceeds 100%.
 * @param {string[]} names jar names from classpath.txt, in classpath order
 * @returns {Promise<Uint8Array[]>} the jar bytes, same order as names
 */
async function downloadJars(names) {
  // Absolute URLs: Cache keys are full request URLs, and pruning compares them as such.
  const urls = names.map((n) => new URL(CFG.jarBase + n, self.location.href).href);
  const cache = await openJarCache();
  if (cache) await pruneJarCache(cache, new Set(urls));

  const loaded = names.map(() => 0);
  const sizes = names.map(() => -1); // -1 = not known yet, 0 = known to be unknowable, >0 = bytes
  let fromCache = 0;
  let lastPost = 0;

  /**
   * Post the aggregate progress. Throttled to ~10 per second: a stream delivers chunks of a few
   * KB and a message per chunk would flood the page for no visible gain. `force` is used when a
   * jar finishes, so the contract's "at least once per jar" always holds.
   */
  const report = (force) => {
    const t = now();
    if (!force && t - lastPost < 100) return;
    lastPost = t;
    const undecided = sizes.some((s) => s < 0);
    const unknown = sizes.some((s) => s === 0);
    const total = (undecided || unknown) ? 0 : sizes.reduce((a, b) => a + b, 0);
    progress('jars', 'Downloading Symja', loaded.reduce((a, b) => a + b, 0), total);
  };

  const list = await Promise.all(names.map(async (name, i) => {
    let hit = null;
    try { hit = cache ? await cache.match(urls[i]) : null; } catch (_) { hit = null; }
    if (hit) {
      const buf = new Uint8Array(await hit.arrayBuffer());
      sizes[i] = buf.length;
      loaded[i] = buf.length;
      fromCache++;
      report(true);
      return buf;
    }
    const r = await fetch(urls[i]);
    if (!r.ok) throw new Error(name + ' HTTP ' + r.status);
    const len = Number(r.headers.get('content-length'));
    // See the doc comment: a compressed body makes Content-Length useless as a total.
    sizes[i] = (len > 0 && !r.headers.get('content-encoding')) ? len : 0;
    const buf = await readBody(r, (n) => { loaded[i] += n; report(false); });
    if (cache) {
      // A Response built from the bytes, not r.clone(): cloning tees the stream and would buffer
      // a second full copy while the slower consumer catches up. Failure (quota) costs only
      // the caching, the jar is already in hand.
      try { await cache.put(urls[i], new Response(buf, { headers: { 'Content-Type': 'application/java-archive' } })); }
      catch (_) { /* quota exceeded or storage disabled: run uncached */ }
    }
    report(true);
    return buf;
  }));

  jarStats = { fromCache, jarBytes: list.reduce((a, b) => a + b.length, 0) };
  return list;
}

/* ------------------------------------------------------------------------------------------ *
 * Real kernel
 * ------------------------------------------------------------------------------------------ */

/**
 * Load CheerpJ and the jars, start NotebookKernel and post 'ready'.
 * The slow, independent parts run concurrently: (loader + cheerpjInit) and (classpath.txt + all
 * jar downloads) share no state until the jars are handed to CheerpJ, and on a real link the
 * download dominates, so overlapping them hides the ~1.6 s CheerpJ takes to initialise on a cold
 * page. Progress phases therefore may interleave; the page only shows the latest text.
 * @returns {Promise<void>} resolves after posting 'ready'; throws on the first failure
 */
async function startReal() {
  const javaReady = (async () => {
    progress('loader', 'Loading the Java runtime');
    // importScripts is synchronous and blocks this worker until the loader has run, which is fine
    // here: nothing else can use the worker before it is ready.
    importScripts(CFG.loader);
    progress('init', 'Initialising the Java runtime');
    await cheerpjInit({ version: CFG.javaVersion });
  })();

  const jarsReady = (async () => {
    progress('classpath', 'Reading the jar list');
    // no-store: the list changes with every build and a stale one would name jars that are gone.
    const r = await fetch(CFG.jarBase + CFG.classpathFile, { cache: 'no-store' });
    if (!r.ok) throw new Error(CFG.classpathFile + ' HTTP ' + r.status);
    const names = (await r.text()).split(/\r?\n/).map((s) => s.trim()).filter(Boolean);
    if (!names.length) throw new Error(CFG.classpathFile + ' is empty');
    const bytes = await downloadJars(names);
    return { names, bytes };
  })();

  // Promise.all attaches a handler to both, so the loser of a failure race never becomes an
  // unhandled rejection.
  const [, jars] = await Promise.all([javaReady, jarsReady]);

  progress('java', 'Starting Symja', jarStats.jarBytes, jarStats.jarBytes);
  // /str/ is flat: a path with a directory fails with "CheerpOS: Directories are not supported"
  // and the classpath would then name files that do not exist.
  jars.names.forEach((n, i) => cheerpOSAddStringFile('/str/' + n, jars.bytes[i]));
  jars.bytes = null; // let the JS-side copies go; CheerpJ holds its own now
  const cp = jars.names.map((n) => '/str/' + n).join(':');

  const lib = await cheerpjRunLibrary(cp);
  // Library mode: a class is reached by walking its package path on the library object, and only
  // the final lookup is awaited. Its static methods are then called on it; all return promises.
  K = await CFG.kernelClass.split('.').reduce((o, p) => o[p], lib);

  // Java returns JSON strings (strings cross the boundary cheaply), parsed here.
  const init = JSON.parse(await K.start());
  if (!init || !init.ok) {
    throw new Error('NotebookKernel.start() failed: ' + safeText((init && (init.error || init.message)) || 'no details'));
  }
  post({
    type: 'ready',
    initMillis: init.initMillis,
    symjaVersion: init.symjaVersion,
    fromCache: jarStats.fromCache,
    jarBytes: jarStats.jarBytes
  });
}

/**
 * Convert what NotebookKernel.download returned into an Int8Array whose buffer may be
 * transferred. A byte[] arrives as an Int8Array, but possibly a view into a larger buffer or
 * one that cannot be transferred (a SharedArrayBuffer); transferring such a buffer would either
 * fail or hand the page the whole VM heap. A copy is cheap next to the Java work already done.
 * @param {*} r the Java result: byte[] or null
 * @returns {Int8Array|null}
 */
function toTransferableBytes(r) {
  if (r == null) return null;
  if (r instanceof Int8Array) {
    const own = r.byteOffset === 0 && r.buffer instanceof ArrayBuffer && r.buffer.byteLength === r.length;
    return own ? r : r.slice();
  }
  return Int8Array.from(r); // some other array-like (e.g. a plain Array of numbers)
}

/**
 * Run one request against the real Java kernel.
 * @param {{cmd:string, endpoint?:string, params?:Object, fileName?:string, bytes?:Int8Array, name?:string}} m
 * @returns {Promise<{body?:string, bytes?:(Int8Array|null)}>} the payload for the 'result' message
 */
async function realCall(m) {
  if (m.cmd === 'call') {
    return { body: bodyText(await K.call(m.endpoint, JSON.stringify(m.params || {}))) };
  } else if (m.cmd === 'upload') {
    return { body: bodyText(await K.upload(m.fileName, m.bytes)) };
  } else if (m.cmd === 'download') {
    return { bytes: toTransferableBytes(await K.download(m.name)) };
  }
  return { body: bodyText(await K.reset()) };
}

/**
 * A Java String result as a JS string. CheerpJ returns java.lang.String as a JS string already;
 * null (an endpoint with no body) becomes '' so the page always gets a string.
 * @param {*} v
 * @returns {string}
 */
function bodyText(v) { return v == null ? '' : String(v); }

/* ------------------------------------------------------------------------------------------ *
 * Mock kernel (no CheerpJ): lets the page, the Stop button and the status chip be developed and
 * tested in seconds, without the 40 MB of jars.
 * ------------------------------------------------------------------------------------------ */

/**
 * Assignments seen so far (`x=5` style), name -> number. Deliberately lives in this worker: Stop
 * terminates the worker, the new one starts with an empty Map, and the `Definitions` query then
 * shows what the real kernel does too - definitions are lost after Stop.
 * @type {Map<string,string>}
 */
const mockDefs = new Map();

/** Total of the pretend download, the size of the real jar set, so the bar looks like the real one. */
const MOCK_JAR_BYTES = 41700000;

/**
 * Pretend to load for ~1.5 s with progress messages for every phase, then post 'ready'.
 * @returns {Promise<void>}
 */
async function startMock() {
  const t0 = now();
  progress('loader', 'Loading the Java runtime (mock)');
  await sleep(200);
  progress('init', 'Initialising the Java runtime (mock)');
  await sleep(200);
  progress('classpath', 'Reading the jar list (mock)');
  await sleep(100);
  const steps = 8;
  for (let i = 1; i <= steps; i++) {
    await sleep(100);
    progress('jars', 'Downloading Symja (simulated)', Math.round(MOCK_JAR_BYTES * i / steps), MOCK_JAR_BYTES);
  }
  progress('java', 'Starting Symja (mock)', MOCK_JAR_BYTES, MOCK_JAR_BYTES);
  await sleep(300);
  post({
    type: 'ready',
    initMillis: Math.round(now() - t0),
    symjaVersion: 'mock',
    fromCache: 0,
    jarBytes: MOCK_JAR_BYTES
  });
}

/**
 * Block this worker for the given time. A busy loop on purpose: the real kernel blocks its
 * worker for the whole evaluation, and only a busy loop reproduces that, so the page's Stop
 * button (which must terminate the worker from outside) can be tested against it. sleep() would
 * leave the worker responsive and test nothing.
 * @param {number} seconds
 */
function busyWait(seconds) {
  const end = now() + seconds * 1000;
  while (now() < end) { /* spin */ }
}

/**
 * Escape text for use inside HTML or MathML markup.
 * @param {string} s
 * @returns {string}
 */
function escapeHtml(s) {
  return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

/**
 * Build the JSON of a query answer in the shape symja.js setResult() renders:
 * {results:[{out:[...], result, format}]}. `out` holds the side output above the result: a plain
 * print line {text, format} (rendered by createLine; format 'text' = escaped plain text) or a
 * message line {prefix, message:true, tag, symbol, text:'<math>..</math>'} (rendered as
 * "prefix: " + the MathML). `result` with format 'text' is the final value line.
 * @param {string} resultText the value shown as the result
 * @param {Object[]} [out] extra output lines
 * @returns {string} JSON text
 */
function queryJson(resultText, out) {
  return JSON.stringify({ results: [{ out: out || [], result: resultText, format: 'text' }] });
}

/**
 * Answer a `query` request the mock way.
 *  - input `Pause(n)` anywhere: busy-loop n seconds first (Stop test), then report it
 *  - input `Definitions`: list the assignments this worker remembers
 *  - input `name=123`: remember it and answer 123
 *  - input containing `Message`: also emit a sample message line (renders the message style)
 *  - anything else: echo the input
 * @param {string} input the cell text
 * @returns {string} JSON text
 */
function mockQuery(input) {
  const pause = /Pause\(\s*(\d+(?:\.\d+)?)\s*\)/.exec(input);
  if (pause) {
    busyWait(parseFloat(pause[1]));
    return queryJson('Pause done after ' + pause[1] + ' s: ' + input);
  }
  if (input.indexOf('Definitions') >= 0) {
    // Show exactly what is remembered, or say so when nothing is: after a Stop it is empty.
    const text = mockDefs.size
      ? 'remembered: ' + Array.from(mockDefs, ([k, v]) => k + '=' + v).join(', ')
      : 'no definitions remembered';
    return queryJson(text);
  }
  const assign = /^\s*(\w+)\s*=\s*(\d+)\s*$/.exec(input);
  if (assign) {
    mockDefs.set(assign[1], assign[2]);
    return queryJson(assign[2]);
  }
  const out = [];
  if (input.indexOf('Message') >= 0) {
    out.push({
      prefix: 'Mock::sample',
      message: true,
      tag: 'sample',
      symbol: 'Mock',
      text: '<math><mrow><mtext>' + escapeHtml('A sample message from the mock kernel.') + '</mtext></mrow></math>'
    });
  }
  return queryJson('mock kernel: ' + input, out);
}

/**
 * Answer a `notebook` request the mock way: GET lists nothing; POST renders each Markdown source
 * to a paragraph (no real Markdown, just enough to see the round trip).
 * @param {Object} params request params; params.markdown is a JSON array of strings
 * @returns {string} JSON text
 */
function mockNotebook(params) {
  if ((params.__method || 'GET') !== 'POST' || params.markdown == null) return '{}';
  let sources;
  try { sources = JSON.parse(params.markdown); } catch (_) { sources = [params.markdown]; }
  if (!Array.isArray(sources)) sources = [sources];
  return JSON.stringify({
    html: sources.map((s) => '<p>' + escapeHtml(s).replace(/\n/g, '<br>') + '</p>')
  });
}

/**
 * Run one request against the mock kernel; same input and output shape as realCall.
 * @param {{cmd:string, endpoint?:string, params?:Object, fileName?:string, name?:string}} m
 * @returns {Promise<{body?:string, bytes?:(Int8Array|null)}>}
 */
async function mockCall(m) {
  if (m.cmd === 'call') {
    const params = m.params || {};
    if (m.endpoint === 'query') return { body: mockQuery(String(params.query == null ? '' : params.query)) };
    if (m.endpoint === 'notebook') return { body: mockNotebook(params) };
    return { body: JSON.stringify({ error: 'mock kernel: not available' }) };
  } else if (m.cmd === 'upload') {
    return { body: JSON.stringify({ name: m.fileName }) };
  } else if (m.cmd === 'download') {
    const u8 = new TextEncoder().encode('mock download of ' + m.name);
    return { bytes: new Int8Array(u8.buffer, u8.byteOffset, u8.byteLength) };
  }
  mockDefs.clear();
  return { body: JSON.stringify({ ok: true }) };
}

/* ------------------------------------------------------------------------------------------ *
 * Message handling
 * ------------------------------------------------------------------------------------------ */

/**
 * Handle 'start': run the real or mock loader once. A failure is posted as 'fatal' and the
 * worker is marked failed so later requests are answered with an error instead of hanging.
 * @param {boolean} mock
 * @returns {Promise<void>}
 */
async function handleStart(mock) {
  if (state !== 'idle') return; // once per worker; a duplicate start must not load twice
  state = 'starting';
  mockMode = !!mock;
  try {
    if (mockMode) await startMock(); else await startReal();
    state = 'ready';
  } catch (e) {
    state = 'failed';
    post({ type: 'fatal', error: errInfo(e) });
  }
}

/**
 * Handle call / upload / download / reset: always answers with exactly one 'result' message for
 * the request's id, success or failure, so the page's queue can never stall on this worker.
 * @param {Object} m the request message (has .id)
 * @returns {Promise<void>}
 */
async function handleRequest(m) {
  if (state !== 'ready') {
    post({ type: 'result', id: m.id, ok: false, error: 'kernel is not running (state: ' + state + ')' });
    return;
  }
  try {
    const r = await (mockMode ? mockCall(m) : realCall(m));
    if (m.cmd === 'download') {
      // Transfer the buffer: no copy, and the worker has no further use for the bytes.
      post({ type: 'result', id: m.id, ok: true, bytes: r.bytes }, r.bytes ? [r.bytes.buffer] : []);
    } else {
      post({ type: 'result', id: m.id, ok: true, body: r.body });
    }
  } catch (e) {
    // errText never throws, which matters: a throw here would leave the request unanswered.
    post({ type: 'result', id: m.id, ok: false, error: errText(e) });
  }
}

/**
 * Entry point for messages from the page (see the header for the list). Every command goes
 * through the promise chain, which is what makes the processing strictly one at a time.
 * Unknown commands are ignored, except that one carrying an id is answered with an error so
 * its sender does not wait for ever.
 */
self.onmessage = (ev) => {
  const m = ev.data || {};
  if (m.cmd === 'start') {
    enqueue(() => handleStart(m.mock));
  } else if (m.cmd === 'call' || m.cmd === 'upload' || m.cmd === 'download' || m.cmd === 'reset') {
    enqueue(() => handleRequest(m));
  } else if (m.id !== undefined) {
    post({ type: 'result', id: m.id, ok: false, error: 'unknown command: ' + safeText(m.cmd) });
  }
};
