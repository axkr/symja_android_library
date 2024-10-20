## MapApply

```
MapApply(head, expr)
```

or

```
head @@@ expr
```
   
> is equivalent to `Apply(head, expr, {1})`.
        
### Examples

```
>> h @@@ {{a, b}, {c, d}}
{h(a,b),h(c,d)}
```
