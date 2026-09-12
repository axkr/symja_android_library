package org.matheclipse.core.expression;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import org.matheclipse.core.basic.Config;

/**
 * Counters for how argument lists are allocated and how often they outgrow their initial capacity.
 *
 * <p>
 * Collection is off unless {@link Config#AST_ALLOCATION_STATISTICS} is enabled, so a disabled
 * counter costs one static boolean read. While it is enabled every recorded event walks the stack
 * to find its call site, which is expensive - this is a census tool, not something to leave on.
 *
 * <p>
 * The counters keep three things apart which are easy to conflate:
 * <ul>
 * <li><b>compaction</b> - {@link HMArrayList#growAtEnd(int)} and {@code growAtFront} can often make
 * room by sliding the used window inside the existing array. That branch allocates nothing, and no
 * capacity hint can remove it: it is caused by removals at the front, not by under-hinting.</li>
 * <li><b>reallocation</b> - the other branch, which allocates a larger array and copies. This is
 * the only growth a better capacity hint can avoid.</li>
 * <li><b>creation</b> - the requested capacity at {@link F#ast(org.matheclipse.core.interfaces.IExpr, int)},
 * and which representation it selected. Above {@link Config#MIN_LIMIT_PERSISTENT_LIST} the hint is
 * discarded by {@link ASTRRBTree}, so only creations below it can be tuned at all.</li>
 * </ul>
 *
 * <p>
 * <b>What this cannot tell you.</b> A list which was hinted too <i>generously</i> never reallocates,
 * so it is invisible here. The hint histogram shows what was asked for and the growth counters show
 * where that was too little, but the two cannot be joined per instance - answering "this site hints
 * 8 and ends at 41" needs a field on every list, which is not worth shipping. Measure over
 * allocation with a JFR allocation profile or a heap census instead.
 *
 * <p>
 * Enable with <code>-Dsymja.astAlloc.stats=true</code>, and optionally dump the summary at JVM exit
 * to a file with <code>-Dsymja.astAlloc.stats.file=&lt;path&gt;</code>.
 */
public final class AstAllocationStats {

  /** Growth because elements were appended at the end. */
  public static final int AT_END = 0;

  /** Growth because elements were inserted at the front. */
  public static final int AT_FRONT = 1;

  /** Growth to make room in the middle. This path has no compaction branch, it always allocates. */
  public static final int FOR_INSERT = 2;

  private static final String[] KIND_NAMES = {"atEnd", "atFront", "forInsert"};

  private static final int KINDS = KIND_NAMES.length;

  /**
   * Buckets for a power of two histogram. Index <code>i</code> counts the values in
   * <code>[2^(i-1), 2^i)</code>, index 0 counts <code>0</code>. 40 is past any capacity
   * {@link Config#MAX_AST_SIZE} allows.
   */
  private static final int BUCKETS = 40;

  private static final AtomicLongArray REALLOCATIONS = new AtomicLongArray(KINDS);

  private static final AtomicLongArray COMPACTIONS = new AtomicLongArray(KINDS);

  /** Elements copied by reallocations, i.e. the work a correct hint would have avoided. */
  private static final AtomicLong REALLOCATED_ELEMENTS = new AtomicLong();

  /** Slots allocated by reallocations, i.e. the garbage a correct hint would have avoided. */
  private static final AtomicLong REALLOCATED_SLOTS = new AtomicLong();

  private static final AtomicLong CREATED_ARRAY = new AtomicLong();

  private static final AtomicLong CREATED_RRB = new AtomicLong();

  private static final AtomicLongArray HINT_ARRAY = new AtomicLongArray(BUCKETS);

  private static final AtomicLongArray HINT_RRB = new AtomicLongArray(BUCKETS);

  /** Size at which a list was reallocated. */
  private static final AtomicLongArray REALLOCATED_AT_SIZE = new AtomicLongArray(BUCKETS);

  /** Lists allocated exact fit by {@code HMArrayList(IExpr, IExpr...)}, i.e. with zero slack. */
  private static final AtomicLong CREATED_EXACT = new AtomicLong();

