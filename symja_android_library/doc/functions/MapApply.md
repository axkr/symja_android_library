## MapApply

```
MapApply(head, expr)
```

or

```
head @@@ expr
```
   
> is equivalent to `Apply(head, expr, {1})`.
        
### Examples

```
>> h @@@ {{a, b}, {c, d}}
{h(a,b),h(c,d)}
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MapApply](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L272) 
