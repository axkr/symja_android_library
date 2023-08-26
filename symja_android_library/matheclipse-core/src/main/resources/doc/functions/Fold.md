## Fold

```
Fold[f, x, {a, b}]
```

>  returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary length. 

### Examples
 
```
>> Fold(test, t1, {a, b, c, d})
test(test(test(test(t1,a),b),c),d)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Fold](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L3160) 
