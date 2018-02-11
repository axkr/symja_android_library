## Clip

```
Clip(expr)
```

> returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.Returns `1` if `expr` is greater than `1`.
  

### Examples

```
>> Clip(Sin(Pi/7))
Sin(Pi/7)

>> Clip(Tan(E))
Tan(E)

>> Clip(Tan(2*E)
-1

>> Clip(Tan(-2*E))
1

>> Clip(x)
Clip(x)
```