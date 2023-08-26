## MapIndexed

```
MapIndexed(f, expr) 
```

> applies `f` to each part on the first level of `expr` and appending the elements position as a list in the second argument.
	
```
MapIndexed(f, expr, levelspec)
```

> applies `f` to each level specified by `levelspec` of `expr` and appending the elements position as a list in the second argument.

### Examples

```
>> MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)
{f({f({{a,b},{c,d}},{1,1})},{1}),f({f({{u,v},{s,t}},{2,1})},{2})}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MapIndexed](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1138) 
