## Piecewise

```  
Piecewise({{subFunction1, subDomain1}, {subFunction2, subDomain2},...})
```   
> A piecewise-defined function is a function which is defined by multiple sub functions, each sub function applying to a certain interval of the main function's domain (a sub-domain). 

See:  
* [Wikipedia - Piecewise](http://en.wikipedia.org/wiki/Piecewise)

### Examples

```   
>> Piecewise({{-x, x<0}, {x, x>=0}})/.{{x->-3}, {x->-1/3}, {x->0}, {x->1/2}, {x->5}}
{3,1/3,0,1/2,5}
```  