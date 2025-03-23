## Assuming

```
Assuming(assumption, expression)
```

> evaluate the `expression` with the assumptions appended to the default `$Assumptions` assumptions.

### Examples

```
>> Assuming(x>0, Simplify(Sqrt(x^2)))
x
```


### Related terms 
[$Assumptions]($Assumptions.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Assuming](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L56) 
