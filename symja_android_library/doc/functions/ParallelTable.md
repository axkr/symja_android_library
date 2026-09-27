## ParallelTable

```
ParallelTable(expr, {imax})
```

> generates in parallel a list of `imax` copies of `expr`.

```
ParallelTable(expr, {i, imax})
```

> generates in parallel a list of the values of `expr` when `i` runs from `1` to `imax`.

```
ParallelTable(expr, {i, imin, imax})
```

> starts with `i = imin`.

```
ParallelTable(expr, {i, imin, imax, di})
```

> uses steps `di`.

```
ParallelTable(expr, {i, {i1, i2, ...}})
```

> uses the successive values `i1, i2, ...`.

```
ParallelTable(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
```

> gives a nested list. The list associated with `i` is outermost.

`ParallelTable` gives the same result as [Table](Table.md), except for side effects during the computation. The values of the outermost iterator are split into pieces which are evaluated on different threads ("kernels") at the same time; the inner iterators are evaluated as `Table` does it, so their bounds may depend on the outer variables.

Each kernel works on a copy of `expr` with symbols of its own, and gets a copy of the definitions of the symbols `expr` uses - directly, or through the definitions of other symbols. An assignment made during the computation therefore stays on the kernel which made it and is not seen afterwards.

If a `ParallelTable` cannot be parallelized - inside of another `ParallelTable`, with only one value for the outermost iterator, or on a platform without threads - it is evaluated using `Table`.

The following options can be given:

- `Method -> Automatic` - the granularity of the parallelization
  - `"CoarsestGrained"` - break the computation into as many pieces as there are kernels; the least overhead, no load balancing
  - `"FinestGrained"` - one piece for each value of the outermost iterator; the most overhead, the best load balancing
  - `"EvaluationsPerKernel" -> e` - break the computation into at most `e` pieces per kernel
  - `"ItemsPerEvaluation" -> m` - break the computation into pieces of at most `m` values
  - `Automatic` - a compromise between overhead and load balancing
- `DistributedContexts -> Automatic` - the contexts whose symbols have their definitions copied to the kernels: `Automatic` for the current context, `All`, `None`, a context name or a list of context names. A symbol whose definitions are not copied stays unevaluated on the kernels and is evaluated when the results come back.
- `ProgressReporting` - accepted and ignored.

The number of kernels is the number of available processors.

See
* [Wikipedia - Parallel computing](https://en.wikipedia.org/wiki/Parallel_computing)
* [Wikipedia - Embarrassingly parallel](https://en.wikipedia.org/wiki/Embarrassingly_parallel)

### Examples

```
>> ParallelTable(i^2, {i, 10})
{1,4,9,16,25,36,49,64,81,100}

>> ParallelTable({i, j}, {i, 3}, {j, i})
{{{1,1}},{{2,1},{2,2}},{{3,1},{3,2},{3,3}}}
```

The definitions of the user are available on the kernels:

```
>> f(n_) := Sum(k, {k, n}); ParallelTable(f(i), {i, 6})
{1,3,6,10,15,21}
```

Evaluations which take a different amount of time each are balanced best with the finest granularity:

```
>> ParallelTable(PrimeQ(2^i - 1), {i, 1, 12}, Method -> "FinestGrained")
{False,True,True,False,True,False,True,False,False,False,False,False}
```

Side effects stay on the kernels:

```
>> c = 0; ParallelTable(c++, {i, 8}); c
0
```

### Related terms
[Do](Do.md), [Table](Table.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ParallelTable](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ParallelTable.java)
