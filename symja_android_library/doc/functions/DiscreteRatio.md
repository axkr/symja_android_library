## DiscreteRatio

```
DiscreteRatio(f(var), var)
```

> `DiscreteRatio` computes `f(var+1)/f(var)`.
 
```
DiscreteRatio(f(var), {var, n, step})
```

> `DiscreteRatio` computes the multiple discrete ratio with step `step`.

### Examples

``` 
>> DiscreteRatio(k!, k) 
1+k
``` 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DiscreteRatio](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DiscreteRatio.java#L18) 
