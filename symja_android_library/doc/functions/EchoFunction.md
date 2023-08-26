## EchoFunction

```
EchoFunction()[expr]
```

> operator form of the `Echo`function. Print the `expr` to the default output stream and return `expr`.

```
EchoFunction(head)[expr]
```

> prints `head(expr)` and returns `expr`.
 
```
EchoFunction(label, head)[expr]
```

> prints `label` before printing `head(expr)` and returns `expr`.


### Examples

``` 
>> {EchoFunction()[f(x,y)], Print(g(a,b))}
{f(x,y),Null}
```

prints 

```
f(x,y)
g(a,b)
```

and returns

```
{f(x,y),Null}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EchoFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IOFunctions.java#L220) 