  private static final AtomicLongArray EXACT_SIZE = new AtomicLongArray(BUCKETS);

  private static final AtomicLong COPIED_TO_ARRAY = new AtomicLong();

  private static final AtomicLong COPIED_TO_RRB = new AtomicLong();

  private static final AtomicLong COPIED_RRB_TO_RRB = new AtomicLong();

  private static final AtomicLong COPIED_ELEMENTS = new AtomicLong();

  /** Site to {events, elementsCopied, maxSize}. */
  private static final ConcurrentHashMap<String, AtomicLongArray> GROWTH_SITES =
      new ConcurrentHashMap<String, AtomicLongArray>();

  /** Site to {events, summedHint, rrbEvents}. */
  private static final ConcurrentHashMap<String, AtomicLongArray> CREATION_SITES =
      new ConcurrentHashMap<String, AtomicLongArray>();

  private static final StackWalker WALKER = StackWalker.getInstance();

  /** How many frames outside this package identify a site. */
  private static final int SITE_FRAMES = 3;

  static {
    if (Config.AST_ALLOCATION_STATISTICS) {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> dump()));
    }
  }

  private AstAllocationStats() {}

  /**
   * A list made room without allocating, by sliding its used window inside the existing array.
   *
   * @param kind {@link #AT_END} or {@link #AT_FRONT}
   */
  public static void compaction(int kind) {
    if (Config.AST_ALLOCATION_STATISTICS) {
      COMPACTIONS.incrementAndGet(kind);
    }
  }

  /**
   * A list outgrew its array and had to allocate a larger one.
   *
   * @param kind {@link #AT_END}, {@link #AT_FRONT} or {@link #FOR_INSERT}
   * @param newCapacity the length of the array which was allocated
   * @param size the number of elements which were copied over
   */
  public static void reallocation(int kind, int newCapacity, int size) {
    if (Config.AST_ALLOCATION_STATISTICS) {
      REALLOCATIONS.incrementAndGet(kind);
      REALLOCATED_ELEMENTS.addAndGet(size);
      REALLOCATED_SLOTS.addAndGet(newCapacity);
      REALLOCATED_AT_SIZE.incrementAndGet(bucket(size));
      AtomicLongArray counters =
          GROWTH_SITES.computeIfAbsent(site(), k -> new AtomicLongArray(3));
      counters.incrementAndGet(0);
      counters.addAndGet(1, size);
      max(counters, 2, size);
    }
  }

  /**
   * A new appendable list was created with a capacity hint.
   *
   * @param hint the requested number of arguments
   * @param rrb <code>true</code> if the hint selected an {@link ASTRRBTree}, which discards it
   */
  public static void created(int hint, boolean rrb) {
    if (Config.AST_ALLOCATION_STATISTICS) {
      if (rrb) {
        CREATED_RRB.incrementAndGet();
        HINT_RRB.incrementAndGet(bucket(hint));
      } else {
        CREATED_ARRAY.incrementAndGet();
        HINT_ARRAY.incrementAndGet(bucket(hint));
      }
      AtomicLongArray counters =
          CREATION_SITES.computeIfAbsent(site(), k -> new AtomicLongArray(3));
      counters.incrementAndGet(0);
      counters.addAndGet(1, hint);
      if (rrb) {
        counters.incrementAndGet(2);
      }
    }
  }

  /**
   * An existing list was copied into an appendable one.
   *
   * @param elements the number of elements the copy holds
   * @param toRrb <code>true</code> if the copy is an {@link ASTRRBTree}
   * @param fromRrb <code>true</code> if the original was already an {@link ASTRRBTree}, in which
   *        case the copy shares its tree instead of rebuilding it
   */
  public static void copied(int elements, boolean toRrb, boolean fromRrb) {
    if (Config.AST_ALLOCATION_STATISTICS) {
      COPIED_ELEMENTS.addAndGet(elements);
      if (!toRrb) {
        COPIED_TO_ARRAY.incrementAndGet();
      } else if (fromRrb) {
        COPIED_RRB_TO_RRB.incrementAndGet();
      } else {
        COPIED_TO_RRB.incrementAndGet();
      }
    }
  }

  /**
   * A list was built exact fit from a head and an argument array, with no room to append. Recorded
   * separately because no capacity hint is involved: the size is known and correct, it is only the
   * absence of slack which makes the first append reallocate.
   *
   * @param slots the number of slots allocated, head included
   */
  public static void createdExact(int slots) {
    if (Config.AST_ALLOCATION_STATISTICS) {
      CREATED_EXACT.incrementAndGet();
      EXACT_SIZE.incrementAndGet(bucket(slots));
    }
  }

  private static void max(AtomicLongArray counters, int index, long candidate) {
    long current;
    while ((current = counters.get(index)) < candidate) {
      if (counters.compareAndSet(index, current, candidate)) {
        return;
      }
    }
  }

  /** Index of the power of two bucket holding <code>value</code>. */
  static int bucket(int value) {
    if (value <= 0) {
      return 0;
    }
    int index = 32 - Integer.numberOfLeadingZeros(value);
    return index < BUCKETS ? index : BUCKETS - 1;
  }

  /**
   * The innermost {@value #SITE_FRAMES} frames outside this package, which is where a capacity hint
   * would have to be fixed. One frame is not enough: a list is often created in one method and
   * filled by a helper it is handed to, and for a growth event it is the creator which is
   * actionable.
   */
  private static String site() {
    return WALKER.walk(frames -> frames //
        .filter(f -> !f.getClassName().startsWith("org.matheclipse.core.expression.")) //
        .limit(SITE_FRAMES) //
        .map(f -> simpleName(f.getClassName()) + "#" + f.getMethodName() + ":" + f.getLineNumber()) //
        .collect(Collectors.joining(" <- ")));
  }

  private static String simpleName(String className) {
    return className.substring(className.lastIndexOf('.') + 1);
  }

  public static long reallocations() {
    long total = 0;
    for (int i = 0; i < KINDS; i++) {
      total += REALLOCATIONS.get(i);
    }
    return total;
  }

  public static long compactions() {
    long total = 0;
    for (int i = 0; i < KINDS; i++) {
      total += COMPACTIONS.get(i);
    }
    return total;
  }

  public static long reallocatedElements() {
    return REALLOCATED_ELEMENTS.get();
  }

  public static long createdArray() {
    return CREATED_ARRAY.get();
  }

  public static long createdRrb() {
    return CREATED_RRB.get();
  }

  public static void reset() {
    for (int i = 0; i < KINDS; i++) {
      REALLOCATIONS.set(i, 0);
      COMPACTIONS.set(i, 0);
    }
    for (int i = 0; i < BUCKETS; i++) {
      HINT_ARRAY.set(i, 0);
      HINT_RRB.set(i, 0);
      EXACT_SIZE.set(i, 0);
      REALLOCATED_AT_SIZE.set(i, 0);
    }
    REALLOCATED_ELEMENTS.set(0);
    REALLOCATED_SLOTS.set(0);
    CREATED_ARRAY.set(0);
    CREATED_RRB.set(0);
    CREATED_EXACT.set(0);
    COPIED_TO_ARRAY.set(0);
    COPIED_TO_RRB.set(0);
    COPIED_RRB_TO_RRB.set(0);
    COPIED_ELEMENTS.set(0);
    GROWTH_SITES.clear();
    CREATION_SITES.clear();
  }

  /** One line, in the style of the other statistics classes. */
  public static String summary() {
    long created = CREATED_ARRAY.get() + CREATED_RRB.get();
    StringBuilder buf = new StringBuilder();
    buf.append("created=").append(created);
    buf.append(" array=").append(CREATED_ARRAY.get());
    buf.append(" rrb=").append(CREATED_RRB.get());
    buf.append(" exactFit=").append(CREATED_EXACT.get());
    buf.append(" reallocations=").append(reallocations());
    buf.append(" compactions=").append(compactions());
    buf.append(" reallocatedElements=").append(REALLOCATED_ELEMENTS.get());
    buf.append(" reallocatedSlots=").append(REALLOCATED_SLOTS.get());
    buf.append(" copies=")
        .append(COPIED_TO_ARRAY.get() + COPIED_TO_RRB.get() + COPIED_RRB_TO_RRB.get());
    buf.append(" copiedElements=").append(COPIED_ELEMENTS.get());
    if (created > 0) {
      buf.append(" reallocationsPerThousandCreated=")
          .append(reallocations() * 1000 / created);
    }
    return buf.toString();
  }

  /** The full census: totals, histograms and the worst call sites. */
  public static String report(int sites) {
    StringBuilder buf = new StringBuilder();
    buf.append("=== AstAllocationStats ===\n");
    buf.append(summary()).append("\n\n");

    buf.append("growth by kind\n");
    for (int i = 0; i < KINDS; i++) {
      buf.append(String.format("  %-10s reallocations=%-12d compactions=%d%n", KIND_NAMES[i],
          REALLOCATIONS.get(i), COMPACTIONS.get(i)));
    }

    buf.append("\ncapacity hint at creation (arguments, power of two buckets)\n");
    appendHistogram(buf, HINT_ARRAY, HINT_RRB, "array", "rrb");
    buf.append("\nexact fit allocations by size (slots, head included)\n");
    appendHistogram(buf, EXACT_SIZE, null, "exactFit", null);
    buf.append("\nlist size when it had to reallocate\n");
    appendHistogram(buf, REALLOCATED_AT_SIZE, null, "reallocations", null);

    appendSites(buf, "\ntop " + sites + " reallocation sites (events, elementsCopied, maxSize)\n",
        GROWTH_SITES, sites);
    appendSites(buf, "\ntop " + sites + " creation sites (events, summedHint, rrbEvents)\n",
        CREATION_SITES, sites);
    return buf.toString();
  }

  private static void appendHistogram(StringBuilder buf, AtomicLongArray first,
      AtomicLongArray second, String firstName, String secondName) {
    buf.append(String.format("  %14s %14s %14s%n", "range", firstName,
        secondName == null ? "" : secondName));
    for (int i = 0; i < BUCKETS; i++) {
      long a = first.get(i);
      long b = second == null ? 0 : second.get(i);
      if (a == 0 && b == 0) {
        continue;
      }
      String range = i == 0 ? "0" : (1 << (i - 1)) + ".." + ((1 << i) - 1);
      buf.append(String.format("  %14s %14d %14s%n", range, a,
          second == null ? "" : Long.toString(b)));
    }
  }

  private static void appendSites(StringBuilder buf, String header,
      ConcurrentHashMap<String, AtomicLongArray> map, int limit) {
    buf.append(header);
    List<Map.Entry<String, AtomicLongArray>> entries =
        new ArrayList<Map.Entry<String, AtomicLongArray>>(map.entrySet());
    entries.sort(Comparator.comparingLong((Map.Entry<String, AtomicLongArray> e) -> //
    e.getValue().get(0)).reversed());
    long total = 0;
    for (Map.Entry<String, AtomicLongArray> entry : entries) {
      total += entry.getValue().get(0);
    }
    long shown = 0;
    for (int i = 0; i < entries.size() && i < limit; i++) {
      AtomicLongArray counters = entries.get(i).getValue();
      long events = counters.get(0);
      shown += events;
      buf.append(String.format("  %10d %12d %10d  %s%n", events, counters.get(1), counters.get(2),
          entries.get(i).getKey()));
    }
    buf.append(String.format("  %d distinct sites, top %d cover %d of %d events (%d%%)%n",
        entries.size(), Math.min(limit, entries.size()), shown, total,
        total == 0 ? 0 : shown * 100 / total));
  }

  /** Print the report, and append it to <code>-Dsymja.astAlloc.stats.file</code> if that is set. */
  public static void dump() {
    String report = report(25);
    String file = System.getProperty("symja.astAlloc.stats.file");
    if (file == null) {
      System.out.println(report);
      return;
    }
    try {
      Files.write(Paths.get(file), report.getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.out.println(report);
    }
  }
}
