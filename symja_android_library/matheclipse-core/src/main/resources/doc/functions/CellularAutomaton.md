## CellularAutomaton

```
CellularAutomaton(rule-or-pure-function, initial-conndition, steps)
```

> create a list of the evolution `steps` of the cellular automaton from the `rule-or-pure-function` specification for the `initial-condition`.

See:  
* [Wikipedia - Cellular automaton](https://en.wikipedia.org/wiki/Cellular_automaton)


### Examples

```
>> CellularAutomaton(Xor(#, #5) &, {{1}, 0}, 3)
{{0,0,0,0,0,0,1,0,0,0,0,0,0},{0,0,0,0,1,0,0,0,1,0,0,0,0},{0,0,1,0,0,0,0,0,0,0,1,0,0},{1,0,0,0,1,0,0,0,1,0,0,0,1}}

>> CellularAutomaton({1008, 2, 1, 2}, {{{1}, {1}}, 0},5) 
{{0,0,0,1},{0,0,1,0},{0,0,0,0},{0,1,0,0},{0,0,1,0},{1,0,0,1}}
``` 