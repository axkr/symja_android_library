## NExpectation

```
NExpectation(pure-function, data-set)
```

> returns the expected value of the `pure-function` for the given `data-set` numerically. 
   

See
* [Wikipedia - Expected value](https://en.wikipedia.org/wiki/Expected_value)

### Examples

```
>> NExpectation(x^2+7*x+8, Distributed(x, NormalDistribution()))
9.0
```
