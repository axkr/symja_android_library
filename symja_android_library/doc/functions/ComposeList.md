## ComposeList

```
ComposeList(list-of-symbols, variable)
```

> creates a list of compositions of the symbols applied at the argument `x`.

### Examples

```
>> ComposeList({f,g,h}, x)
{x,f(x),g(f(x)),h(g(f(x)))}

```