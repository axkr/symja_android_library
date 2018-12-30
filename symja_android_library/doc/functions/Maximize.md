## Maximize

```
Maximize(unary-function, variable) 
```

> returns the maximum of the unary function for the given `variable`.
	
### Examples
 

```
>> Maximize(x^4+7*x^3-2*x^2 + 42, x) 
{{42+7*(-21/8-Sqrt(505)/8)^3-2*(21/8+Sqrt(505)/8)^2+(21/8+Sqrt(505)/8)^4,x->-21/8-Sqrt(505)/8},{42-2*(21/8-Sqrt(505)/8)^2+(21/8-Sqrt(505)/8)^4+7*(-21/8+Sqrt(505)/8)^3,x->-21/8+Sqrt(505)/8}}
```