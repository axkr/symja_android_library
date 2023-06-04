## Adjugate

``` 
Adjugate(matrix)
```

> calculate the adjugate matrix `Inverse(matrix)*Det(matrix)`

See:
* [Wikipedia - Adjugate matrix](https://en.wikipedia.org/wiki/Adjugate_matrix)

### Examples

The sparse array can be converted to an adjacency matrix in `List` format with the `Normal` function:

```
>> Adjugate({{-3,2,-5}, {-1,0,-2}, {3,-4,1}}) 
{{-8,18,-4},
 {-5,12,-1},
 {4,-6,2}}
```
