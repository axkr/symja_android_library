## Verbatim

```
Verbatim(expr)
```

> prevents pattern constructs in `expr` from taking effect, allowing them to match themselves.  

### Examples

```
>> _ /. Verbatim(_)->t
t

>> x /. Verbatim[_]->t
x
```
