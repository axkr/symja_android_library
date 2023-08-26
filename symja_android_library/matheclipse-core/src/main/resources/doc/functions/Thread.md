## Thread

```
Thread(f(args)
```

> threads `f` over any lists that appear in `args`.
	
```
Thread(f(args), h)
```

> threads over any parts with head `h`. 

### Examples

```
>> Thread(f({a, b, c}))
{f(a),f(b),f(c)}
 
>> Thread(f({a, b, c}, t))
{f(a,t),f(b,t),f(c,t)}
 
>> Thread(f(a + b + c), Plus)
f(a)+f(b)+f(c)

>> Thread(Tuples({0, 1}, 2) -> {a, b, c, d}) 
{{0,0}->a,{0,1}->b,{1,0}->c,{1,1}->d}
```

Functions with attribute `Listable` are automatically threaded over lists:

```
>> {a, b, c} + {d, e, f} + g
{a+d+g,b+e+g,c+f+g} 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Thread](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L2168) 
