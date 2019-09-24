## Quiet

```
Quiet(expr)
```

> evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during evaluation).

### Examples
 
No error is printed for the division by `0`

```
>> Quiet(1/0) 
ComplexInfinity
```