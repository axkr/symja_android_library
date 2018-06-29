## FindFit  

```
FindFit(list-of-data-points, function, parameters, variable)
```
 
> solve a least squares problem using the Levenberg-Marquardt algorithm.
   
See:  
* [Wikipedia - Levenberg–Marquardt algorithm](https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm) 
 
### Examples

```
>> FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, a*Log(b*x), {a, b}, x)
{a->1.54503,b->20.28258}

>> FindFit({{1,1},{2,4},{3,9},{4,16}}, a+b*x+c*x^2, {a, b, c}, x)
{a->0.0,b->0.0,c->1.0}
```