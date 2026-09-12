package org.matheclipse.io.servlet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.interfaces.ISymbol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bounded maps for the things an instance holds on behalf of a browser session.
 *
 * <p>
 * These registries used to be plain maps, pruned only by {@link SymjaSessionListener} when a
 * session ended. That is sound on a server you run yourself, where one process sees every request
 * of a session and therefore sees it expire. It is not sound behind a load balancer that may send
 * the next request of the same session to a different process, which is the arrangement on Google
 * App Engine: the session itself is shared - it lives in memcache with a Datastore copy - but
 * everything keyed by its id here is not. A session first served by instance A and thereafter by
 * instance B expires in B's session manager, so the listener fires on B and removes nothing from
 * A's maps. A's entry for it lives until A does.
 *
 * <p>
 * So an instance-local cache has to bound itself rather than wait to be told. Every registry keyed
 * by a session id is a least-recently-used map with a ceiling; the listener still prunes precisely
 * when it can, and the ceiling catches what it misses. Losing the least recently used entry costs
 * that session its definitions, which is what it would have cost anyway had its request been
 * routed to an instance that never had them.
 */
final class SessionRegistry {

  private static final Logger LOGGER = LogManager.getLogger(SessionRegistry.class);

  /**
   * The most browser sessions one instance keeps state for.
   *
   * <p>
   * Each one is an {@link org.matheclipse.core.eval.EvalEngine} with everything that session has
   * defined, so this is the number that decides how much of the instance's memory the registries
   * can take. It is settable for the same reason the sandbox quotas are: the right number depends
   * on how much memory the process was given.
   */
  static final int MAX_SESSIONS_PER_INSTANCE =
      intProperty("symja.sessions.maxPerInstance", 200);

  private SessionRegistry() {}

  /** A bounded LRU map keyed by session id, evicting silently once it is full. */
  static <V> Map<String, V> bySession(String name) {
    return bySession(name, null);
  }

  /**
   * A bounded LRU map keyed by session id.
   *
   * @param name what to call this registry in the log when it drops an entry
   * @param onEvict run for the entry being dropped, or <code>null</code> for nothing. It runs while
   *        the map's own monitor is held, so it must not reach into another registry - the session
   *        listener takes those monitors in the opposite order, and the two together would
   *        deadlock. Releasing what the entry itself owns is fine.
   */
  static <V> Map<String, V> bySession(String name, BiConsumer<String, V> onEvict) {
    return new LinkedHashMap<String, V>(16, 0.75f, true) {
      private static final long serialVersionUID = 1L;

      /**
       * Whether reaching the ceiling has been reported. An instance that is full evicts on every
       * new session, so reporting each one would say the same thing thousands of times; the
       * operator needs to know that this instance runs at its ceiling, which is one message.
       */
      private transient boolean reportedFull = false;

      @Override
      protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        if (size() <= MAX_SESSIONS_PER_INSTANCE) {
          return false;
        }
        if (!reportedFull) {
          reportedFull = true;
          // warn, not info: the library's root logger sits at warn, and an instance running at
          // its session ceiling is the thing you would want to have been told about
          LOGGER.warn(
              "{} reached its ceiling of {} sessions on this instance; from here the least "
                  + "recently used session loses its state whenever a new one arrives. Raise "
                  + "symja.sessions.maxPerInstance if the instance has the memory for it.",
              name, Integer.valueOf(MAX_SESSIONS_PER_INSTANCE));
        }
        LOGGER.debug("{}: dropping the least recently used session ({})", name, eldest.getKey());
        if (onEvict != null) {
          try {
            onEvict.accept(eldest.getKey(), eldest.getValue());
          } catch (RuntimeException rex) {
            LOGGER.warn("{}: releasing the evicted entry {} failed", name, eldest.getKey(), rex);
          }
        }
        return true;
      }
    };
  }

  /**
   * An <code>int</code> system property, or <code>defaultValue</code> if it is absent or is not a
   * positive number.
   */
  static int intProperty(String name, int defaultValue) {
    String value = System.getProperty(name);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    try {
      int parsed = Integer.parseInt(value.trim());
      return parsed > 0 ? parsed : defaultValue;
    } catch (NumberFormatException ex) {
      LOGGER.warn("{} is not a number: {} - using {}", name, value, Integer.valueOf(defaultValue));
      return defaultValue;
    }
  }

  /**
   * The most definitions one browser session may accumulate before they are dropped.
   *
   * <p>
   * The ceiling exists because a session's engine grows for as long as the session lives and
   * nothing else bounds it. The size of any single expression is already bounded - a deployment
   * that cares sets the <code>Config.MAX_*</code> ceilings - but the <em>number</em> of definitions
   * is not, and one caller working through a script can hold an arbitrary amount of an instance's
   * memory. A public demo in particular should not let one visitor do that to everyone else on the
   * instance.
   */
  static final int MAX_SYMBOLS_PER_SESSION = intProperty("symja.session.maxSymbols", 1000);

  /**
   * Drop this session's definitions if it has accumulated more than it is allowed.
   *
   * <p>
   * All of them go, not the oldest few: a definition usually means nothing without the ones it was
   * written against, so keeping an arbitrary subset would leave the session in a state its author
   * never wrote. Starting clean is the honest outcome, and saying so is the caller's job.
   *
   * <p>
   * Must be called while the session's evaluation lock is held - it mutates the context an
   * evaluation reads.
   *
   * @return how many definitions were dropped, or 0 if the session was within its limit
   */
  static int enforceDataLimit(EvalEngine engine) {
    Context global = engine.getContextPath().getGlobalContext();
    int size = global.size();
    if (size <= MAX_SYMBOLS_PER_SESSION) {
      return 0;
    }
    // the keys are copied first: removing while iterating the context's own entry set is not
    // something it promises to support
    List<String> names = new ArrayList<String>(size);
    for (Entry<String, ISymbol> entry : global.entrySet()) {
      names.add(entry.getKey());
    }
    for (String name : names) {
      global.remove(name);
    }
    LOGGER.warn("session {} reached {} definitions and was reset", engine.getSessionID(),
        Integer.valueOf(size));
    return size;
  }
}
