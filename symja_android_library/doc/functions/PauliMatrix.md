## PauliMatrix

``` 
PauliMatrix(n)
```

> returns `n`th Pauli spin `2x2` matrix for `n` between `0` and `4`.

See
* [Wikipedia - Pauli matrices](https://en.wikipedia.org/wiki/Pauli_matrices) 

### Examples

```
>> PauliMatrix(2) 
{{0,-I},{I,0}}
```

`PauliMatrix` has attribute `Listable`

```
>> PauliMatrix({1,2,3,4})
{{{0,1},{1,0}},{{0,-I},{I,0}},{{1,0},{0,-1}},{{1,0},{0,1}}}
```

### Implementation status

* &#x2705; - full supported