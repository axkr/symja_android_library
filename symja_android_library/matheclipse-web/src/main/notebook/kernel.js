/*
 * kernel.js - the page side of Symja's in-browser notebook kernel.
 *
 * WHAT THIS IS
 * The notebook page (matheclipse-io/src/main/resources/public/index.html and its media/js/*.js) was
 * written against Java servlets: it talks to them with `new Ajax.Request('/ajax/...')`
 * (prototype.js), with raw XMLHttpRequest uploads to /ajax/upload/, and it shows download links
 * to /ajax/download/?name=. In the static site there is no servlet. The kernel is Symja running
 * in a Web Worker (kernel-worker.js, via CheerpJ) and the documentation is a folder of JSON files
 * (/doc-json/). This script sits in between so that the page itself runs UNCHANGED:
 *
 *      page scripts --Ajax.Request('/ajax/query/')--> [kernel.js] --postMessage--> kernel-worker.js
 *                   <---------- same callbacks ------            <--- result -----
 *
 * HOW IT FITS
 * It is loaded right after prototype.js and before the page's own scripts, so everything below
 * patches at load time and uses only Prototype APIs that already exist then (Ajax, Object,
 * String#evalJSON). The contract with the worker (message names and shapes) is
 * notebook-site-protocol.md; the build puts this file, kernel.css, kernel-config.js and
 * kernel-worker.js at the site root.
 *
 * WHAT IT DOES
 *  1. Ajax.Request.prototype.request is wrapped. A request whose URL is /ajax/... on this origin
 *     is answered here; anything else goes to the original implementation untouched. The answer
 *     is not delivered by hand-written callback calls: a fake transport (readyState 4, status,
 *     responseText, headers) is installed and Prototype's own respondToReadyState(4) is run, so
 *     Ajax.Response, onSuccess/onFailure/on<status>/onComplete, Ajax.Responders and
 *     Ajax.activeRequestCount behave exactly as with a real XMLHttpRequest.
 *  2. Routing of /ajax/<name>/ :
 *       query | notebook | manipulate | dynamic | tracedialog   -> the worker ("call"), queued
 *       doc/<path>, doc/search/?query=                          -> /doc-json/*.json (never queued)
 *       login | logout | save | open | getworksheets            -> "not available" stubs (never queued)
 *       anything else                                           -> 404 (so onFailure runs)
 *  3. window.XMLHttpRequest is replaced by a subclass of the native one. Only POSTs to
 *     /ajax/upload/ are diverted (the FormData's file goes to the worker); every other XHR is
 *     the native implementation, byte for byte.
 *  4. A capturing click listener turns <a href="/ajax/download/?name=..."> into a Blob download
 *     of the bytes the worker reads from the kernel's virtual file system.
 *  5. A status chip in #headerright (loading / ready / busy / restarted / error, progress bar,
 *     Stop button) and a fixed "Built with CheerpJ" credit (a CheerpJ licence condition).
 *
 * WHY A QUEUE, WHY STOP KILLS THE WORKER
 * The kernel is one Java thread in one worker: it cannot do two things at once, and it cannot be
 * interrupted from outside (a long Integrate() just keeps the worker busy). So requests are sent
 * one at a time, and "Stop" is Worker.terminate() plus a fresh worker. That loses every
 * definition the user made - the answer to the interrupted cell says so in words.
 *
 * DEBUG HOOKS  window.symjaKernel : state, queueLength, inFlight, ready, mock, restarts,
 *   lastError, info, stop(), restart(), onceReady(cb), call(endpoint, params) -> Promise<string>.
 *   `?kernel=mock` on the page URL starts the worker in mock mode (no CheerpJ at all).
 *
 * ASSUMPTIONS TO CHECK IN THE INTEGRATION TEST
 *  - /doc-json/search-index.json looks like the README next to it describes (names, pageNames,
 *    singles, oneCharFallbacks, template). Other plausible shapes are tolerated, see normalizeIndex.
 *  - The worker answers `call`/`upload` with {type:'result', id, ok, body|error} and `download`
 *    with {ok:true, bytes: Int8Array|null}, one at a time, in the order sent.
 *  - Asynchronous requests only: asynchronous:false is answered asynchronously too (the page
 *    never asks for it).
 */
