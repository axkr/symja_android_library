/*
 * kernel-config.js - the one place that holds every tunable of the in-browser Symja kernel.
 *
 * Where it fits: the notebook site (target/notebook-site/) runs Symja in the visitor's browser.
 * kernel.js (page side) talks to kernel-worker.js (a Web Worker that hosts CheerpJ, a Java VM in
 * WebAssembly) with the message contract of notebook-site-protocol.md. Both sides need the same
 * few facts - which CheerpJ loader, where the jars are, what the cache is called - so they live
 * here once instead of being copied into two files that would drift apart.
 *
 * Why a plain script that assigns to `self`: the file is loaded by a <script> tag in the page AND
 * by importScripts() in the worker. `self` is the global object in both (window / the worker
 * scope), so the same text works in both without a module system, which a classic worker, and
 * importScripts, do not have. The URLs are absolute paths from the site root because the site is
 * served at the origin root, and a worker resolves relative URLs against its own script URL, not
 * the page's.
 */
self.KERNEL_CONFIG = {
  /**
   * CheerpJ runtime loader. A pinned nightly, not the 5.0 release: it is 3-4x faster for Symja
   * and carries the Arrays.equals and WeakReference fixes (see cheerpj-nightly-and-static.md).
   * Pinned to an exact build so a nightly that changes behaviour cannot break the site silently;
   * only cjrtnc.leaningtech.com is a sensible host. Bump it deliberately after re-testing.
   */
  loader: 'https://cjrtnc.leaningtech.com/20261119_3844/loader.js',

  /**
   * Java version handed to cheerpjInit. A number, not a string: cheerpjInit rejects strings.
   * 21: Hipparchus 5 is compiled for Java 21 (class file version 65), which a Java 17 JVM refuses,
   * although the first cheerpjRunLibrary on Java 21 measured 4-8 s against 0.6-2.5 s on 17.
   */
  javaVersion: 21,

  /**
   * Directory of the jars on the site, with the trailing slash. The build script writes every jar
   * here under a content-hashed name (<name>-<hash8>.jar), which is what makes the Cache Storage
   * entries below safe: a changed jar has a new URL, so a cached entry can never be stale.
   */
  jarBase: '/jars/',

  /**
   * File inside jarBase that lists the hashed jar names, one per line, in classpath order (the
   * module's own jar first, so its classes win over duplicates in dependencies). It is the only
   * file the worker fetches with cache:'no-store', because it changes on every build.
   */
  classpathFile: 'classpath.txt',

  /**
   * Name of the Cache Storage cache that keeps the jars between visits. Chrome's HTTP cache was
   * measured to drop the 16 MB matheclipse-core jar, Cache Storage has no per-entry limit. The
   * "-v1" suffix lets a change of storage format discard every old entry at once (bump it).
   */
  cacheName: 'symja-jars-v1',

  /**
   * Where the documentation JSON files live (index.json, search-index.json, <path>.json). Used by
   * kernel.js, never by the worker: the docs are answered without any Java.
   */
  docBase: '/doc-json/',

  /**
   * Fully qualified Java class of the kernel entry point. Its static methods start(), call(),
   * upload(), download() and reset() are what kernel-worker.js invokes through CheerpJ.
   */
  kernelClass: 'org.matheclipse.web.NotebookKernel',

  /**
   * Version of this config / message contract, for the log lines of whoever debugs a mismatch
   * between a cached page and a fresh worker. Bump it when the message contract changes.
   */
  version: 1
};
