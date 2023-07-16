## Roots

```
Roots(polynomial-equation, var)
```

> determine the roots of a univariate polynomial equation with respect to the variable `var`.

### Examples

```
>> Roots(3*x^3-5*x^2+5*x-2==0,x)
x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)
```

### Github

* [Implementation of Roots](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RootsFunctions.java#L369) 
