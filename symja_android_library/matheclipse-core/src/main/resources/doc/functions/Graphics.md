## Graphics

``` 
Graphics(primitives, options)
```

> represents a two-dimensional graphic.  
 

### Examples

```
>> Graphics(Table({EdgeForm({GrayLevel(0, 0.5)}), Hue((-11+q+10*r)/72, 1, 1, 0.6), Disk((8-r)*{Cos(2*Pi*q/12), Sin(2*Pi*q/12)}, (8-r)/3)}, {r,6}, {q, 12}))
-Graphics- 
```
