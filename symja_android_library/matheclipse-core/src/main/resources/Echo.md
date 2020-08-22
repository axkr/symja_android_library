## Echo

```
Echo(expr)
```

> prints the `expr` to the default output stream and returns `expr`.
 
```
Echo(expr, label)
```

> prints `label` before printing `expr`.

 
```
Echo(expr, label, head)
```

> prints `label` before printing `head(expr)` and returns `expr`.

### Examples

``` 
>> {Echo(f(x,y)), Print(g(a,b))}
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