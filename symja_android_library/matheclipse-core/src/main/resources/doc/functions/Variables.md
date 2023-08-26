## Variables

```
Variables(expr)
```

> gives a list of the variables that appear in the polynomial `expr`.

### Examples

```
>> Variables(a x^2 + b x + c)
{a,b,c,x}

>> Variables({a + b x, c y^2 + x/2})
{a,b,c,x,y}

>> Variables(x + Sin(y))
{x,Sin(y)}
```

### Related terms 
[BooleanVariables](BooleanVariables.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Variables](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L4563) 