(function () {
  'use strict';

  // A second inclusion (or a page that already has a kernel) must not double-patch everything.
  if (window.symjaKernel) {
    return;
  }

  // ===========================================================================================
  // Constants
  // ===========================================================================================

  /** Where the worker script lives (site root, see the protocol). */
  var WORKER_URL = '/kernel-worker.js';

  /** The stylesheet of the chip and the credit; linked from here so index.html needs no change. */
  var CSS_URL = '/kernel.css';

  /** Used when kernel-config.js did not load (or loads later than this script). */
  var DEFAULT_DOC_BASE = '/doc-json/';

  /** The credit CheerpJ's licence asks for. */
  var CREDIT_URL = 'https://cheerpj.com';

  /** Body of every "this needs a server" answer. */
  var UNAVAILABLE_MESSAGE = 'Not available in the static version.';

  /** What the user reads in the cell of an evaluation that Stop interrupted. */
  var STOPPED_MESSAGE = 'Evaluation stopped. The kernel was restarted and all definitions are lost.';

  /** The documentation servlet's "no such page / no match" content (copied from the export). */
  var DEFAULT_NOTHING_FOUND =
      "<p>Insert a keyword and append a '*' to search for keywords. Example: <b>Int*</b>.</p>";

  /** Fallback list markup for search results, same shape as search-index.json's `template`. */
  var DEFAULT_TEMPLATE = {
    listPrefix: '<div id="docContent">\n<p>',
    item: '<a href="javascript:loadDoc(\'/functions/{name}\')">{name}</a>',
    separator: ', ',
    listSuffix: '</p>\n\n</div>',
    nothingFound: DEFAULT_NOTHING_FOUND
  };

  /** The endpoints the Java kernel serves; the worker's `call` accepts exactly these. */
  var KERNEL_ENDPOINTS = {
    query: true, notebook: true, manipulate: true, dynamic: true, tracedialog: true
  };

  /** Reason phrases for the status codes this script produces (XHR has no way to look them up). */
  var STATUS_TEXT = {
    200: 'OK', 404: 'Not Found', 500: 'Internal Server Error', 501: 'Not Implemented',
    503: 'Service Unavailable'
  };

  /** Own-property test that also works on objects without a prototype. */
  var hasOwn = Object.prototype.hasOwnProperty;

  // ===========================================================================================
  // Small helpers
  // ===========================================================================================

  /**
   * Run a function on a fresh task. Local answers are always asynchronous, like a real XHR: the
   * page's code is written as "start request, continue in onSuccess", and calling onSuccess
   * before Ajax.Request has even returned would break code that assigns its result afterwards.
   * @param {Function} fn what to run
   */
  function defer(fn) {
    window.setTimeout(fn, 0);
  }

  /**
   * Escape text for HTML. Used for error text that ends up in a doc page's `content`.
   * @param {*} text anything printable
   * @returns {string} HTML-safe text
   */
  function escapeHtml(text) {
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
  }

  /**
   * Message of anything thrown, without assuming it is an Error.
   * @param {*} e a caught value
   * @returns {string} its message
   */
  function messageOf(e) {
    return e && e.message ? String(e.message) : String(e);
  }

  /**
   * The current kernel-config.js settings, read lazily: this script may run before that one.
   * @returns {Object} KERNEL_CONFIG or an empty object
   */
  function config() {
    return window.KERNEL_CONFIG || {};
  }

  /**
   * Base URL of the exported documentation, always with a trailing slash.
   * @returns {string} e.g. '/doc-json/'
   */
  function docBase() {
    var base = config().docBase || DEFAULT_DOC_BASE;
    return base.charAt(base.length - 1) === '/' ? base : base + '/';
  }

  /**
   * Split a URL into the part after /ajax/ and its query string, if it is a same-origin /ajax/ URL.
   * Relative URLs resolve against the page, absolute ones must have the page's origin: a request
   * for another host that merely contains "/ajax/" is none of our business.
   * @param {string} url what was given to Ajax.Request / XMLHttpRequest.open
   * @returns {?{name: string, rest: string, search: string}} name = first segment ('query'),
   *   rest = the remaining path including its leading slash ('' or '/functions/Sin'), search = '?a=b'
   */
  function parseAjaxUrl(url) {
    var u;
    try {
      u = new URL(String(url), window.location.href);
    } catch (e) {
      return null;
    }
    if (u.origin !== window.location.origin || u.pathname.indexOf('/ajax/') !== 0) {
      return null;
    }
    var path = u.pathname.substring('/ajax/'.length);
    var slash = path.indexOf('/');
    var name = slash < 0 ? path : path.substring(0, slash);
    return {name: name, rest: slash < 0 ? '' : path.substring(slash), search: u.search};
  }

  /**
   * Copy a query string's pairs into `target`, without overwriting what is already there (the
   * servlet's getParameter returns the first value, and body parameters win over the URL's).
   * Everything stays a string, which is what the worker's `call` takes.
   * @param {Object} target plain object receiving name -> string
   * @param {string} query a query string with or without the leading '?'
   */
  function addQuery(target, query) {
    if (!query) {
      return;
    }
    new URLSearchParams(query).forEach(function (value, key) {
      if (key !== '__proto__' && !hasOwn.call(target, key)) {
        target[key] = value;
      }
    });
  }

  // ===========================================================================================
  // Kernel connection: the worker, the queue, the state machine
  // ===========================================================================================

  /** True when ?kernel=mock is on the page URL: the worker then fakes the kernel (no CheerpJ). */
  var mockMode = false;
  try {
    mockMode = new URLSearchParams(window.location.search).get('kernel') === 'mock';
  } catch (e) {
    mockMode = false;
  }

  /** 'loading' | 'ready' | 'busy' | 'restarting' (new worker after Stop) | 'error' (fatal). */
  var state = 'loading';

  var worker = null;          // the live Worker, or null (error state)
  var workerReady = false;    // the live worker has posted `ready`
  var queue = [];             // jobs waiting for the kernel, oldest first
  var inflight = null;        // the job the kernel is working on, or null
  var nextId = 1;             // request ids, unique over the page's lifetime (also across workers)
  var restarts = 0;           // how many times Stop/restart replaced the worker
  var lastError = '';         // message of the last fatal error
  var readyInfo = null;       // the worker's last `ready` message
  var busySince = 0;          // Date.now() when the in-flight job was sent
  var progress = {text: '', fraction: null}; // latest `progress` message, for the chip
  var readyCallbacks = [];    // onceReady() waiters

  /**
   * Change state and refresh the chip.
   * @param {string} next the new state
   */
  function setState(next) {
    state = next;
    renderChip();
  }

  /**
   * Start a fresh worker and tell it to load the kernel. Failures to even create the worker (file://
   * origins, blocked workers) end in the error state instead of an uncaught exception.
   */
  function startWorker() {
    workerReady = false;
    readyInfo = null;
    progress = {text: '', fraction: null};
    var w;
    try {
      w = new Worker(WORKER_URL);
    } catch (e) {
      fatal('Cannot start the kernel worker: ' + messageOf(e));
      return;
    }
    worker = w;

    // Every handler compares against `worker`: after Stop the old worker may still deliver a
    // message that was already in flight, and it must not be taken for the new worker's.
    w.onmessage = function (event) {
      if (w === worker) {
        handleWorkerMessage(event.data || {});
      }
    };
    w.onerror = function (event) {
      if (w !== worker) {
        return;
      }
      // preventDefault keeps the browser from also logging the error to the console: the chip
      // already shows it, and a missing worker script is an expected deployment problem.
      if (event && event.preventDefault) {
        event.preventDefault();
      }
      var detail = event && event.message ? event.message : 'the script failed to load';
      fatal('Kernel worker failed: ' + detail + ' (' + WORKER_URL + ')');
    };
    w.onmessageerror = function () {
      if (w === worker) {
        fatal('Kernel worker sent a message the page cannot read.');
      }
    };

    setState(restarts > 0 ? 'restarting' : 'loading');
    try {
      w.postMessage({cmd: 'start', mock: mockMode});
    } catch (e) {
      fatal('Cannot talk to the kernel worker: ' + messageOf(e));
    }
  }

  /**
   * Stop using the current worker for good.
   */
  function killWorker() {
    var w = worker;
    worker = null;
    workerReady = false;
    if (w) {
      // clear handlers first so nothing it still emits can reach us
      w.onmessage = w.onerror = w.onmessageerror = null;
      try {
        w.terminate();
      } catch (e) {
        // a worker that cannot be terminated is already gone
      }
    }
  }

  /**
   * The kernel cannot be used (start failed, worker crashed). Everything waiting is answered as
   * "unavailable" instead of hanging forever; later requests are answered the same way until the
   * user presses Retry (restart()).
   * @param {string} message what went wrong, shown in the chip and in the answers
   */
  function fatal(message) {
    killWorker();
    lastError = message;
    var failed = queue;
    queue = [];
    var running = inflight;
    inflight = null;
    setState('error');
    if (running) {
      deliver(running, {status: 'unavailable', error: message});
    }
    failed.forEach(function (job) {
      deliver(job, {status: 'unavailable', error: message});
    });
  }

  /**
   * React to a message of the current worker (protocol: progress | ready | fatal | result).
   * @param {Object} msg the message data
   */
  function handleWorkerMessage(msg) {
    switch (msg.type) {
      case 'progress':
        if (!workerReady) {
          var fraction = msg.total > 0 ? Math.min(1, msg.loaded / msg.total) : null;
          progress = {text: msg.text || msg.phase || '', fraction: fraction};
          renderChip();
        }
        break;
      case 'ready':
        workerReady = true;
        readyInfo = msg;
        setState('ready');
        var waiting = readyCallbacks;
        readyCallbacks = [];
        waiting.forEach(function (cb) {
          try {
            cb();
          } catch (e) {
            // one faulty waiter must not stop the others or the queue
          }
        });
        pump();
        break;
      case 'fatal':
        fatal('Kernel failed to start: ' + ((msg.error && msg.error.message) || 'unknown error'));
        break;
      case 'result':
        if (inflight && inflight.id === msg.id) {
          var job = inflight;
          inflight = null;
          setState('ready');
          if (msg.ok) {
            deliver(job, {status: 'ok', body: msg.body, bytes: msg.bytes});
          } else {
            deliver(job, {status: 'error', error: msg.error || 'The kernel call failed.'});
          }
          pump();
        }
        break;
      default:
        break;
    }
  }

  /**
   * Hand a job its outcome. Exceptions of the callback are swallowed here so one bad handler
   * cannot wedge the queue (the Prototype path catches them itself; this protects the rest).
   * @param {Object} job a queued job
   * @param {Object} outcome {status:'ok', body, bytes} | {status:'error', error} |
   *   {status:'stopped'} | {status:'unavailable', error}
   */
  function deliver(job, outcome) {
    try {
      job.done(outcome);
    } catch (e) {
      // nothing sensible to do: the requester's own code failed
    }
  }

  /**
   * Send the next queued job if the kernel is up and idle. Strictly one at a time: the real
   * kernel is single threaded, and answers are matched by order of sending.
   */
  function pump() {
    if (!workerReady || inflight || queue.length === 0) {
      return;
    }
    var job = queue.shift();
    job.id = nextId++;
    inflight = job;
    busySince = Date.now();
    setState('busy');
    var packet = job.build(job.id);
    try {
      worker.postMessage(packet.message, packet.transfer || []);
    } catch (e) {
      inflight = null;
      setState('ready');
      deliver(job, {status: 'error', error: 'Cannot send the request to the kernel: ' + messageOf(e)});
      pump();
    }
  }

  /**
   * Queue a job for the kernel (or fail it at once if the kernel is dead).
   * @param {{build: function(number): {message: Object, transfer: ?Array}, done: function(Object)}} job
   */
  function enqueue(job) {
    if (state === 'error') {
      defer(function () {
        deliver(job, {status: 'unavailable', error: lastError});
      });
      return;
    }
    queue.push(job);
    renderChip();   // the "n queued" count
    pump();
  }

  /**
   * Replace the worker with a fresh one. The in-flight job is answered as "stopped"; queued jobs
   * stay and are sent once the new kernel is ready. All kernel state (definitions, sessions) is
   * gone, which is exactly what the user is told.
   */
  function restart() {
    var interrupted = inflight;
    inflight = null;
    killWorker();
    restarts++;
    lastError = '';
    startWorker();
    if (interrupted) {
      deliver(interrupted, {status: 'stopped'});
    }
  }

  /**
   * Stop whatever the kernel is doing (the Stop button). Does nothing when it is idle.
   * @returns {boolean} true if the worker was replaced
   */
  function stop() {
    if (state === 'busy' || state === 'loading' || state === 'restarting') {
      restart();
      return true;
    }
    return false;
  }

  /**
   * Run `cb` once the kernel is ready (immediately, but asynchronously, if it already is).
   * @param {Function} cb called with no arguments
   */
  function onceReady(cb) {
    if (workerReady) {
      defer(cb);
    } else {
      readyCallbacks.push(cb);
    }
  }

  // ----- the three kinds of job the protocol knows -----

  /**
   * Job: a servlet-style call (`call` message).
   * @param {string} endpoint query|notebook|manipulate|dynamic|tracedialog
   * @param {Object} params strings only
   * @param {Function} done receives the outcome
   * @returns {Object} the job
   */
  function callJob(endpoint, params, done) {
    return {
      kind: 'call',
      endpoint: endpoint,
      done: done,
      build: function (id) {
        return {message: {cmd: 'call', id: id, endpoint: endpoint, params: params}};
      }
    };
  }

  /**
   * Job: put a file into the kernel's virtual file system (`upload` message).
   * @param {string} fileName name the kernel stores it under
   * @param {Int8Array} bytes content; its buffer is transferred, not copied
   * @param {Function} done receives the outcome
   * @returns {Object} the job
   */
  function uploadJob(fileName, bytes, done) {
    return {
      kind: 'upload',
      done: done,
      build: function (id) {
        return {
          message: {cmd: 'upload', id: id, fileName: fileName, bytes: bytes},
          transfer: [bytes.buffer]
        };
      }
    };
  }

  /**
   * Job: read a file out of the kernel's virtual file system (`download` message).
   * @param {string} name file name in the kernel's directory
   * @param {Function} done receives the outcome (bytes is null for "no such file")
   * @returns {Object} the job
   */
  function downloadJob(name, done) {
    return {
      kind: 'download',
      done: done,
      build: function (id) {
        return {message: {cmd: 'download', id: id, name: name}};
      }
    };
  }

  // ===========================================================================================
  // Turning outcomes into HTTP-shaped answers
  // ===========================================================================================

  /**
   * A query result that shows one message instead of a value. This is the shape setResult()
   * (symja.js) renders: results[].out[] with message:true prints "<prefix>: <text>". format
   * 'text' keeps the sentence as plain text (no MathML needed).
   * @param {string} tag message tag, e.g. 'stopped'
   * @param {string} text what the user reads
   * @returns {string} JSON body
   */
  function queryMessageBody(tag, text) {
    return JSON.stringify({
      results: [{
        out: [{prefix: 'Kernel::' + tag, message: true, tag: tag, symbol: 'Kernel',
          text: text, format: 'text'}]
      }]
    });
  }

  /**
   * JSON error body used for every non-query failure.
   * @param {string} message the error
   * @returns {string} JSON body
   */
  function errorBody(message) {
    return JSON.stringify({error: message});
  }

  /**
   * Map a job's outcome to the HTTP answer the servlet would have given.
   * - ok: 200 with the worker's body unchanged.
   * - Java threw: 500 (the page's onFailure shows its generic "an error occurred").
   * - stopped / unavailable: for `query` a normal 200 result that carries a visible message (so
   *   the cell explains what happened); for the other endpoints a 500 failure. Never status 0:
   *   Prototype counts 0 as success.
   * @param {string} endpoint the kernel endpoint that was called
   * @param {Object} outcome see deliver()
   * @returns {{status: number, body: string}} answer
   */
  function httpFor(endpoint, outcome) {
    switch (outcome.status) {
      case 'ok':
        return {status: 200, body: outcome.body == null ? '' : String(outcome.body)};
      case 'stopped':
        return endpoint === 'query'
            ? {status: 200, body: queryMessageBody('stopped', STOPPED_MESSAGE)}
            : {status: 500, body: errorBody(STOPPED_MESSAGE)};
      case 'unavailable':
        var why = 'The kernel is not available: ' + outcome.error;
        return endpoint === 'query'
            ? {status: 200, body: queryMessageBody('unavailable', why)}
            : {status: 503, body: errorBody(why)};
      default:
        return {status: 500, body: errorBody(outcome.error || 'The kernel call failed.')};
    }
  }

  // ===========================================================================================
  // Documentation: /ajax/doc/... answered from /doc-json/
  // ===========================================================================================

  var indexPromise = null;    // cached promise of the normalized search index (or null if missing)

  /**
   * Encode a relative path for a URL, segment by segment ('$Assumptions' -> '%24Assumptions').
   * @param {string} path e.g. 'functions/Sin'
   * @returns {string} encoded path
   */
  function encodePath(path) {
    return path.split('/').map(encodeURIComponent).join('/');
  }

  /**
   * Normalize a path found in an index entry to the form fetchDocJson wants (no leading slash,
   * no '.json'), so exports that store full file names work too.
   * @param {string} path e.g. '/functions/Sin.json'
   * @returns {string} e.g. 'functions/Sin'
   */
  function cleanRel(path) {
    return String(path).replace(/^\/+/, '').replace(/\.json$/i, '');
  }

  /**
   * Fetch one exported JSON page. Anything that is not a JSON object with a string `content`
   * counts as "no such page": a static host answering a missing file with its own index.html
   * (status 200) must not be mistaken for documentation.
   * @param {string} rel path below docBase without extension, e.g. 'functions/Sin'
   * @returns {Promise<?Object>} the parsed page, or null
   */
  function fetchDocJson(rel) {
    return fetch(docBase() + encodePath(rel) + '.json')
        .then(function (res) {
          return res.ok ? res.text() : null;
        })
        .then(function (text) {
          if (text == null) {
            return null;
          }
          var obj = JSON.parse(text);
          return obj && typeof obj.content === 'string' ? obj : null;
        })
        .catch(function () {
          return null;
        });
  }

  /**
   * Serialize a doc page the way showPage() (doc.js) wants it. It reads response.data[...] for
   * every <ul class="test"> in the content; the exported pages have none, but a missing `data`
   * would turn that into a TypeError, so an empty one is always present.
   * @param {Object} page parsed page
   * @returns {string} JSON body
   */
  function docBody(page) {
    if (!page.data || typeof page.data !== 'object') {
      page.data = {};
    }
    return JSON.stringify(page);
  }

  /**
   * The "nothing found" answer (what the servlet shows for an unknown page or empty search).
   * @param {?Object} index normalized search index, for its template text
   * @returns {string} JSON body
   */
  function nothingFoundBody(index) {
    var text = (index && index.template && index.template.nothingFound) || DEFAULT_NOTHING_FOUND;
    return JSON.stringify({content: text, data: {}});
  }

  /**
   * Turn whatever search-index.json holds into one internal form. The documented format is
   * {names, pageNames, singles, oneCharFallbacks, template}; to be tolerant of other exports an
   * array of names, an array of {name, path|file|url}, or a {name: path} object also work. The
   * `strict` flag marks the documented format (the servlet's matching rules apply); otherwise
   * matching is exact, then case-insensitive, then prefix.
   * @param {*} raw parsed JSON
   * @returns {?{names: string[], paths: Object, pageNames: ?Set, singles: ?Set,
   *   oneChar: ?Set, template: ?Object, strict: boolean}} index, or null if unusable
   */
  function normalizeIndex(raw) {
    var index = {names: [], paths: {}, pageNames: null, singles: null, oneChar: null,
      template: null, strict: false};

    /** Add one entry that may be a plain name or an object describing one. */
    function addEntry(entry) {
      if (typeof entry === 'string') {
        index.names.push(entry);
      } else if (entry && typeof entry === 'object') {
        var name = entry.name || entry.title || entry.id;
        if (typeof name === 'string') {
          index.names.push(name);
          var path = entry.path || entry.file || entry.url;
          if (typeof path === 'string') {
            index.paths[name] = path;
          }
        }
      }
    }

    if (Array.isArray(raw)) {
      raw.forEach(addEntry);
    } else if (raw && Array.isArray(raw.names)) {
      raw.names.forEach(addEntry);
      index.pageNames = Array.isArray(raw.pageNames) ? new Set(raw.pageNames) : null;
      index.singles = Array.isArray(raw.singles) ? new Set(raw.singles) : null;
      index.oneChar = Array.isArray(raw.oneCharFallbacks) ? new Set(raw.oneCharFallbacks) : null;
      index.template = raw.template && typeof raw.template === 'object' ? raw.template : null;
      index.strict = !!index.template;
    } else if (raw && typeof raw === 'object') {
      Object.keys(raw).forEach(function (name) {
        index.names.push(name);
        if (typeof raw[name] === 'string') {
          index.paths[name] = raw[name];
        }
      });
    } else {
      return null;
    }
    return index.names.length > 0 ? index : null;
  }

  /**
   * The search index, fetched once. A missing or unreadable file yields null (search then falls
   * back to treating the query as a page name).
   * @returns {Promise<?Object>} normalized index or null
   */
  function getIndex() {
    if (!indexPromise) {
      indexPromise = fetch(docBase() + 'search-index.json')
          .then(function (res) {
            return res.ok ? res.json() : null;
          })
          .then(normalizeIndex)
          .catch(function () {
            return null;
          });
    }
    return indexPromise;
  }

  /**
   * Candidate page files for the path the doc pane asked for. The pane asks for '/' (index),
   * '/05-lists' and '/functions/Sin'; the variations (trailing slash, '.json', a missing or
   * extra 'functions/') are cheap to try and make hand-typed paths work too.
   * @param {string} rest path after '/ajax/doc', possibly URL-encoded
   * @returns {string[]} relative page paths without extension, in the order to try
   */
  function docCandidates(rest) {
    var p = rest;
    try {
      p = decodeURIComponent(p);
    } catch (e) {
      // keep the raw text
    }
    p = p.replace(/^\/+|\/+$/g, '').replace(/\.json$/i, '');
    if (p === '' || p.toLowerCase() === 'index') {
      return ['index'];
    }
    var other = p.indexOf('functions/') === 0 ? p.substring('functions/'.length) : 'functions/' + p;
    return [p, other];
  }

  /**
   * Fetch the first existing page of a candidate list.
   * @param {string[]} candidates relative paths without extension
   * @returns {Promise<?Object>} the page or null
   */
  function firstPage(candidates) {
    return candidates.reduce(function (chain, rel) {
      return chain.then(function (found) {
        return found || fetchDocJson(rel);
      });
    }, Promise.resolve(null));
  }

  /**
   * Answer GET /ajax/doc/<path>.
   * @param {string} rest path after '/ajax/doc'
   * @returns {Promise<string>} JSON body
   */
  function answerDocPage(rest) {
    var candidates = docCandidates(rest);
    return firstPage(candidates).then(function (page) {
      if (page) {
        return docBody(page);
      }
      // Last resort: the right page under another spelling of its name ('sin' for 'Sin').
      return getIndex().then(function (index) {
        var last = candidates[0].split('/').pop().toLowerCase();
        var match = index && index.names.filter(function (n) {
          return n.toLowerCase() === last && (!index.pageNames || index.pageNames.has(n));
        })[0];
        return (match ? fetchDocJson(cleanRel(index.paths[match] || 'functions/' + match)) : Promise.resolve(null))
            .then(function (found) {
              return found ? docBody(found) : nothingFoundBody(index);
            });
      });
    });
  }

  /**
   * Build the linked list the servlet answers a multi-match search with (README rule 2): only
   * names that have a function page get a link, and the separator follows every match except the
   * last one of the list, even if that last one has no page.
   * @param {string[]} matches matching names in index order
   * @param {Object} index normalized index
   * @returns {string} JSON body
   */
  function listBody(matches, index) {
    var t = index.template || DEFAULT_TEMPLATE;
    var html = '';
    var any = false;
    matches.forEach(function (name, i) {
      // names are plain identifiers; anything else cannot be put safely into the javascript: href
      var linkable = /^[\w$`.]+$/.test(name) && (!index.pageNames || index.pageNames.has(name));
      if (linkable) {
        html += t.item.split('{name}').join(name);
        any = true;
        if (i !== matches.length - 1) {
          html += t.separator;
        }
      }
    });
    if (!any) {
      return nothingFoundBody(index);
    }
    return JSON.stringify({content: t.listPrefix + html.replace(/\s+$/, '') + t.listSuffix, data: {}});
  }

  /**
   * Answer GET /ajax/doc/search/?query=q like AJAXSearchServlet: a prefix search over the known
   * names; several matches give a list of links, exactly one gives that function's page.
   * @param {string} query what the user typed (a trailing '*' is the pane's hint syntax and is dropped)
   * @returns {Promise<string>} JSON body
   */
  function answerSearch(query) {
    var q = String(query || '').trim().replace(/\*+$/, '');
    if (!q) {
      return getIndex().then(nothingFoundBody);
    }
    return getIndex().then(function (index) {
      if (!index) {
        // no index: at least try the query as a function name
        return firstPage(['functions/' + q, q]).then(function (page) {
          return page ? docBody(page) : nothingFoundBody(null);
        });
      }
      var lower = q.toLowerCase();
      var names = index.names;
      var matches;
      if (index.strict) {
        // the servlet's rule: lower-cased prefix match, in index order
        matches = names.filter(function (n) {
          return n.toLowerCase().indexOf(lower) === 0;
        });
      } else {
        // unknown export: prefer the most specific kind of match
        matches = names.filter(function (n) {
          return n === q;
        });
        if (matches.length === 0) {
          matches = names.filter(function (n) {
            return n.toLowerCase() === lower;
          });
        }
        if (matches.length === 0) {
          matches = names.filter(function (n) {
            return n.toLowerCase().indexOf(lower) === 0;
          });
        }
      }

      if (matches.length === 0) {
        if (index.strict && q.length === 1 && index.oneChar && index.oneChar.has(q)) {
          return fetchDocJson('search/' + q).then(function (page) {
            return page ? docBody(page) : nothingFoundBody(index);
          });
        }
        return nothingFoundBody(index);
      }
      if (matches.length > 1) {
        return listBody(matches, index);
      }

      // exactly one match: that function's page. The strict format stores the answer as
      // search/<name>.json (it differs from functions/<name>.json by the missing "Main" link).
      var name = matches[0];
      var rels = [];
      if (index.strict && index.singles && index.singles.has(name)) {
        rels.push('search/' + name);
      }
      rels.push(cleanRel(index.paths[name] || 'functions/' + name));
      if (!index.strict) {
        rels.push(name);
      }
      return firstPage(rels).then(function (page) {
        return page ? docBody(page) : nothingFoundBody(index);
      });
    });
  }

  // ===========================================================================================
  // Stubs for the server features a static site does not have
  // ===========================================================================================

  /**
   * Answers for login/logout/save/open/getworksheets. The page never needs them to work, but its
   * callbacks read particular fields, so each answer has the shape its caller expects:
   *  - login/save go through submitForm() (utils.js), which reads response.form.generalErrors and
   *    shows them inside the form - a visible "not available" instead of a TypeError.
   *  - logout: onLogout only needs a 2xx.
   *  - getworksheets: showOpen() iterates response.worksheets, so an empty list.
   *  - open: openWorksheet() would call setContent(response.content), which first CLEARS the
   *    document; a 2xx without content would wipe the user's cells. A 501 has no onFailure
   *    handler in the page, so nothing happens.
   */
  var STUBS = {
    login: function () {
      return {status: 200, body: JSON.stringify({
        result: 'error', error: UNAVAILABLE_MESSAGE,
        form: {generalErrors: [UNAVAILABLE_MESSAGE]}})};
    },
    save: function () {
      return {status: 200, body: JSON.stringify({
        result: 'error', error: UNAVAILABLE_MESSAGE,
        form: {generalErrors: [UNAVAILABLE_MESSAGE]}})};
    },
    logout: function () {
      return {status: 200, body: JSON.stringify({result: 'ok'})};
    },
    getworksheets: function () {
      return {status: 200, body: JSON.stringify({error: UNAVAILABLE_MESSAGE, worksheets: []})};
    },
    open: function () {
      return {status: 501, body: errorBody(UNAVAILABLE_MESSAGE)};
    }
  };

  // ===========================================================================================
  // Ajax.Request patch
  // ===========================================================================================

  /**
   * The stand-in for XMLHttpRequest installed as request.transport. Just enough surface for
   * Prototype's Ajax.Request/Ajax.Response: the fields they read, the header accessors, and an
   * evalJSON() (not part of XHR; some page code calls it on the transport).
   * @constructor
   */
  function LocalTransport() {
    this.readyState = 0;
    this.status = 0;
    this.statusText = '';
    this.responseText = '';
    this.responseXML = null;
    this.headers = {};
    this.onreadystatechange = null;
  }
  LocalTransport.prototype.open = function () {};
  LocalTransport.prototype.send = function () {};
  LocalTransport.prototype.setRequestHeader = function () {};
  LocalTransport.prototype.overrideMimeType = function () {};
  LocalTransport.prototype.abort = function () {};
  /** @param {string} name header name, case-insensitive @returns {?string} its value */
  LocalTransport.prototype.getResponseHeader = function (name) {
    var key = String(name).toLowerCase();
    return hasOwn.call(this.headers, key) ? this.headers[key] : null;
  };
  /** @returns {string} all headers in XHR's "name: value\r\n" form */
  LocalTransport.prototype.getAllResponseHeaders = function () {
    var headers = this.headers;
    return Object.keys(headers).map(function (k) {
      return k + ': ' + headers[k] + '\r\n';
    }).join('');
  };
  /** @param {boolean} sanitize as String#evalJSON @returns {*} the parsed responseText */
  LocalTransport.prototype.evalJSON = function (sanitize) {
    return this.responseText.evalJSON(sanitize);
  };

  /**
   * Complete a request: fill the fake transport and let Prototype run its normal completion
   * (callbacks, responders, activeRequestCount) by calling respondToReadyState(4) itself.
   * @param {Object} request the Ajax.Request instance
   * @param {{status: number, body: string}} answer the HTTP-shaped answer
   */
  function respond(request, answer) {
    if (request._complete) {
      return;
    }
    var transport = request.transport;
    transport.readyState = 4;
    transport.status = answer.status;
    transport.statusText = STATUS_TEXT[answer.status] || '';
    transport.responseText = answer.body;
    // The servlets send JSON with this header; Ajax.Response only evaluates responseJSON when
    // the content type says JSON, so leaving it out would make response.responseJSON null.
    transport.headers = {'content-type': 'application/json; charset=UTF-8'};
    try {
      request.respondToReadyState(4);
    } catch (e) {
      // respondToReadyState reports callback errors itself; this is only a last guard
    }
  }

  /**
   * Gather what the servlet's request.getParameter() would have seen: the form body (POST) and
   * the URL's query string, all strings.
   * @param {Object} request the Ajax.Request (url and method already set)
   * @param {string} bodyParams the serialized parameters Prototype computed
   * @param {Object} info parseAjaxUrl() result for the URL
   * @returns {Object} name -> string
   */
  function collectParams(request, bodyParams, info) {
    var params = {};
    if (request.method === 'post') {
      addQuery(params, request.options.postBody || bodyParams);
    }
    // for GET the parameters were already appended to the URL by request(), so info.search has them
    addQuery(params, info.search);
    return params;
  }

  /**
   * Route one local request to its handler and arrange the answer.
   * @param {Object} request the Ajax.Request
   * @param {Object} info parseAjaxUrl() result
   * @param {Object} params collected parameters
   */
  function dispatchLocal(request, info, params) {
    var name = info.name;
    if (hasOwn.call(KERNEL_ENDPOINTS, name)) {
      if (name === 'notebook') {
        // the notebook servlet answers GET and POST differently (see AJAXNotebookServlet)
        params.__method = request.method === 'get' ? 'GET' : 'POST';
      }
      enqueue(callJob(name, params, function (outcome) {
        respond(request, httpFor(name, outcome));
      }));
    } else if (name === 'doc') {
      var isSearch = info.rest.replace(/\/+$/, '') === '/search';
      (isSearch ? answerSearch(params.query) : answerDocPage(info.rest))
          .then(function (body) {
            respond(request, {status: 200, body: body});
          }, function (e) {
            respond(request, {status: 500, body: errorBody(messageOf(e))});
          });
    } else if (hasOwn.call(STUBS, name)) {
      defer(function () {
        respond(request, STUBS[name]());
      });
    } else {
      defer(function () {
        respond(request, {status: 404, body: errorBody('Not found: /ajax/' + name)});
      });
    }
  }

  /**
   * Wrap Ajax.Request.prototype.request, the method Prototype's constructor calls to start a
   * request (Ajax.Updater goes through it too). The local branch repeats what the original does
   * up to the point where it would touch the network - url/method/parameters bookkeeping and the
   * onCreate notifications that keep Ajax.activeRequestCount right - and then answers itself.
   */
  function patchAjaxRequest() {
    if (typeof Ajax === 'undefined' || !Ajax.Request || Ajax.Request.prototype.request.__symjaKernel) {
      return;
    }
    var original = Ajax.Request.prototype.request;

    var patched = function (url) {
      var info = parseAjaxUrl(url);
      if (!info) {
        return original.apply(this, arguments);
      }

      this.url = url;
      this.method = this.options.method;
      var params = typeof this.options.parameters === 'string'
          ? this.options.parameters
          : Object.toQueryString(this.options.parameters);

      // same handling of "put"/"delete" as Prototype: tunnel through POST with _method
      if (this.method !== 'get' && this.method !== 'post') {
        params += (params ? '&' : '') + '_method=' + this.method;
        this.method = 'post';
      }
      if (params && this.method === 'get') {
        this.url += (this.url.indexOf('?') >= 0 ? '&' : '?') + params;
      }
      this.parameters = params.toQueryParams();
      this.body = this.method === 'post' ? (this.options.postBody || params) : null;
      this.transport = new LocalTransport();

      try {
        var response = new Ajax.Response(this);
        if (this.options.onCreate) {
          this.options.onCreate(response);
        }
        Ajax.Responders.dispatch('onCreate', this, response);
      } catch (e) {
        this.dispatchException(e);
        return;
      }

      dispatchLocal(this, parseAjaxUrl(this.url), collectParams(this, params, parseAjaxUrl(this.url)));
    };
    patched.__symjaKernel = true;
    Ajax.Request.prototype.request = patched;
  }

  // ===========================================================================================
  // XMLHttpRequest patch: uploads to /ajax/upload/
  // ===========================================================================================

  /**
   * Define a read-only instance value that shadows the native XHR accessor of the same name.
   * @param {Object} xhr the XHR instance
   * @param {string} name property name
   * @param {*} value what reading it returns
   */
  function shadow(xhr, name, value) {
    Object.defineProperty(xhr, name, {value: value, configurable: true, writable: true});
  }

  /**
   * Remove the shadows again so the instance behaves natively when it is re-opened for a
   * different URL.
   * @param {Object} xhr the XHR instance
   */
  function unshadow(xhr) {
    ['readyState', 'status', 'statusText', 'responseText', 'response', 'responseURL']
        .forEach(function (name) {
          if (hasOwn.call(xhr, name)) {
            delete xhr[name];
          }
        });
  }

  /**
   * Fire an event on an XHR, as a progress event when the browser has them.
   * @param {Object} xhr the XHR instance
   * @param {string} type 'readystatechange' | 'load' | 'loadend' | 'error' | 'abort'
   */
  function fire(xhr, type) {
    var event;
    try {
      event = typeof ProgressEvent === 'function' ? new ProgressEvent(type) : new Event(type);
    } catch (e) {
      event = new Event(type);
    }
    xhr.dispatchEvent(event);
  }

  /**
   * Complete a diverted upload: set the shadowed response fields and fire the events a finished
   * XHR fires (readystatechange, then load or error, then loadend). `onload` handlers set by the
   * page are real event-handler attributes of the native class, so dispatchEvent runs them.
   * @param {Object} xhr the XHR instance
   * @param {?number} status HTTP status, or null for a network-level failure (event 'error')
   * @param {string} body response text
   */
  function finishUpload(xhr, status, body) {
    if (xhr.__kernelLocal.aborted) {
      return;
    }
    shadow(xhr, 'readyState', 4);
    shadow(xhr, 'status', status == null ? 0 : status);
    shadow(xhr, 'statusText', status == null ? '' : (STATUS_TEXT[status] || ''));
    shadow(xhr, 'responseText', status == null ? '' : body);
    shadow(xhr, 'response', status == null ? '' : body);
    fire(xhr, 'readystatechange');
    fire(xhr, status == null ? 'error' : 'load');
    fire(xhr, 'loadend');
  }

  /**
   * Whether an XHR open() is the page's file upload.
   * @param {string} method HTTP method
   * @param {string} url target URL
   * @returns {boolean} true for POST /ajax/upload/
   */
  function isUpload(method, url) {
    var info = parseAjaxUrl(url);
    return !!info && info.name === 'upload' && String(method).toUpperCase() === 'POST';
  }

  /**
   * Replace window.XMLHttpRequest by a subclass that diverts uploads. Subclassing (rather than
   * wrapping) keeps every native property, constant and event working for all other requests;
   * for a diverted one the native object is simply never opened, and the few response fields the
   * page reads are shadowed on the instance.
   */
  function patchXhr() {
    var Native = window.XMLHttpRequest;
    if (typeof Native !== 'function' || Native.__symjaKernel) {
      return;
    }

    var KernelXHR = class extends Native {
      /** open(): diverts POST /ajax/upload/, everything else is native. */
      open(method, url) {
        this.__kernelLocal = null;
        unshadow(this);
        if (isUpload(method, url)) {
          this.__kernelLocal = {aborted: false};
          shadow(this, 'readyState', 1);
          return undefined;
        }
        return super.open.apply(this, arguments);
      }

      /** send(): for an upload, read the 'file' part and hand it to the kernel. */
      send(body) {
        var local = this.__kernelLocal;
        if (!local) {
          return super.send.apply(this, arguments);
        }
        var xhr = this;
        var file = body && typeof body.get === 'function' ? body.get('file') : null;
        if (!file || typeof file.arrayBuffer !== 'function') {
          defer(function () {
            finishUpload(xhr, 200, errorBody('No file posted!'));
          });
          return undefined;
        }
        file.arrayBuffer().then(function (buffer) {
          // Int8Array because the worker hands Java a byte[] (signed bytes) without conversion
          enqueue(uploadJob(file.name, new Int8Array(buffer), function (outcome) {
            if (outcome.status === 'ok') {
              finishUpload(xhr, 200, outcome.body == null ? '' : String(outcome.body));
            } else if (outcome.status === 'unavailable') {
              finishUpload(xhr, null, '');
            } else {
              finishUpload(xhr, 500, errorBody(outcome.status === 'stopped'
                  ? 'Upload interrupted: ' + STOPPED_MESSAGE : outcome.error));
            }
          }));
        }, function (e) {
          finishUpload(xhr, 500, errorBody('Cannot read the file: ' + messageOf(e)));
        });
        return undefined;
      }

      /** setRequestHeader(): headers mean nothing to a local answer (and the native one would throw). */
      setRequestHeader() {
        return this.__kernelLocal ? undefined : super.setRequestHeader.apply(this, arguments);
      }

      /** abort(): a diverted upload just stops reporting; the kernel still stores the file. */
      abort() {
        if (this.__kernelLocal) {
          this.__kernelLocal.aborted = true;
          fire(this, 'abort');
          return undefined;
        }
        return super.abort.apply(this, arguments);
      }

      /** getResponseHeader(): JSON, like the servlet. */
      getResponseHeader(name) {
        if (this.__kernelLocal) {
          return String(name).toLowerCase() === 'content-type' ? 'application/json; charset=UTF-8' : null;
        }
        return super.getResponseHeader.apply(this, arguments);
      }

      /** getAllResponseHeaders(): see getResponseHeader. */
      getAllResponseHeaders() {
        return this.__kernelLocal ? 'content-type: application/json; charset=UTF-8\r\n'
            : super.getAllResponseHeaders.apply(this, arguments);
      }
    };
    KernelXHR.__symjaKernel = true;
    window.XMLHttpRequest = KernelXHR;
  }

  // ===========================================================================================
  // Downloads: <a href="/ajax/download/?name=...">
  // ===========================================================================================

  /**
   * Save bytes as a file: Blob -> object URL -> temporary <a download> -> click. The URL is
   * revoked after a minute; revoking at once can cancel the download in some browsers.
   * @param {Int8Array|Uint8Array|ArrayBuffer} bytes file content
   * @param {string} fileName suggested name
   */
  function saveBytes(bytes, fileName) {
    var blob = new Blob([bytes], {type: 'application/octet-stream'});
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.setTimeout(function () {
      URL.revokeObjectURL(url);
    }, 60000);
  }

  /**
   * Find the anchor a click landed in.
   * @param {Node} node event target
   * @returns {?Element} nearest enclosing <a href>, if any
   */
  function anchorOf(node) {
    while (node && node.nodeType === 1) {
      if (node.tagName && node.tagName.toLowerCase() === 'a' && node.getAttribute('href')) {
        return node;
      }
      node = node.parentNode;
    }
    return null;
  }

  /**
   * Capturing click handler on the document. Listening on the document (not on each link) is what
   * makes links created later - every ExportForm result - work without any registration.
   * @param {Event} event the click
   */
  function onDocumentClick(event) {
    var a = anchorOf(event.target);
    if (!a) {
      return;
    }
    var info = parseAjaxUrl(a.getAttribute('href'));
    if (!info || info.name !== 'download') {
      return;
    }
    event.preventDefault();
    var name = new URLSearchParams(info.search).get('name') || a.getAttribute('download') || '';
    // the name the user's file gets: the link's own download attribute wins, it is what the page chose
    var saveAs = a.getAttribute('download') || name || 'download';
    enqueue(downloadJob(name, function (outcome) {
      if (outcome.status === 'ok' && outcome.bytes) {
        saveBytes(outcome.bytes, saveAs);
      } else if (outcome.status === 'ok') {
        window.alert('file not found');
      } else if (outcome.status === 'stopped') {
        window.alert('Download interrupted: the kernel was stopped and restarted.');
      } else {
        window.alert('Download failed: ' + (outcome.error || 'the kernel is not available.'));
      }
    }));
  }

  // ===========================================================================================
  // UI: status chip and credit
  // ===========================================================================================

  var chip = null;       // the status element
  var dotEl = null;      // coloured state dot (styled by data-state)
  var labelEl = null;    // text
  var barEl = null;      // progress bar track
  var barFill = null;    // progress bar fill
  var buttonEl = null;   // Stop / Retry
  var timer = null;      // 1 s ticker that updates the elapsed time while busy

  /**
   * Create an element with attributes (plain DOM: this runs before the page's own helpers).
   * @param {string} tag element name
   * @param {Object} attrs attribute name -> value
   * @returns {Element} the new element
   */
  function el(tag, attrs) {
    var node = document.createElement(tag);
    Object.keys(attrs || {}).forEach(function (name) {
      node.setAttribute(name, attrs[name]);
    });
    return node;
  }

  /**
   * Make sure kernel.css is loaded. index.html is a copy of the servlet page and does not link
   * it, so it is added from here (early, so the chip never flashes unstyled).
   */
  function ensureStyles() {
    var existing = document.querySelectorAll('link[rel="stylesheet"]');
    for (var i = 0; i < existing.length; i++) {
      if (/(^|\/)kernel\.css(\?|$)/.test(existing[i].getAttribute('href') || '')) {
        return;
      }
    }
    var link = el('link', {rel: 'stylesheet', type: 'text/css', href: CSS_URL});
    (document.head || document.documentElement).appendChild(link);
  }

  /**
   * Build the chip and the credit. Idempotent, and defensive about the header: if #headerright is
   * missing the chip floats in a corner instead.
   */
  function buildUi() {
    if (chip || !document.body) {
      return;
    }
    chip = el('div', {id: 'kernelstatus', 'data-state': state, role: 'status', 'aria-live': 'polite'});
    dotEl = el('span', {'class': 'kernel-dot'});
    labelEl = el('span', {'class': 'kernel-label'});
    buttonEl = el('button', {type: 'button', 'class': 'kernel-btn'});
    buttonEl.addEventListener('click', function () {
      if (state === 'error') {
        restart();
      } else {
        stop();
      }
    });
    barEl = el('span', {'class': 'kernel-bar'});
    barFill = el('i', {});
    barEl.appendChild(barFill);
    chip.appendChild(dotEl);
    chip.appendChild(labelEl);
    chip.appendChild(buttonEl);
    chip.appendChild(barEl);

    var host = document.getElementById('headerright');
    if (host) {
      var docs = document.getElementById('doclink');
      if (docs && docs.parentNode === host) {
        host.insertBefore(chip, docs);   // left of the docs icon, right of the search field
      } else {
        host.appendChild(chip);
      }
    } else {
      chip.setAttribute('class', 'kernel-floating');
      document.body.appendChild(chip);
    }

    var credit = el('a', {id: 'kernel-credit', href: CREDIT_URL, target: '_blank', rel: 'noopener',
      title: 'The Java kernel runs in your browser on CheerpJ'});
    credit.appendChild(document.createTextNode('Built with CheerpJ'));
    document.body.appendChild(credit);

    renderChip();
  }

  /**
   * Refresh text, bar, button and the elapsed-time ticker from the current state.
   */
  function renderChip() {
    if (!chip) {
      return;
    }
    var queued = queue.length > 0 ? ' · ' + queue.length + ' queued' : '';
    var text;
    var title = '';
    var showButton = false;
    var buttonText = '';
    var buttonIcon = '';

    switch (state) {
      case 'loading':
      case 'restarting':
        text = (state === 'restarting' ? 'Kernel restarted – ' : '') +
            (progress.text || 'loading kernel…') + queued;
        title = state === 'restarting'
            ? 'The kernel was restarted; definitions made before are lost.' : 'Starting the Symja kernel';
        showButton = true;
        buttonText = 'Stop';
        buttonIcon = 'fa-stop';
        break;
      case 'busy':
        text = 'Working… ' + Math.max(0, Math.round((Date.now() - busySince) / 1000)) + ' s' + queued;
        title = 'The kernel is evaluating. Stop restarts it and loses all definitions.';
        showButton = true;
        buttonText = 'Stop';
        buttonIcon = 'fa-stop';
        break;
      case 'error':
        text = 'Kernel error: ' + lastError;
        title = lastError;
        showButton = true;
        buttonText = 'Retry';
        buttonIcon = 'fa-refresh';
        break;
      default:   // ready
        text = (mockMode ? 'Mock kernel ready' : 'Kernel ready') + (restarts > 0 ? ' (restarted)' : '') + queued;
        title = readyInfo
            ? 'Symja ' + (readyInfo.symjaVersion || '') + ', started in ' +
              Math.round((readyInfo.initMillis || 0) / 100) / 10 + ' s' +
              (restarts > 0 ? '. Restarted: earlier definitions are lost.' : '')
            : '';
        break;
    }

    chip.setAttribute('data-state', state);
    chip.setAttribute('title', title);
    labelEl.textContent = text;

    buttonEl.hidden = !showButton;
    buttonEl.setAttribute('aria-label', buttonText);
    buttonEl.textContent = '';
    if (showButton) {
      buttonEl.appendChild(el('i', {'class': 'fa ' + buttonIcon, 'aria-hidden': 'true'}));
      buttonEl.appendChild(document.createTextNode(buttonText));
    }

    if (progress.fraction == null) {
      barEl.setAttribute('class', 'kernel-bar indeterminate');
      barFill.style.width = '';
    } else {
      barEl.setAttribute('class', 'kernel-bar');
      barFill.style.width = Math.round(progress.fraction * 100) + '%';
    }

    // the elapsed seconds need a ticker while busy, and nothing may keep running afterwards
    if (state === 'busy' && !timer) {
      timer = window.setInterval(renderChip, 1000);
    } else if (state !== 'busy' && timer) {
      window.clearInterval(timer);
      timer = null;
    }
  }

  /**
   * Run `fn` when the DOM can be touched (now, if the page is already parsed).
   * @param {Function} fn what to run
   */
  function whenDomReady(fn) {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', fn);
    } else {
      fn();
    }
  }

  // ===========================================================================================
  // The page's upload prefill
  // ===========================================================================================

  /**
   * Make the cell that the upload button pre-fills read Import("name") instead of
   * SemanticImport("name") for tables (csv, tsv, xlsx, xls).
   *
   * Why here and not in upload.js: that file belongs to the server page, which keeps
   * SemanticImport. In the browser the kernel is shipped without matheclipse-dataset (fastutil,
   * icu4j, poi and arrow, about 64 MiB), so SemanticImport would come back unevaluated, while
   * Import of the same file gives a plain list. importCall() is a global function declared by
   * upload.js and called by name from uploadDone(), so replacing the global is enough. It must
   * happen after upload.js has run, which loads after this script and would overwrite an earlier
   * replacement, hence it is done on DOMContentLoaded. The wrapper only rewrites its own result
   * and falls through unchanged for anything else, so a future change to upload.js keeps working.
   */
  function patchImportCall() {
    var original = window.importCall;
    if (typeof original !== 'function') {
      return;
    }
    window.importCall = function (name) {
      var call = original.apply(this, arguments);
      return typeof call === 'string' ? call.replace(/^SemanticImport\(/, 'Import(') : call;
    };
  }

  // ===========================================================================================
  // Boot
  // ===========================================================================================

  /**
   * The public debug surface, also what the integration test drives.
   */
  window.symjaKernel = {
    /** @returns {string} loading | ready | busy | restarting | error */
    get state() { return state; },
    /** @returns {number} requests waiting (not counting the one in flight) */
    get queueLength() { return queue.length; },
    /** @returns {boolean} whether the kernel is working on a request */
    get inFlight() { return !!inflight; },
    /** @returns {boolean} whether a worker is up and has reported ready */
    get ready() { return workerReady; },
    /** @returns {boolean} whether ?kernel=mock selected the mock worker */
    get mock() { return mockMode; },
    /** @returns {number} how many times the worker was replaced */
    get restarts() { return restarts; },
    /** @returns {string} message of the last fatal error ('' if none) */
    get lastError() { return lastError; },
    /** @returns {?Object} the worker's last `ready` message */
    get info() { return readyInfo; },
    stop: stop,
    restart: restart,
    onceReady: onceReady,
    /**
     * Call a kernel endpoint directly (console testing).
     * @param {string} endpoint query|notebook|manipulate|dynamic|tracedialog
     * @param {Object} params string parameters
     * @returns {Promise<string>} the response body; rejects on failure/stop
     */
    call: function (endpoint, params) {
      return new Promise(function (resolve, reject) {
        enqueue(callJob(endpoint, params || {}, function (outcome) {
          if (outcome.status === 'ok') {
            resolve(outcome.body);
          } else {
            reject(new Error(outcome.error || outcome.status));
          }
        }));
      });
    }
  };

  patchAjaxRequest();
  patchXhr();
  document.addEventListener('click', onDocumentClick, true);
  ensureStyles();
  startWorker();
  whenDomReady(buildUi);
  whenDomReady(patchImportCall);
}());
