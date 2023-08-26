## Position

```
Position(expr, patt)
```

> returns the list of positions for which `expr` matches `patt`.

```
Position(expr, patt, ls) 
```

> returns the positions on levels specified by level specification `ls`.

### Examples

```
>> Position({1, 2, 2, 1, 2, 3, 2}, 2)
{{2},{3},{5},{7}} 
```

Find positions upto 3 levels deep

```
>> Position({1 + Sin(x), x, (Tan(x) - y)^2}, x, 3)
{{1,2,1},{2}} 
```

Find all powers of x

```
>> Position({1 + x^2, x y ^ 2,  4 y,  x ^ z}, x^_)
{{1,2},{4}} 
```

Use Position as an operator

```
>> Position(_Integer)({1.5, 2, 2.5})
{{2}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Position](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5004) 
