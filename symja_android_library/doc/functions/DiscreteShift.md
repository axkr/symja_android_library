## DiscreteShift

```
DiscreteShift(f(var), {var, shift})
```

> `DiscreteShift` computes the shift `f(var+shift)`.
 
```
DiscreteShift(f(var), {var, shift, step})
```

> `DiscreteShift` computes the shift `f(var+shift*step)`.

### Examples

``` 
>> DiscreteShift(n^2, n)
(1+n)^2
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DiscreteShift](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DiscreteShift.java#L22) 
