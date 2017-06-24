## Permutations

```
Permutations(list)
```
> finds a list of all possible permutations of `list`.
        
```
Permutations(list, {n})
```
> finds a list of all possible permutations containing exactly `n` elements.
	
See:  
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> Permutations({a, b, c})   
{{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}}  

>> Permutations({a, b, c}, {2})  
{{a,b},{a,c},{b,a},{b,c},{c,a},{c,b}}
```