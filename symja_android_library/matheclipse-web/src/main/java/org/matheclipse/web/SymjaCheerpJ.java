package org.matheclipse.web;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Entry points of the feasibility spike which runs Symja in the browser on CheerpJ 5.0, a
 * WebAssembly JVM.
 *
 * <p>
 * JavaScript calls the <code>public static</code> methods of this class through CheerpJ's library
 * mode, from a Web Worker. Library mode can only pass and return simple values, so every method
 * takes {@link String} arguments and returns a JSON {@link String}; no Java object ever crosses
 * the boundary.
 *
 * <p>
 * Every public method returns normally. A failure - including an {@link Error} such as a
 * {@link StackOverflowError} from a deep evaluation - is reported inside the JSON, because an
 * exception thrown into the JavaScript side would only surface as an opaque rejected promise.
 *
 * <p>
 * JavaScript may call each method on a different Java thread than the one which called
 * {@link #init()}. Symja keeps its {@link EvalEngine} in a thread local, so the evaluating methods
 * attach the engine to the current thread before they evaluate.
 *
 * <p>
 * The JSON is written by hand: it has a handful of flat shapes, and this keeps reflection-based
 * serialization (Jackson) off the start-up path, which matters when classes are loaded over HTTP.
 */
public final class SymjaCheerpJ {

  /**
   * The fixed inputs of the benchmark, in the order JavaScript runs them. They cover the parser,
   * differentiation, integration (a table lookup and a rational function), polynomial
   * factorization, equation solving, big expansion, arbitrary precision, numerical linear algebra,
   * integer factorization and graphics output.
   */
  private static final String[] BENCHMARK_INPUTS = {"1+1", "D(Sin(x)^2,x)",
      "Integrate(x^2*Sin(x),x)", "Integrate(1/(x^3+1),x)", "Factor(x^12-1)",
      "Solve(x^3-6*x^2+11*x-6==0,x)", "Expand((x+y+z)^15)", "N(Pi,200)",
      "Eigenvalues(N({{1,2,3},{4,5,6},{7,8,10}}))", "FactorInteger(2^64+1)",
      "ExportString(Plot(Sin(x),{x,0,10}),\"SVG\")"};

  /**
   * The names of the self tests in run order. The tests which are most likely to hang the page
   * come last, so that the informational results of the others are already reported when one of
   * them hangs. arraysEqualsDouble is last of all: on CheerpJ 5.0 (Java 17) it never returned
   * (the page's watchdog fired after 180 s), and a Java call stuck in the worker cannot be
   * interrupted, so every test queued after it would be lost.
   */
  private static final String[] SELF_TEST_NAMES = {"weakReference", "availableProcessors",
      "clockInBusyLoop", "threadJoin", "futureGetTimeout", "timeConstrained", "arraysEqualsDouble"};

  /**
   * The number of characters of an evaluation result which is returned. A result such as
   * <code>Expand((x+y+z)^15)</code> or an SVG graphic is thousands of characters; copying that
   * through the Java/JavaScript boundary on every call would distort the timings the spike
   * measures. The caller learns the full size from <code>outputLength</code>.
   */
  private static final int MAX_OUTPUT_CHARS = 300;

  /** Guards {@link #evaluator} and {@link #initJson}, which are written by {@link #init()}. */
  private static final Object LOCK = new Object();

  /** The one evaluator of the single user; <code>null</code> until {@link #init()} succeeded. */
  private static ExprEvaluator evaluator;

  /** The result of the successful {@link #init()}, returned again by every later call. */
  private static String initJson;

  /** Static entry points only. */
  private SymjaCheerpJ() {}

  /**
   * Initializes Symja for one single user. The call is idempotent: after a successful
   * initialization it returns the first result again without doing any work. A failed
   * initialization is not remembered, so a later call tries again.
   *
   * @return <code>{"ok":true,"initMillis":&lt;long&gt;,"javaVersion":"&lt;java.version&gt;",
   *         "symjaVersion":"&lt;Config.VERSION&gt;"}</code>, or
   *         <code>{"ok":false,"error":"&lt;ExceptionClass&gt;: &lt;message&gt;","initMillis":
   *         &lt;long&gt;}</code> if the initialization failed
   */
  public static String init() {
    synchronized (LOCK) {
      if (evaluator != null && initJson != null) {
        return initJson;
      }
      long start = System.currentTimeMillis();
      try {
        // log4j-api alone has no logging implementation and complains about that on the first use.
        // The simple implementation is part of log4j-api itself, so naming it needs no extra jar.
        System.setProperty("log4j2.loggerContextFactory",
            "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");

        // The static initializer of F reads Config.CHEEPRJ (it skips the Android logger fix, which
        // is not meant for a browser JVM). So the flag has to be set before anything loads the class F;
        // setting it later has no effect. Keep everything which touches Symja below this line.
        Config.CHEEPRJ = true;
        // Tell JAS (the polynomial algebra library behind Factor and friends) not to
        // use its thread pool: threads are scarce and costly in a browser JVM, so it computes on
        // the calling thread.
        Config.JAS_NO_THREADS = true;
        // A browser has no file system which a session may use; keep file functions disabled.
        Config.FILESYSTEM_ENABLED = false;
        // Relaxed syntax: accept lower case function names like sin(x) as well as Sin(x).
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;

        // The rest mirrors AJAXQueryServlet.initialization() as far as it applies to one single
        // user evaluator. (COMPILE, JAVA_UNSAFE, SHARED_JVM, IOInit and the session sandbox are
        // left out: there is no Java compiler or Unsafe in the browser, no other user to share
        // the JVM with, and matheclipse-io is not a dependency of this module.)
        Config.UNPROTECT_ALLOWED = false;
        Config.MATHML_TRIG_LOWERCASE = false;
        Config.DEFAULT_ITERATION_LIMIT = 10_000;
        Config.DEFAULT_RECURSION_LIMIT = 1_024;

        // The constructor creates the engine and attaches it to this thread.
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        EvalEngine engine = exprEvaluator.getEvalEngine();
        F.initSymja();
        // The limits are per engine: the Config defaults above only apply to engines created later.
        engine.setRecursionLimit(Config.DEFAULT_RECURSION_LIMIT);
        engine.setIterationLimit(Config.DEFAULT_ITERATION_LIMIT);
        // Evaluate something trivial so that the lazy part of the start-up (parser tables, first
        // rule lookups) happens here and is counted in initMillis, not in the first real call.
        exprEvaluator.eval("1+1");
        evaluator = exprEvaluator;

        long millis = System.currentTimeMillis() - start;
        initJson = "{\"ok\":true,\"initMillis\":" + millis + ",\"javaVersion\":"
            + quote(System.getProperty("java.version")) + ",\"symjaVersion\":"
            + quote(Config.VERSION) + "}";
        return initJson;
      } catch (Throwable t) {
        // Forget a half done initialization so that the next call starts again from the top.
        evaluator = null;
        initJson = null;
        return "{\"ok\":false,\"error\":" + quote(describe(t)) + ",\"initMillis\":"
            + (System.currentTimeMillis() - start) + "}";
      }
    }
  }

  /**
   * Evaluates one Symja input string. Calls {@link #init()} first if that has not happened yet.
   *
   * @param input the Symja input, for example <code>Integrate(x^2*Sin(x),x)</code>
   * @return <code>{"ok":true,"input":..,"output":..,"outputLength":..,"millis":..}</code> where
   *         <code>output</code> is cut to 300 characters and <code>outputLength</code> is the
   *         length of the complete result; or
   *         <code>{"ok":false,"input":..,"error":"&lt;ExceptionClass&gt;: &lt;message&gt;",
   *         "millis":..}</code> if the input could not be evaluated
   */
  public static String eval(String input) {
    long start = System.currentTimeMillis();
    try {
      ExprEvaluator exprEvaluator = evaluator();
      // ExprEvaluator.eval(String) calls EvalEngine.setReset(engine) first. That attaches the
      // engine to the *current* thread and resets its state, which is what makes it safe for
      // JavaScript to call us on another Java thread than init() ran on, and keeps an aborted
      // evaluation from leaving state behind for the next one.
      IExpr result = exprEvaluator.eval(input);
      String output = result == null ? "" : result.toString();
      // Truncate, see MAX_OUTPUT_CHARS.
      String shown =
          output.length() > MAX_OUTPUT_CHARS ? output.substring(0, MAX_OUTPUT_CHARS) : output;
      return "{\"ok\":true,\"input\":" + quote(input) + ",\"output\":" + quote(shown)
          + ",\"outputLength\":" + output.length() + ",\"millis\":"
          + (System.currentTimeMillis() - start) + "}";
    } catch (Throwable t) {
      return errorJson(input, t, start);
    }
  }

  /**
   * The fixed inputs of the benchmark. Keeping them here and not in the JavaScript means the
   * desktop JVM run in {@link #main(String[])} and the browser run time exactly the same work.
   *
   * @return a JSON array of the input strings, in run order, for example
   *         <code>["1+1","D(Sin(x)^2,x)",..]</code>
   */
  public static String benchmarkInputs() {
    return toJsonArray(BENCHMARK_INPUTS);
  }

  /**
   * The names of the self tests which {@link #selfTest(String)} accepts.
   *
   * @return a JSON array of the names in run order, the tests most likely to hang last:
   *         <code>["weakReference","availableProcessors","clockInBusyLoop","threadJoin",
   *         "futureGetTimeout","timeConstrained","arraysEqualsDouble"]</code>
   */
  public static String selfTestNames() {
    return toJsonArray(SELF_TEST_NAMES);
  }

  /**
   * Runs one self test, which checks a JVM behaviour Symja depends on and which CheerpJ may
   * implement differently from a desktop JVM.
   *
   * @param name one of the names from {@link #selfTestNames()}
   * @return <code>{"name":..,"pass":true|false,"millis":..,"detail":..}</code>. An unknown name,
   *         or a test which throws, gives <code>"pass":false</code> with the reason in
   *         <code>detail</code>.
   */
  public static String selfTest(String name) {
    long start = System.currentTimeMillis();
    boolean pass = false;
    String detail;
    try {
      String[] result = runSelfTest(name);
      pass = "true".equals(result[0]);
      detail = result[1];
    } catch (Throwable t) {
      detail = describe(t);
    }
    return "{\"name\":" + quote(name) + ",\"pass\":" + pass + ",\"millis\":"
        + (System.currentTimeMillis() - start) + ",\"detail\":" + quote(detail) + "}";
  }

  /**
   * The desktop JVM baseline: the same calls JavaScript makes, as one line of JSON each, prefixed
   * with the kind of line. The output is <code>INIT &lt;json&gt;</code>, one
   * <code>EVAL &lt;json&gt;</code> per benchmark input and one <code>SELFTEST &lt;json&gt;</code>
   * per self test. Symja may print its own messages in between; those lines carry no prefix.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    try {
      System.out.println("INIT " + init());
      for (String input : BENCHMARK_INPUTS) {
        System.out.println("EVAL " + eval(input));
      }
      for (String name : SELF_TEST_NAMES) {
        System.out.println("SELFTEST " + selfTest(name));
      }
    } catch (Throwable t) {
      System.out.println("ERROR " + quote(describe(t)));
    }
    System.out.flush();
    // A self test or a Symja time limiter may leave a non-daemon thread behind, which would keep
    // the JVM from ending; the baseline run must always end.
    System.exit(0);
  }

  // ------------------------------------------------------------------------------------------

  /**
   * The initialized evaluator; initializes on first use.
   *
   * @throws IllegalStateException if the initialization failed
   */
  private static ExprEvaluator evaluator() {
    ExprEvaluator exprEvaluator = evaluator;
    if (exprEvaluator == null) {
      String json = init();
      exprEvaluator = evaluator;
      if (exprEvaluator == null) {
        throw new IllegalStateException("init failed: " + json);
      }
    }
    return exprEvaluator;
  }

  /**
   * Dispatches to one self test.
   *
   * @return <code>{"true"|"false", detail}</code>
   */
  private static String[] runSelfTest(String name) throws Exception {
    if (name == null) {
      return new String[] {"false", "unknown self test: null"};
    }
    switch (name) {
      case "arraysEqualsDouble":
        return arraysEqualsDouble();
      case "weakReference":
        return weakReference();
      case "availableProcessors":
        return new String[] {"true", String.valueOf(Runtime.getRuntime().availableProcessors())};
      case "clockInBusyLoop":
        return clockInBusyLoop();
      case "threadJoin":
        return threadJoin();
      case "futureGetTimeout":
        return futureGetTimeout();
      case "timeConstrained":
        return timeConstrained();
      default:
        return new String[] {"false", "unknown self test: " + name};
    }
  }

  /**
   * Detects a known bug of CheerpJ 5.0: <code>Arrays.equals</code> (and so
   * <code>Arrays.mismatch</code>) on <code>double[]</code> gives wrong answers there. Any
   * code which compares numeric arrays gets a wrong answer silently, without an error. Passes only if all three answers are right.
   * In the spike's first browser run (CheerpJ 5.0, Java 17) the call did not return at all, so
   * this test runs last; a hang here means "fails", not merely "slow".
   */
  private static String[] arraysEqualsDouble() {
    double[] a = {1.5, 2.5, -3.25};
    double[] b = {1.5, 2.5, -3.25};
    // differs from a in the sign of the last element
    double[] c = {1.5, 2.5, 3.25};
    boolean equalArrays = Arrays.equals(a, b);
    boolean differingArrays = Arrays.equals(a, c);
    int mismatch = Arrays.mismatch(a, b);
    boolean pass = equalArrays && !differingArrays && mismatch == -1;
    return new String[] {String.valueOf(pass), "equals(equal)=" + equalArrays
        + " (want true), equals(differing)=" + differingArrays + " (want false), mismatch(equal)="
        + mismatch + " (want -1)"};
  }

  /**
   * Informational: does the garbage collector clear a weak reference? A WebAssembly JVM may never
   * clear weak references, which would make caches built on weak references grow without bound;
   * "pass" here means only that the reference was cleared, and a failure is a warning, not a
   * blocker.
   *
   * <p>
   * The test gives the collector up to 5 seconds, in rounds of: allocate garbage, System.gc(),
   * sleep. A single System.gc() is not enough on CheerpJ: its weak references (supported for
   * Java 11+ since nightly 20261119_3844) are presumably cleared only when the browser's own
   * garbage collector runs, which System.gc() cannot force; allocation pressure and returning to
   * the event loop (the sleep) are what make it run. The detail says how long clearing took.
   */
  private static String[] weakReference() throws InterruptedException {
    Object strong = new Object();
    WeakReference<Object> ref = new WeakReference<>(strong);
    // drop the only strong reference, then ask for collections until it is cleared or time is up
    strong = null;
    long start = System.currentTimeMillis();
    int rounds = 0;
    long checksum = 0;
    while (ref.get() != null && System.currentTimeMillis() - start < 5_000L) {
      rounds++;
      // about 8 MB of short-lived garbage per round: memory pressure is what triggers a real GC
      for (int i = 0; i < 1024; i++) {
        byte[] garbage = new byte[8 * 1024];
        checksum += garbage.length;
      }
      System.gc();
      Thread.sleep(100);
    }
    long elapsed = System.currentTimeMillis() - start;
    boolean cleared = ref.get() == null;
    return new String[] {String.valueOf(cleared),
        (cleared ? "reference cleared" : "reference NOT cleared") + " after " + rounds
            + " rounds of garbage+System.gc()+sleep(100), " + elapsed + " ms (allocated "
            + checksum / (1024 * 1024) + " MB)"};
  }

  /**
   * The number of rounds of {@link #clockInBusyLoop()}. A count, not a duration, bounds the loop,
   * so the probe ends even on a JVM whose clock stands still while Java code runs.
   */
  private static final long BUSY_LOOP_ROUNDS = 1L << 26;

  /**
   * Diagnoses why futureGetTimeout can hang: does <code>System.currentTimeMillis()</code> advance
   * while the current thread computes without blocking? The safety net of futureGetTimeout (stop
   * the loop after 15 s) and every deadline check Symja makes in its evaluation loop read this
   * clock. If it only moves when the JVM returns to the JavaScript event loop, a deadline is never
   * reached inside a busy loop. The loop is bounded by a round count, so this probe always ends;
   * "pass" means the clock advanced, and the detail tells how far.
   */
  private static String[] clockInBusyLoop() {
    long first = System.currentTimeMillis();
    long nanoFirst = System.nanoTime();
    long last = first;
    int changes = 0;
    long counter = 0;
    // the clock is read every 65536 rounds, as in futureGetTimeout, so the result applies there
    for (long i = 0; i < BUSY_LOOP_ROUNDS; i++) {
      counter += i;
      if ((i & 0xFFFFL) == 0) {
        long now = System.currentTimeMillis();
        if (now != last) {
          changes++;
          last = now;
        }
      }
    }
    long millisSeen = last - first;
    long nanoMillis = (System.nanoTime() - nanoFirst) / 1_000_000L;
    // a real JVM sees the clock change many times during a loop that takes more than a few ms
    boolean pass = changes > 0;
    return new String[] {String.valueOf(pass),
        BUSY_LOOP_ROUNDS + " rounds: currentTimeMillis advanced " + millisSeen + " ms in "
            + changes + " steps; nanoTime says the loop took " + nanoMillis + " ms (checksum "
            + (counter & 0xFF) + ")"};
  }

  /**
   * Diagnoses the other half of a futureGetTimeout hang: does a second Java thread run at all
   * while the calling thread waits for it? The new thread does no busy work, it only sets a flag,
   * so preemption does not matter here; only whether CheerpJ schedules a started thread while the
   * library-mode caller blocks in <code>join</code>. <code>join(5000)</code> is bounded, so the
   * probe ends either way, unless the timed wait itself never wakes, which the page's watchdog
   * then reports as a hang.
   */
  private static String[] threadJoin() throws InterruptedException {
    final AtomicBoolean ran = new AtomicBoolean(false);
    Thread t = new Thread(() -> ran.set(true), "join-test");
    // a daemon thread, so a thread which never got to run does not keep a desktop JVM alive
    t.setDaemon(true);
    long start = System.currentTimeMillis();
    t.start();
    t.join(5_000L);
    long elapsed = System.currentTimeMillis() - start;
    boolean pass = ran.get() && !t.isAlive();
    return new String[] {String.valueOf(pass), "second thread " + (ran.get() ? "ran" : "did NOT run")
        + ", join(5s) returned after " + elapsed + " ms, thread alive=" + t.isAlive()};
  }

  /**
   * Detects whether a thread which only computes is preempted, which is what lets
   * <code>Future.get(timeout)</code> time out. A browser JVM may run all Java threads on one
   * JavaScript thread and switch only at blocking calls. Then a busy loop with no sleep and no
   * yield never gives the waiting caller a turn, <code>get(2, SECONDS)</code> blocks until the
   * loop ends on its own, and every timeout Symja builds on such a future is useless. The loop
   * ends itself after 15 seconds so that a CheerpJ without preemption finishes the test instead of
   * hanging for good (and then fails, because the caller was not woken in time).
   */
  private static String[] futureGetTimeout() throws InterruptedException {
    final AtomicBoolean loopDone = new AtomicBoolean(false);
    // a daemon thread: it must not keep the JVM alive if cancel(true) does not stop it
    ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "spin-test");
      t.setDaemon(true);
      return t;
    });
    try {
      long start = System.currentTimeMillis();
      Future<Long> future = executor.submit(() -> {
        long deadline = System.currentTimeMillis() + 15_000L;
        long counter = 0;
        // Busy loop: no sleep, no yield, nothing which would hand the CPU to another thread. It
        // ends on interruption (cancel(true)) or, as the safety net, after the hard limit; the
        // clock is only read every 65536 rounds so that reading it does not become the "yield".
        while (!Thread.currentThread().isInterrupted()) {
          counter++;
          if ((counter & 0xFFFFL) == 0 && System.currentTimeMillis() > deadline) {
            break;
          }
        }
        loopDone.set(true);
        return counter;
      });
      boolean timedOut = false;
      String other = "";
      try {
        // must time out; if the loop is not preempted this only returns when the loop is over
        future.get(2, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        timedOut = true;
      } catch (ExecutionException e) {
        other = "ExecutionException: " + e.getCause();
      }
      long elapsed = System.currentTimeMillis() - start;
      // Second observation, reported in the detail but not part of "pass": does interrupting
      // reach a running loop? Symja's own time limit relies on that.
      long cancelStart = System.currentTimeMillis();
      future.cancel(true);
      while (!loopDone.get() && System.currentTimeMillis() - cancelStart < 2_000L) {
        Thread.sleep(10);
      }
      boolean stopped = loopDone.get();
      long stopMillis = System.currentTimeMillis() - cancelStart;
      // A TimeoutException alone is not enough: on CheerpJ 5.0 (Java 17) it was thrown, but only
      // after 15 s, when the loop ended by itself and the waiting thread got its turn. A timeout
      // which fires only when the work is over is no timeout, so it must also be on time.
      boolean onTime = timedOut && elapsed < 5_000L;
      return new String[] {String.valueOf(onTime),
          "get(2s) " + (timedOut ? "threw TimeoutException" : "did NOT time out " + other)
              + " after " + elapsed + " ms; cancel(true) "
              + (stopped ? "stopped the loop within " + stopMillis + " ms"
                  : "did NOT stop the loop within 2000 ms")};
    } finally {
      executor.shutdownNow();
    }
  }

  /**
   * Detects whether Symja's own <code>TimeConstrained</code> works on this JVM without relying on
   * thread preemption. <code>While(True)</code> never ends by itself, so the result
   * <code>timedout</code> arrives only if the evaluation checks its time limit itself (or the
   * interrupt reaches it). If the test fails, a Symja evaluation in the browser cannot be stopped
   * and a runaway input hangs the worker. Must also end in well under 6 seconds for a 2 second
   * limit.
   */
  private static String[] timeConstrained() {
    long start = System.currentTimeMillis();
    IExpr result = evaluator().eval("TimeConstrained(While(True),2,\"timedout\")");
    long elapsed = System.currentTimeMillis() - start;
    String text = result == null ? "null" : result.toString();
    // the third argument is a string, which prints with quotes, where a symbol would not
    String bare = text;
    if (bare.length() >= 2 && bare.startsWith("\"") && bare.endsWith("\"")) {
      bare = bare.substring(1, bare.length() - 1);
    }
    boolean pass = "timedout".equals(bare) && elapsed < 6_000L;
    return new String[] {String.valueOf(pass), "result=" + text + " after " + elapsed + " ms"};
  }

  /** The failure JSON of {@link #eval(String)}. */
  private static String errorJson(String input, Throwable t, long start) {
    return "{\"ok\":false,\"input\":" + quote(input) + ",\"error\":" + quote(describe(t))
        + ",\"millis\":" + (System.currentTimeMillis() - start) + "}";
  }

  /** <code>ExceptionClass: message</code>, the form the error fields of the JSON use. */
  private static String describe(Throwable t) {
    return t.getClass().getSimpleName() + ": " + t.getMessage();
  }

  /** A JSON array of string literals. */
  private static String toJsonArray(String[] values) {
    StringBuilder buf = new StringBuilder("[");
    for (int i = 0; i < values.length; i++) {
      if (i > 0) {
        buf.append(',');
      }
      buf.append(quote(values[i]));
    }
    return buf.append(']').toString();
  }

  /**
   * A JSON string literal. Everything outside printable ASCII is written as a unicode escape
   * (backslash, the letter u and four hex digits), so the result is valid whatever default charset the JVM and the JavaScript bridge
   * use.
   */
  private static String quote(String s) {
    if (s == null) {
      return "null";
    }
    StringBuilder buf = new StringBuilder(s.length() + 2);
    buf.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"':
          buf.append("\\\"");
          break;
        case '\\':
          buf.append("\\\\");
          break;
        case '\n':
          buf.append("\\n");
          break;
        case '\r':
          buf.append("\\r");
          break;
        case '\t':
          buf.append("\\t");
          break;
        default:
          if (c < 0x20 || c > 0x7e) {
            buf.append(String.format("\\u%04x", (int) c));
          } else {
            buf.append(c);
          }
      }
    }
    return buf.append('"').toString();
  }
}
