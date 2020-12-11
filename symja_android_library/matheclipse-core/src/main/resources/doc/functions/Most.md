## Most

```
Most(expr)
```

> returns `expr` with the last element removed.

`Most(expr)` is equivalent to `expr[[;;-2]]`.

### Examples

```
>> Most({a, b, c})
{a,b}
 
>> Most(a + b + c)
a+b
```

Nonatomic expressions are expected.

```
>> Most(x) 
Most(x)
```