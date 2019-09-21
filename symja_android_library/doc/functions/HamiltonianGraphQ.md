## HamiltonianGraphQ

```
HamiltonianGraphQ(graph)
```

> returns `True` if `graph` is an hamiltonian graph, and `False` otherwise.
 
See:
* [Wikipedia - Hamiltonian path](https://en.wikipedia.org/wiki/Hamiltonian_path)
* [Wikipedia - Hamiltonian path problem](https://en.wikipedia.org/wiki/Hamiltonian_path_problem)
  

### Examples

```
>> HamiltonianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))
True
```