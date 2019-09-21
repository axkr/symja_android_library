## GraphQ

``` 
GraphQ(expr)
```

> test if `expr` is a graph object.


See:
* [Wikipedia - Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 

### Examples

```
>> GraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) ) 
True
				
>> GraphQ( Sin(x) ) 
False
```
