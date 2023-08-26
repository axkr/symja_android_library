## Normalize

```
Normalize(v)
```

> calculates the normalized vector `v` as `v/Norm(v)`.

```
Normalize(z)
```

> calculates the normalized complex number `z`.

```
Normalize(v, f)
```

> calculates the normalized vector `v` and use the function `f` as the norm. Default value is the predefined function `Norm`.

See: 
* [Wikipedia - Unit vector](https://en.wikipedia.org/wiki/Unit_vector)

### Examples

```
>> Normalize({1, 1, 1, 1})
{1/2,1/2,1/2,1/2}

>> Normalize(1 + I)
(1+I)/Sqrt(2) 

>> Normalize(0)
0

>> Normalize({0})
{0}

>> Normalize({})
{}

>> Normalize({x,y}, f)
{x/f({x,y}),y/f({x,y})}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Normalize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4232) 
