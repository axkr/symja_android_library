## Permutations

```
Permutations(list)
```

> gives all possible orderings of the items in `list`.
     
```	 
Permutations(list, n)
```

> gives permutations up to length `n`.
		
```
Permutations(list, {n})
```

> finds a list of all possible permutations containing exactly `n` elements.
	
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> Permutations({a, b, c})   
{{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}}  

>> Permutations({1, 2, 3}, 2)
{{},{1},{2},{3},{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}

>> Permutations({a, b, c}, {2})  
{{a,b},{a,c},{b,a},{b,c},{c,a},{c,b}}
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Permutations](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L2087) 
