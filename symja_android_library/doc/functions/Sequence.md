## Sequence

```
Sequence[x1, x2, ...]
```

> represents a sequence of arguments to a function.

### Examples

`Sequence` is automatically spliced in, except when a function has attribute `SequenceHold` (like assignment functions).

```
>> f(x, Sequence(a, b), y)
f(x,a,b,y)

>> Attributes(Set)
{HoldFirst,Protected,SequenceHold}

>> a = Sequence(b, c);

>> a
Sequence(b,c)
```

Apply `Sequence` to a list to splice in arguments:

```
>> lst = {1, 2, 3};

>> f(Sequence @@ lst)
f(1,2,3)
```

Inside `Hold` or a function with a held argument, `Sequence` is spliced in at the first level of the argument:

```
>> Hold(a, Sequence(b, c), d)
Hold(a,b,c,d)
```

If `Sequence` appears at a deeper level, it is left unevaluated:

```
>> Hold({a, Sequence(b, c), d})
Hold({a,Sequence(b,c),d})
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Sequence](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1694) 
