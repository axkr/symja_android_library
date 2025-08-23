## MeijerG

```
MeijerG({{a(1),a(2),...,a(n)},{a(n+1),a(n+2),...,a(p)}},{{b(1),b(2),...,b(m)},{b(m+1),b(m+2),...,b(q)}}, z)
```

> return the `MeijerG` function. The G-function was introduced by Cornelis Simon Meijer (1936) as a very general function intended to include most of the known special functions as particular cases. 

See: 
* [Wikipedia - Meijer G-function](https://en.wikipedia.org/wiki/Meijer_G-function) 

### Examples

```
>> MeijerG({{}, {}}, {{b1}, {}},z)
z^b1/E^z
```
