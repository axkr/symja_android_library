## OneIdentity

```
OneIdentity
```

> is an attribute specifying that `f(x)` should be treated as equivalent to `x` in pattern matching.    
 
### Examples

`OneIdentity` affects pattern matching. It does not affect evaluation.
  
```
>> SetAttributes[f, OneIdentity]

>> a /. f(x_:0, u_) -> {u}
{a}
```

However, without a default argument, the pattern does not match:

```
>> a /. f(u_) -> {u}
a
```

Also, it does not affect evaluation:

```
>> f(a)
f(a)  
```
