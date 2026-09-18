## ArrayPlot

```
ArrayPlot( matrix-of-values )  
```

> generate a rectangle image for the `matrix-of-values`.
	 
### Examples

```
>> ArrayPlot(RandomReal(1, {10, 20}))
```

`ColorRules` names the colour of particular values. The rules are applied like `Replace`, so a 
pattern on the left names every value it matches, and the first rule written wins:

```
>> ArrayPlot({{0, -I}, {I, 0}}, ColorRules -> {0 -> White, I -> Red, -I -> Green})
```

```
>> ArrayPlot({{1, -1, 2}}, ColorRules -> {_?Positive -> Red})
```

A value no rule names is painted by the colour scale as usual.

### Related terms 
[ListPlot](ListPlot.md), [ListLogPlot](ListLogPlot.md), [ListLogLogPlot](ListLogLogPlot.md), [Manipulate](Manipulate.md), [ParametricPlot](ParametricPlot.md), [Plot](Plot.md), [Plot3D](Plot3D.md)
 

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of ArrayPlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/graphics/ArrayPlot.java#L22) 
