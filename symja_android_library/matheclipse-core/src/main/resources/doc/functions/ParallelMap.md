## ParallelMap

```
ParallelMap(f, expr)
```

> applies `f` in parallel to each element of `expr`: `h(f(e1), f(e2), ...)` for `expr = h(e1, e2, ...)`.

```
ParallelMap(f, expr, levelspec)
```

> applies `f` to the parts of `expr` at the levels given by `levelspec`, as [Map](Map.md) does.

`ParallelMap` gives the same result as [Map](Map.md), except for side effects during the computation. The elements are evaluated by the kernels of [ParallelTable](ParallelTable.md): each kernel works on a copy of the expression with symbols of its own and gets a copy of the definitions of the user's symbols, so an assignment made during the computation stays on its kernel.

A level specification, `Heads -> True`, a held head like `Hold(...)`, an association, a sparse array, an expression with less than two elements, and a platform without threads are mapped on one thread, as `Map` does it.

The options `Method`, `DistributedContexts` and `ProgressReporting` are those of [ParallelTable](ParallelTable.md).

See
* [Wikipedia - Map (higher-order function)](https://en.wikipedia.org/wiki/Map_(higher-order_function))
* [Wikipedia - Embarrassingly parallel](https://en.wikipedia.org/wiki/Embarrassingly_parallel)

### Examples

```
>> ParallelMap(f, {1, 2, 3})
{f(1),f(2),f(3)}

>> ParallelMap(#^2&, Range(10))
{1,4,9,16,25,36,49,64,81,100}

>> ParallelMap(f, g(a, b, c))
g(f(a),f(b),f(c))
```

The definitions of the user are available on the kernels:

```
>> h(0) = 1; h(n_) := n*h(n-1); ParallelMap(h, Range(0, 8))
{1,1,2,6,24,120,720,5040,40320}
```

Side effects stay on the kernels:

```
>> k = 0; ParallelMap((k++; #)&, {1, 2, 3}); k
0
```

### Related terms
[Map](Map.md), [ParallelTable](ParallelTable.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ParallelMap](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java)
