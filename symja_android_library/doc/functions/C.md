## C

```
C(n)
```

> represents the `n`-th constant in a solution to a differential equation.
 
### Examples

```
>> DSolve({y'(x)==y(x)+2},y(x), x)
{{y(x)->-2+E^x*C(1)}}
```

### Related terms
[DSolve](DSolve.md)