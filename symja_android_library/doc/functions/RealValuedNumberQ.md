## RealValuedNumberQ

```
RealValuedNumberQ(expr)
```
> returns `True` if `expr` is an explicit real number with no imaginary component.

See
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number)

### Examples

```
>> RealValuedNumberQ[10]
 = True
 
>> RealValuedNumberQ[4.0]
 = True
 
>> RealValuedNumberQ[1+I]
 = False
 
>> RealValuedNumberQ[0 * I]
 = True
 
>> RealValuedNumberQ[0.0 * I]
 = False
```
