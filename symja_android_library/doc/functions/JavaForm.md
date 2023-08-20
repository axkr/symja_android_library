## JavaForm

```
JavaForm(expr)
```

> returns the Symja Java form of the `expr`. In Java you can use the created Symja expressions.

```
JavaForm(expr, Float)

or

JavaForm(expr, Float->True)
```

> returns the `java.lang.Math` form of the `expr`.  

See:  
* [docs.oracle.com - java.lang.Math](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html) 


### Examples

JavaForm can add the `F.` prefix for class `org.matheclipse.core.expression.F` if you set `Prefix->True`:

```
>> JavaForm(D(sin(x)*cos(x),x), Prefix->True)
"F.Plus(F.Sqr(F.Cos(F.x)),F.Negate(F.Sqr(F.Sin(F.x))))"

>> JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))
"Plus(Times(CC(0L,1L,1L,2L),Power(E,Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Power(E,Times(CI,x))))"
```

JavaForm evaluates its argument before creating the Java form:

```
>> JavaForm(D(sin(x)*cos(x),x))
"Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
```

You can use `Hold` to suppress the evaluation:

```
>> JavaForm(Hold(D(sin(x)*cos(x),x)))
"D(Times(Sin(x),Cos(x)),x)"

>> JavaForm(Hold(D(sin(x)*cos(x),x)), prefix->True)
"F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)"
```

Generate output for `java.lang.Math` `double` or `float` expressions:

```
>> JavaForm(E^3-Cos(Pi^2/x), Float) 
Math.pow(Math.E,3)-Math.cos(Math.pow(Math.PI,2)/x)
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JavaForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L427) 
