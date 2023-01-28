## ArcLength

```
ArcLength(geometric-form)
```

> returns the length of the `geometric-form`.
  

See:
* [Wikipedia - Arc length](https://en.wikipedia.org/wiki/Arc_length)
 

### Examples

```
>> ArcLength(Line({{a,b},{c,d},{e,f}}))
Sqrt((a-c)^2+(b-d)^2)+Sqrt((c-e)^2+(d-f)^2)
```
