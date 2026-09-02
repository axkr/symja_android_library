# matheclipse-external

Third-party code that Symja ships itself, either because no suitable Maven artifact exists,
because the upstream artifact is much larger than what Symja needs, or because local fixes were
required. Every package keeps the license of its origin.

| Package | Origin | License |
| --- | --- | --- |
| `edu.jas..` | [Java Algebra System (JAS)](https://github.com/kredel/java-algebra-system) | LGPL / GPL |
| `tech.tablesaw.plotly..` | [Tablesaw](https://github.com/jtablesaw/tablesaw) | Apache 2.0 |
| `uk.ac.ed.ph.snuggletex..` | [SnuggleTeX](https://www2.ph.ed.ac.uk/snuggletex/) | BSD |
| `jp.ac.kobe_u.cs.cream..` | [Cream constraint solver](https://github.com/kobe-u/cream) | LGPL |
| `io.github.mangara.diophantine..` | [Diophantine](https://github.com/Mangara/Diophantine) | MIT |
| `com.baeldung.algorithms..` | Baeldung algorithm samples | MIT |
| `org.matheclipse.external.fastutil..` | [fastutil 8.5.19](https://fastutil.di.unimi.it/) | Apache 2.0 |

## `org.matheclipse.external.fastutil`

Slim replacements for the fourteen fastutil collection types that `matheclipse-core`,
`matheclipse-gpl` and `matheclipse-image` use, so that those modules no longer depend on the
6.6 MB `it.unimi.dsi:fastutil-core` artifact (3461 classes). A verbatim copy was not an option:
the transitive source closure of those fourteen types is roughly 266 files and 135 000 lines,
and keeping the original package name would have collided with the real fastutil jar that the
vendored `tech.tablesaw` copy in `matheclipse-io` still needs.

Implemented types:

- `ints`: `IntList`, `IntArrayList`, `IntSubList`, `IntIterator`, `Int2IntMap`,
  `Int2IntOpenHashMap`, `Int2IntRBTreeMap`, `Int2ObjectMap`, `Int2ObjectAVLTreeMap`
- `longs`: `LongArrayList`, `LongOpenHashSet`, `LongIterator`
- `objects`: `ObjectSet`, `ObjectSortedSet`, `Object2IntMap`, `Object2IntOpenHashMap`,
  `Object2IntArrayMap`
- `HashCommon` (copied verbatim from fastutil)

Rules for maintaining these classes:

1. **The overload set mirrors fastutil.** In particular `remove(int)` removes by *index* and
   `rem(int)` removes by *value*; `getInt`/`get` return the *default return value* (settable with
   `defaultReturnValue`) for absent keys; `addTo` returns the *previous* value; `subList` returns a
   *view*. Changing an overload can silently rebind an existing call site.
2. **The hash-based classes must keep fastutil's table layout**, i.e. `HashCommon.mix` plus linear
   probing with the zero/null key in the extra slot. The iteration order of Symja output depends
   on it.
3. `Int2IntRBTreeMap` and `Int2ObjectAVLTreeMap` are backed by `java.util.TreeMap` instead of
   porting the balanced trees; they only have to be ascending-by-key and are used for small maps.
4. Removal through the entry-set iterator of the open hash maps is not supported and throws.
5. Every change is covered by the differential tests in
   `src/test/java/org/matheclipse/external/fastutil`, which run the same operation sequences
   against the original fastutil implementation (test-scope dependency) and compare results,
   iteration order and `toString()`.

An ArchUnit rule (`ArchUnitTests.noFastutilOutsideTablesaw` in `matheclipse-io`) keeps
`it.unimi.dsi..` out of `matheclipse-core`, `matheclipse-gpl` and `matheclipse-image`.
