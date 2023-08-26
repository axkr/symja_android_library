## ZTransform

```
ZTransform(x,n,z)
```

> returns the Z-Transform of `x`.
 
 
See: 
* [Wikipedia - Z-transform](https://en.wikipedia.org/wiki/Z-transform) 

### Examples

```
>> ZTransform(a*f(n)+ b*g(n), n, z)
a*ZTransform(f(n),n,z)+b*ZTransform(g(n),n,z)
```






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of ZTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ZTransform.java#L17) 

* [Rule definitions of ZTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/ZTransformRules.m) 
