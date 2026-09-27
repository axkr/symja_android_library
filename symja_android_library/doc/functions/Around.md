## Around

```
Around(x, delta)
```

> represents an approximate number `x` with the uncertainty `delta`.

```
Around(x, {delta-, delta+})
```

> represents an approximate number `x` with the downward uncertainty `delta-` and the upward uncertainty `delta+`.

```
Around(list)
```

> the mean of `list` with its standard deviation as uncertainty.

```
Around(dist)
```

> the mean of the distribution `dist` with its standard deviation as uncertainty.

Arithmetic and elementary functions propagate the uncertainty to first order.

In `Graphics`, `Graphics3D`, `ListPlot`, `ListLinePlot`, `ListPointPlot3D` and `ListPlot3D` a coordinate given as `Around(x, delta)` is drawn at `x` with interval markers from `x - delta` to `x + delta`, see [IntervalMarkers](IntervalMarkers.md).

See
* [Wikipedia - Propagation of uncertainty](https://en.wikipedia.org/wiki/Propagation_of_uncertainty)

### Examples

```
>> Around(2, 0.1)
Around(2.0,0.1)

>> Around(2, {0.1, 0.3})
Around(2.0,{0.1,0.3})

>> Around(1, 0.1) + Around(2, 0.2)
Around(3.0,0.223607)

>> Sin(Around(1, 0.1))
Around(0.841471,0.0540302)

>> Around({1, 2, 3, 4})
Around(2.5,1.29099)
```

Error bars in a plot:

```
>> ListPlot(Table(Around(Sin(k/2), 0.1), {k, 0, 12}))
```

### Related terms
[AroundReplace](AroundReplace.md), [IntervalMarkers](IntervalMarkers.md), [IntervalMarkersStyle](IntervalMarkersStyle.md), [MeanAround](MeanAround.md), [VectorAround](VectorAround.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Around](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AroundFunctions.java)
