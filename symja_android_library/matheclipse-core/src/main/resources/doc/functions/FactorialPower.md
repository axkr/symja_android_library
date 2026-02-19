## FactorialPower

```
FactorialPower(v, n)
```

> The `FactorialPower` implements the falling factorial. The falling factorial (sometimes called the descending factorial, falling sequential product, or lower factorial) is defined as the polynomial `v*(v-1)*(v-2)*...*(v-n+1)`.

See 
* [Wikipedia - Falling and rising factorials](https://en.wikipedia.org/wiki/Falling_and_rising_factorials)
 
### Examples

```
>> FactorialPower(v,4) // FunctionExpand 
(-3+v)*(-2+v)*(-1+v)*v
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of FactorialPower](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L2421) 
