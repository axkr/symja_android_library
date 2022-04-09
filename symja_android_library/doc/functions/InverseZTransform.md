## InverseZTransform

```
InverseZTransform(x,z,n)
```

> returns the inverse Z-Transform of `x`.
 
 
See: 
* [Wikipedia - Z-transform](https://en.wikipedia.org/wiki/Z-transform) 

### Examples

```
>> InverseZTransform(f(z)+ g(z)+h(z),z,n) 
InverseZTransform(f(z),n,z)+InverseZTransform(g(z),n,z)+InverseZTransform(h(z),n,z)
```
