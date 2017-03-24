## Variables

```
Variables[expr]
```

> gives a list of the variables that appear in the polynomial `expr`.

### Examples
```
Variables(a x^2 + b x + c)

Variables({a + b x, c y^2 + x/2})

Variables(x + Sin(y))
```