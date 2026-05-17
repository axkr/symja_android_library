## HodgeDual

```
HodgeDual(tensor,dimensions,slots)
```

> `HodgeDual` evaluates the Hodge star of a tensor. 

See
* [Wikipedia - Hodge star operator](https://en.wikipedia.org/wiki/Hodge_star_operator) 

### Examples

```
>> m3 = {{{1,0},{0,1},{0,0}},{{0,0},{0,1},{1,0}}}; Normal@HodgeDual(m3, 3, {2})
{{{{0,0},{0,0}},{{0,0},{1,0}},{{0,-1},{0,-1}}},{{{0,0},{-1,0}},{{0,0},{0,0}},{{1,0},{0,0}}},{{{0,1},{0,1}},{{-1,0},{0,0}},{{0,0},{0,0}}}}
```
